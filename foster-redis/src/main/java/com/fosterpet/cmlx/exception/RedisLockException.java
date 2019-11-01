package com.fosterpet.cmlx.exception;

import org.springframework.dao.DataAccessException;

/**
 * @Desc
 * @Author cmlx
 * @Date 2019-11-1 0001 11:33
 */
public class RedisLockException extends DataAccessException {

    public RedisLockException(String msg) {
        super(msg);
    }

    public RedisLockException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
