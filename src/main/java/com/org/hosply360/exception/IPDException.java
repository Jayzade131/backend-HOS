package com.org.hosply360.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class IPDException extends RuntimeException {
    private final HttpStatus httpStatus;

    public IPDException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

}
