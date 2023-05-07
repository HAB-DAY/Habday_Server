package com.habday.server.exception;

import com.habday.server.constants.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomExceptionWithMessage extends RuntimeException{
    private final ExceptionCode exceptionCode;
    private final String describe;
}
