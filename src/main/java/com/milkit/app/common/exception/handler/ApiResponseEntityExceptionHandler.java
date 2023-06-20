package com.milkit.app.common.exception.handler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.milkit.app.common.ErrorCodeEnum;
import com.milkit.app.common.exception.ServiceException;
import com.milkit.app.common.response.GenericResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ApiResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = { Exception.class })
	protected ResponseEntity<Object> handleRuntimeException(Exception ex, WebRequest request) {
		log.error(ex.getMessage(), ex);
		
		return handleExceptionInternal(ex, new GenericResponse<>(ErrorCodeEnum.SystemError.getCode(), ErrorCodeEnum.SystemError.getMessage()), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}
	
	@ExceptionHandler(value = { ServiceException.class })
	protected ResponseEntity<Object> handleServiceException(ServiceException ex, WebRequest request) {
		log.error(ex.getMessage(), ex);
		
		return handleExceptionInternal(ex, new GenericResponse<>(ex.getCode(), ex.getMessage()), new HttpHeaders(), HttpStatus.CONFLICT, request);
	}
}
