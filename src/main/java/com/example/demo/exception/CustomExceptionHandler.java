package com.example.demo.exception;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(ProductNotFoundException.class)
	public final ResponseEntity<ExceptionResponse> productNotFound(ProductNotFoundException ex) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(),
				"Product Not Exists");
		return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);

	}

	@ExceptionHandler(BadCredentialsException.class)
	public final ResponseEntity<ExceptionResponse> badCredentialsException(BadCredentialsException ex) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(),"User name and Password not matched");
		return new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
	}

	
	@ExceptionHandler(EmptyResultDataAccessException.class)
	public final ResponseEntity<ExceptionResponse> badCredentialsException(EmptyResultDataAccessException ex) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(),"Not Exists");
		return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<ExceptionResponse> genericException(Exception ex) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(),
				"Any details you would want to add");
		return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
	}

}


