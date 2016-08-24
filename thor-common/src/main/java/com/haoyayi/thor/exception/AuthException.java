/*
 * Copyright 2014-2020 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.exception;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class AuthException extends RuntimeException {

    public AuthException() {
        super();
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthException(String message) {
        super(message);
    }

    public AuthException(Throwable cause) {
        super(cause);
    }

    public String getMessage() {
        return buildMessage(super.getMessage(), getCause());
    }

    public boolean contains(Class<?> exType) {
        if (exType == null) {
            return false;
        }
        if (exType.isInstance(this)) {
            return true;
        }
        Throwable cause = getCause();
        if (cause == this) {
            return false;
        }
        while (cause != null) {
            if (exType.isInstance(cause)) {
                return true;
            }
            if (cause.getCause() == cause) {
                break;
            }
            cause = cause.getCause();
        }
        return false;
    }

    protected static String buildMessage(String message, Throwable cause) {
        if (cause != null) {
            StringBuilder sb = new StringBuilder();
            if (message != null) {
                sb.append(message).append("; ");
            }
            sb.append("Nested exception is ").append(cause);
            return sb.toString();
        } else {
            return message;
        }
    }

}
