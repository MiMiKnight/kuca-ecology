package com.github.mimiknight.kuca.ecology.exception;

/**
 * Handler未找到异常
 *
 * @author MiMiKnight victor2015yhm@gmail.com
 * @since 2023-07-30 12:47:42
 */
public class HandlerNotFoundException extends RuntimeException {

    public HandlerNotFoundException() {
        super();
    }


    public HandlerNotFoundException(String message) {
        super(message);
    }


    public HandlerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }


    public HandlerNotFoundException(Throwable cause) {
        super(cause);
    }
}
