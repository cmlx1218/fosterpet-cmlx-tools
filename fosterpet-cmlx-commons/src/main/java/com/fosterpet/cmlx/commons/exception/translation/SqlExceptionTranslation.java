package com.fosterpet.cmlx.commons.exception.translation;

import com.fosterpet.cmlx.commons.constant.ErrorCode;
import com.fosterpet.cmlx.commons.exception.TranslationContext;
import com.fosterpet.cmlx.commons.exception.annotation.ExpTranslation;
import com.fosterpet.cmlx.commons.springextension.view.UnifyFailureView;
import org.springframework.web.servlet.view.AbstractView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Desc 数据库异常类
 * @Author cmlx
 * @Date 2019-12-9 0009 10:30
 */
@ExpTranslation
public class SqlExceptionTranslation extends AbstractExceptionTranslation{
    private static List<Integer> ignoreSqlCode;

    public SqlExceptionTranslation(){
        ignoreSqlCode = new ArrayList<>();
        ignoreSqlCode.add(1062);//悲观锁异常
        ignoreSqlCode.add(1213);//悲观锁异常
    }

    @Override
    public boolean support(Exception ex) {
        return this.rootCause(ex) instanceof SQLException;
    }

    @Override
    public AbstractView translationToJson(TranslationContext context) {
        Throwable throwable = this.rootCause(context.getException());
        SQLException sqlException = (SQLException) throwable;
        AbstractView view = new UnifyFailureView();
        if (ignoreSqlCode.contains(sqlException.getErrorCode())) {
            view.addStaticAttribute(CODE, ErrorCode.Conflict.getCode());
            view.addStaticAttribute(THROWTYPE, getThrowtype(context.getException()));
            view.addStaticAttribute(MESSAGE, sqlException.getMessage());
        } else {
            view.addStaticAttribute(CODE, ErrorCode.Server.getCode());
            view.addStaticAttribute(THROWTYPE, getThrowtype(context.getException()));
            view.addStaticAttribute(MESSAGE, "Code [" + sqlException.getErrorCode() + "] Message [" + sqlException.getMessage() + "]");
        }
        return view;
    }
}
