package com.fosterpet.cmlx.commons.constant;

/**
 * @Author cmlx
 * @Date 2019-7-17 0017 10:46
 * @Version 1.0
 */
public class Constant {

    /**
     * 数据状态
     */
    public enum DataState {
        Invalid,    // 删除
        Disable,    // 无效
        Available   // 有效
    }

    /**
     * token类型
     */
    public enum TokenType {
        UserDmToken(0),
        AnonymousDmToken(1);
        private Integer val;

        TokenType(Integer val) {
            this.val = val;
        }

        public Integer getVal() {
            return val;
        }

    }

}
