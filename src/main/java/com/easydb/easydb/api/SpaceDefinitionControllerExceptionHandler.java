package com.easydb.easydb.api;

import com.easydb.easydb.domain.space.SpaceDoesNotExistException;
import com.easydb.easydb.domain.space.SpaceNameNotUniqueException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class SpaceDefinitionControllerExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = {SpaceNameNotUniqueException.class})
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	void handleSpaceNameNotUniqueException() { }

	@ExceptionHandler(value = {SpaceDoesNotExistException.class})
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	void handleSpaceDoesNotExistException() { }
}
