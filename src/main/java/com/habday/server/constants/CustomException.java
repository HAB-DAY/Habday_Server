package com.habday.server.constants;

import com.habday.server.constants.code.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {
    private final ExceptionCode exceptionCode;
}

