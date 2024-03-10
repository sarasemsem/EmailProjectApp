package com.emailProcessor.emailProcessor.configuration;

import com.emailProcessor.basedomains.dto.ErrorDto;
import com.emailProcessor.emailProcessor.controller.errors.AppExecption;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class RestExeceptionHandler {
    @ExceptionHandler(value = {AppExecption.class})
    @ResponseBody
    public ResponseEntity<ErrorDto> handleExeption(AppExecption execption){
        return ResponseEntity.status(execption.getHttpStatus())
                .body(new ErrorDto(execption.getMessage()));
    }
}
