package com.github.mimiknight.kuca.ecology.exception;

/**
 * Request重复绑定异常
 *
 * @author MiMiKnight victor2015yhm@gmail.com
 * @since 2023-09-08 12:20:04
 */
public class RequestRepeatBindException extends RuntimeException {
    private static final long serialVersionUID = 4937610828908578505L;

    public RequestRepeatBindException() {
        super();
    }

    public RequestRepeatBindException(String message) {
        super(message);
    }

    public RequestRepeatBindException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestRepeatBindException(Throwable cause) {
        super(cause);
    }
}
