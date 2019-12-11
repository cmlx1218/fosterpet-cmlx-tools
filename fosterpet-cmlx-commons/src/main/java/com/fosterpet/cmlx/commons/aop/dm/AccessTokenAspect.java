package com.fosterpet.cmlx.commons.aop.dm;

import com.fosterpet.cmlx.commons.constant.Constant;
import com.fosterpet.cmlx.commons.constant.DefaultConstant;
import com.fosterpet.cmlx.commons.constant.ErrorCode;
import com.fosterpet.cmlx.commons.exception.EXPF;
import com.fosterpet.cmlx.commons.support.JwtUtility;
import com.fosterpet.cmlx.commons.support.StringUtility;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @Desc token校验切面
 * @Author cmlx
 * @Date 2019-12-9 0009 17:45
 */
@Aspect
@Component
public class AccessTokenAspect {

    @Before("@annotation(accessToken)")
    public void checkToken(JoinPoint pjp, AccessToken accessToken) throws Throwable {
        String token = getToken(getRequest());
        if (StringUtility.isEmpty(token)) {
            throw EXPF.exception(ErrorCode.AUTH, false);
        }

        Constant.TokenType[] arr = accessToken.value();
        finalize();
        if (arr.length == 0) {
            throw EXPF.exception(ErrorCode.AUTH, false);
        }

        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        Annotation[] annotations = AnnotationUtils.getAnnotations(method);

        if (annotations.length > 0) {
            Integer tType = tokenType(token);
            boolean hasPermission = false;
            for (Annotation annotation : annotations) {
                if (annotations.length > 0) {
                    for (Constant.TokenType tokenType : arr) {
                        if (tokenType.getVal().equals(tType)) {
                            hasPermission = true;
                            break;
                        }
                    }
                    break;
                }
            }
            if (!hasPermission) {
                throw EXPF.exception(ErrorCode.AUTH, false);
            }
        }
    }

    private Integer tokenType(String token) throws Exception {
        String type = JwtUtility.getProperty(token, "anonymous");
        if (StringUtility.isEmpty(type)) {
            throw EXPF.exception(ErrorCode.AUTH.getCode(), "token错误", false);
        }
        return Integer.valueOf(type);
    }

    private boolean effectiveAnnotation(Annotation annotation) {
        return annotation instanceof RequestMapping || annotation instanceof PostMapping || annotation instanceof GetMapping;
    }

    private HttpServletRequest getRequest() {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        return request;
    }

    private String getToken(HttpServletRequest request) {
        String authorization = request.getHeader(DefaultConstant.OAUTH_REQ_HEADER);
        return StringUtils.substringAfter(authorization, DefaultConstant.OAUTH_REQ_HEADER);

    }
}
