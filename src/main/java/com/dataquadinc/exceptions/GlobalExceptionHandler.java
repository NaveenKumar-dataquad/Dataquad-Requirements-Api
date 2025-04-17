package com.dataquadinc.exceptions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.dataquadinc.dto.ResponseBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {



	@ExceptionHandler(RequirementAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleRequirementAlreadyExists(RequirementAlreadyExistsException ex) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.OK.value(), ex.getMessage(),
				LocalDateTime.now());
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
		// Return a meaningful response with a 400 Bad Request status
		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(RequirementNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleRequirementNotFound(RequirementNotFoundException ex) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.OK.value(), ex.getMessage(),
				LocalDateTime.now());
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	@ExceptionHandler(NoJobsAssignedToRecruiterException.class)
	public ResponseEntity<ErrorResponse> handleNoJobsAssignedToRecruiter(NoJobsAssignedToRecruiterException ex) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.OK.value(), ex.getMessage(),
				LocalDateTime.now());
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"An unexpected error occurred", LocalDateTime.now());
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(ClientAlreadyExistsException.class)
	public ResponseEntity<ResponseBean> handleClientAlreadyExists(ClientAlreadyExistsException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(ResponseBean.errorResponse("Client already exists", ex.getMessage()));
	}

	@ExceptionHandler(DateRangeValidationException.class)
	public ResponseEntity<Map<String, String>> handleDateRangeValidationException(DateRangeValidationException ex) {
		Map<String, String> error = new HashMap<>();
		error.put("error", ex.getMessage());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
		if (ex.getRequiredType() == LocalDate.class) {
			return ResponseEntity.badRequest()
					.body(Collections.singletonMap("message", "Invalid date format. Use yyyy-MM-dd (e.g., 2025-04-30)."));
		}
		return ResponseEntity.badRequest()
				.body(Collections.singletonMap("message", "Invalid request parameter."));
	}
	@ExceptionHandler(AssignedByNotFoundException.class)
	public ResponseEntity<Map<String, String>> handleAssignedByNotFoundException(AssignedByNotFoundException ex) {
		Map<String, String> response = new HashMap<>();
		response.put("message", ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

}
