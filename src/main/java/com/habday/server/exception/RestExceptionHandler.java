package com.habday.server.exception;

import com.habday.server.dto.BaseResponse;
import com.siot.IamportRestClient.exception.IamportResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(value={CustomException.class})
    protected ResponseEntity<BaseResponse> handleCustomException(CustomException e, HttpServletRequest request) {
        log.warn(String.format("[%s Error] : %s %s", e.getExceptionCode().getStatus(), request.getMethod(), request.getRequestURI()));
        return BaseResponse.toCustomErrorResponse(e.getExceptionCode());
    }

    @ExceptionHandler(value={CustomExceptionWithMessage.class})
    protected ResponseEntity<BaseResponse> handleCustomExceptionWithMessage(CustomExceptionWithMessage e, HttpServletRequest request) {
        log.warn(String.format("[%s Error] : %s %s", e.getExceptionCode().getStatus(), request.getMethod(), request.getRequestURI()));
        return BaseResponse.toCustomErrorWithMessageResponse(e.getExceptionCode(), e.getDescribe());
    }
    @ExceptionHandler(value = { MethodArgumentNotValidException.class })
    protected ResponseEntity<BaseResponse> handleMethodArgNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.warn(String.format("[400 Error] : %s %s", request.getMethod(), request.getRequestURI()));
        return BaseResponse.toBasicErrorResponse(HttpStatus.BAD_REQUEST, e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    /*@ExceptionHandler(value={IamportResponseException.class, IOException.class})
    protected ResponseEntity<BaseResponse> handleIamportResponseException(CustomExceptionWithMessage e, HttpServletRequest request) {
        log.warn(String.format("[%s Error] : %s %s", e.getExceptionCode().getStatus(), request.getMethod(), request.getRequestURI()));
        return BaseResponse.toCustomErrorWithMessageResponse(e.getExceptionCode(), e.getDescribe());
    }

    // request param
    @ExceptionHandler(value = { MissingServletRequestParameterException.class })
    protected ResponseEntity<BaseResponse> handleMissingRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest request) {
        log.warn(String.format("[%s Error] : %s %s", NO_REQUIRED_PARAMETER.getStatus(), request.getMethod(), request.getRequestURI()));
        return BaseResponse.toBasicErrorResponse(NO_REQUIRED_PARAMETER.getStatus(), NO_REQUIRED_PARAMETER.getMsg());
    }

    @ExceptionHandler(value = { MissingRequestHeaderException.class })
    protected ResponseEntity<BaseResponse> handleMissingRequestHeaderException(MissingRequestHeaderException e, HttpServletRequest request) {
        log.warn(String.format("[%s Error] : %s %s", NO_SPACE_ID_HEADER.getStatus(), request.getMethod(), request.getRequestURI()));
        return BaseResponse.toBasicErrorResponse(NO_SPACE_ID_HEADER.getStatus(), NO_SPACE_ID_HEADER.getMsg());
    }

    // @RequestBody valid 에러
    @ExceptionHandler(value = { MethodArgumentNotValidException.class })
    protected ResponseEntity<BaseResponse> handleMethodArgNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.warn(String.format("[400 Error] : %s %s", request.getMethod(), request.getRequestURI()));
        return BaseResponse.toBasicErrorResponse(HttpStatus.BAD_REQUEST, e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    // @ModelAttribute valid 에러
    @ExceptionHandler(value = { BindException.class })
    protected ResponseEntity<BaseResponse> handleMethodArgNotValidException(BindException e, HttpServletRequest request) {
        log.warn(String.format("[400 Error] : %s %s", request.getMethod(), request.getRequestURI()));
        return BaseResponse.toBasicErrorResponse(HttpStatus.BAD_REQUEST, e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    // 404 Error Handler
    @ExceptionHandler(value = { NoHandlerFoundException.class } )
    protected ResponseEntity<BaseResponse> handleNotFoundException(NoHandlerFoundException e, HttpServletRequest request){
        log.warn(String.format("[404 Error] : %s %s", request.getMethod(), request.getRequestURI()));
        return BaseResponse.toBasicErrorResponse(NOT_FOUND, request.getMethod()+ " " +request.getRequestURI()+ " 요청을 찾을 수 없습니다.");
    }

    // Rest Exception Handler
    @ExceptionHandler(value = { Exception.class })
    protected ResponseEntity<BaseResponse> handleException(Exception e, HttpServletRequest request) {
        log.error("[500 Error] : " + request.getMethod() + " " + request.getRequestURI() + " " + e.getMessage());
        log.error(e.toString());
        return BaseResponse.toBasicErrorResponse(INTERNAL_SERVER_ERROR, request.getRequestURI()+ " 서버 내에서 요청 처리 중 에러가 발생했습니다.");
    }*/
}
