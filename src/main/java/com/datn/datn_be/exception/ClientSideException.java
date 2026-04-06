package com.datn.datn_be.exception;

/**
 * Custom exception for client-side errors (4xx errors)
 * This exception is thrown when the client makes an invalid request
 */
public class ClientSideException extends RuntimeException {

    private int code;
    private String message;
    private Object data;

    /**
     * Constructor with message only
     */
    public ClientSideException(String message) {
        super(message);
        this.message = message;
        this.code = 400; // Default to Bad Request
    }

    /**
     * Constructor with code and message
     */
    public ClientSideException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * Constructor with code, message, and data
     */
    public ClientSideException(int code, String message, Object data) {
        super(message);
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * Constructor with code, message, data, and cause
     */
    public ClientSideException(int code, String message, Object data, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

