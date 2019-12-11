package com.fosterpet.cmlx.commons.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Desc 错误码实体
 * @Author cmlx
 * @Date 2019-12-6 0006 16:26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorBody {

    private int code;
    private String message;
    private String throwType;

}
