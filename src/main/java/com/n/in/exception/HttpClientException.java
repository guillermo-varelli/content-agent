package com.n.in.exception;

public class HttpClientException extends RuntimeException {
    public HttpClientException(int statusCode, String responseBody) {
        super("HTTP error " + statusCode + ": " + responseBody);
    }

    public HttpClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
