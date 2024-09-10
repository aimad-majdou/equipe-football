package com.matawan.equipefootball.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * This class handles exceptions globally across the entire application
 * It catches specific exceptions (in our case we want to catch validation errors) that might occur during API calls
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles the ResourceNotFoundException exception
     * @param ex the exception that gets thrown when resource is not found
     * @return a ResponseEntity containing the error details (timestamp, status, and message)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("message", ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }


    /**
     * Handles validation errors triggered when the request body fails the validation rules
     * This method is triggered when validation on an argument annotated with `@Valid` fails
     *
     * @param ex the exception that gets thrown when validation fails
     * @return a ResponseEntity containing the error details (timestamp, status, and validation error messages for each field)
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400 status
    @ExceptionHandler(MethodArgumentNotValidException.class) // catches validation errors
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {

        // a map to store the validation errors
        Map<String, String> validationErrors = new HashMap<>();

        // extract the validation errors and map them to the field names
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);  // add the field name and error message to the map
        });

        // create a map for the full error response including timestamp and status
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("errors", validationErrors);  // add validation errors to the response

        // return the errors map as the response body, with a 400 status
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}