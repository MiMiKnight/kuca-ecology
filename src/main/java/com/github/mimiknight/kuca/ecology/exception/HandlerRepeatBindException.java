package com.github.mimiknight.kuca.ecology.exception;

/**
 * 重复绑定Handler异常
 *
 * @author MiMiKnight victor2015yhm@gmail.com
 * @since 2023-09-08 12:20:04
 */
public class HandlerRepeatBindException extends RuntimeException {
    private static final long serialVersionUID = 4937610828908578505L;

    public HandlerRepeatBindException() {
        super();
    }

    public HandlerRepeatBindException(String message) {
        super(message);
    }

    public HandlerRepeatBindException(String message, Throwable cause) {
        super(message, cause);
    }

    public HandlerRepeatBindException(Throwable cause) {
        super(cause);
    }
}
