package com.emailProcessor.emailProcessor.controller.errors;

import lombok.Data;
import org.springframework.http.HttpStatus;
@Data
public class AppExecption extends RuntimeException {
    private final HttpStatus httpStatus;

    public AppExecption(String message ,HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
