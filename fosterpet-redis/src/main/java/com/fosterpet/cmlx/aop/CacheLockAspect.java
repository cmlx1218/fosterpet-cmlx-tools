package com.fosterpet.cmlx.aop;

import com.fosterpet.cmlx.annotation.CacheLock;
import com.fosterpet.cmlx.exception.RedisLockException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * @Desc
 * @Author cmlx
 * @Date 2019-11-1 0001 11:41
 */

@Slf4j
@Aspect
@Component
@Order(4)  // 执行顺序
public class CacheLockAspect {

    final int slice = 3;
    final String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    @Autowired
    RedisTemplate redisTemplate;

    @Around("@annotation(cacheLock)")
    public Object around(ProceedingJoinPoint pjp, CacheLock cacheLock) throws Throwable {
        String key = lockKey(pjp, cacheLock);
        try {
            if (tryLock(key, Thread.currentThread().getName(), cacheLock.timeOut(), cacheLock.expireTime())) {
                log.info("获取分布式锁=======>key={}", key);
                return pjp.proceed();
            }
            log.info("获取分布式锁失败=======>key={}", key);
            throw new RedisLockException("get redis cache lock failed!!");
        } finally {
            tryUnLock(key, Thread.currentThread().getName());
            log.info("释放分布式锁=======>key={}", key);
        }
    }


    /**
     * 获取分布式锁
     *
     * @param key
     * @param catchId 加锁者ID
     * @param timeOut 加锁超时 毫秒
     * @param expTime 加锁时间 毫秒
     * @return
     */
    public boolean tryLock(String key, String catchId, long timeOut, long expTime) throws InterruptedException {
        if (timeOut < 0 || expTime < 0) {
            return false;
        }
        while (timeOut > 0) {
            RedisCallback<Boolean> callback = (connection) ->
                    connection.set(key.getBytes(Charset.forName("UTF-8")), catchId.getBytes(Charset.forName("UTF-8")), Expiration.milliseconds(expTime), RedisStringCommands.SetOption.SET_IF_ABSENT);
            Boolean result = (Boolean) redisTemplate.execute(callback);
            if (result) {
                return true;
            }
            Thread.sleep(slice);
            timeOut -= slice;
        }
        return false;
    }

    /**
     * 释放分布式锁
     *
     * @param key
     * @param catchId 加锁者ID
     * @return
     */
    public boolean tryUnLock(String key, String catchId) {
        RedisCallback<Boolean> callback = (connection) ->
                connection.eval(script.getBytes(), ReturnType.BOOLEAN, 1, key.getBytes(Charset.forName("UTF-8")), catchId.getBytes(Charset.forName("UTF-8")));
        return (Boolean) redisTemplate.execute(callback);

    }


    /**
     * 组装lock key
     *
     * @param pjp
     * @param cacheLock
     * @return
     */
    public String lockKey(ProceedingJoinPoint pjp, CacheLock cacheLock) {
        StringBuilder sb = new StringBuilder();
        sb.append(cacheLock.lockedPrefix()).append(":");
        if ("".equals(cacheLock.value())) {
            sb.append(pjp.getSignature().getName());
        } else {
            sb.append(cacheLock.value());
        }

        HashMap<String, Object> params = params(pjp);

        for (String index : cacheLock.args()) {
            Object arg = params.get(index.toLowerCase());
            sb.append("_").append(convertValue(arg));
        }
        return sb.toString();
    }


    /**
     * 方法参数值转换
     *
     * @param value
     * @return
     */
    Object convertValue(Object value) {
        if (value instanceof Number) {
            return value;
        }
        if (value instanceof String) {
            return value;
        }
        return "default";
    }

    /**
     * 获取所有方法参数
     *
     * @param joinPoint
     * @return
     */
    HashMap<String, Object> params(ProceedingJoinPoint joinPoint) {
        HashMap<String, Object> params = new HashMap<>();

        Object[] paramValues = joinPoint.getArgs();
        String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();

        for (int i = 0; i < paramNames.length; i++) {
            params.put(paramNames[i].toLowerCase(), paramValues[i]);
        }
        return params;
    }
}
