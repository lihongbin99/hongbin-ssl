package io.lihongin.ssl.model.vo;

import io.lihongin.ssl.model.exception.AppException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private final static Object OBJECT = null;

    public final static Integer SUCCESS_CODE = 200;
    public final static String SUCCESS_MESSAGE = "success";
    public final static Object SUCCESS_DATA = OBJECT;

    public final static Integer ERROR_CODE = -1;
    public final static String ERROR_MESSAGE = "error";
    public final static Object ERROR_DATA = OBJECT;

    public final static Integer SEVERITY_ERROR_CODE = -2;
    public final static String SEVERITY_ERROR_MESSAGE = "error";
    public final static Object SEVERITY_ERROR_DATA = OBJECT;

    private Integer code;

    private String message;

    private T data;

    public void isSuccessElseThrowException() {
        if (code <= 0) {
            throw new AppException(this.code, this.message);
        }
    }

    public T noErrorData() {
        this.isSuccessElseThrowException();
        return data;
    }

    public static R<Object> success() {
        return success(SUCCESS_CODE, SUCCESS_MESSAGE, SUCCESS_DATA);
    }

    public static R<Object> success_m(String message) {
        return success(SUCCESS_CODE, message, SUCCESS_DATA);
    }

    public static <T> R<T> success(T data) {
        return success(SUCCESS_CODE, SUCCESS_MESSAGE, data);
    }

    public static <T> R<T> success(String message, T data) {
        return success(SUCCESS_CODE, message, data);
    }

    public static <T> R<T> success(Integer code, String message, T data) {
        return new R<T>(code, message, data);
    }

    public static R<Object> error(AppException e) {
        return new R<>(e.getCode(), e.getMessage(), ERROR_DATA);
    }

    public static R<Object> error(String message) {
        return new R<>(ERROR_CODE, message, ERROR_DATA);
    }

}
