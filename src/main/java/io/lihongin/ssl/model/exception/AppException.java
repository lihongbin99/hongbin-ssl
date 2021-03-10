package io.lihongin.ssl.model.exception;

/**
 * 自定义异常
 *
 * @author Li Hong Bin
 */
public class AppException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private Integer code;

    private String message;

    public AppException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
