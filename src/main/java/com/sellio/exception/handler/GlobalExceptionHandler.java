package com.sellio.exception.handler;

import com.sellio.exception.custom.*;
import com.sellio.model.result.ErrorDataResult;
import com.sellio.model.result.ErrorResult;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ApiResponse(responseCode = "404", description = "Resource not found")
    public ResponseEntity<ErrorResult> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResult(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CloudinaryException.class)
    @ApiResponse(responseCode = "500", description = "Cloudinary not working")
    public ResponseEntity<ErrorResult> handleCloudinaryException(CloudinaryException ex) {
        return new ResponseEntity<>(new ErrorResult(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    @ApiResponse(responseCode = "409", description = "Resource already exists")
    public ResponseEntity<ErrorResult> handleAlreadyExistsException(AlreadyExistsException ex) {
        return new ResponseEntity<>(new ErrorResult(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidInputException.class)
    @ApiResponse(responseCode = "400", description = "Bad Request")
    public ResponseEntity<ErrorResult> handleInvalidInputException(InvalidInputException ex) {
        return new ResponseEntity<>(new ErrorResult(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GoogleMapsException.class)
    @ApiResponse(responseCode = "500", description = "Google Maps Error")
    public ResponseEntity<ErrorResult> handleMethodArgumentNotValid(GoogleMapsException ex) {
        return new ResponseEntity<>(new ErrorResult(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MailException.class)
    @ApiResponse(responseCode = "500", description = "Mail Error")
    public ResponseEntity<ErrorResult> handleMailException(MailException ex) {
        return new ResponseEntity<>(new ErrorResult(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidTokenException.class)
    @ApiResponse(responseCode = "400", description = "Invalid token")
    public ResponseEntity<ErrorResult> handleInvalidTokenException(InvalidTokenException ex) {
        return new ResponseEntity<>(new ErrorResult(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ApiResponse(responseCode = "400", description = "Bad Request")
    public ResponseEntity<ErrorDataResult<Map<String, String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return new ResponseEntity<>(new ErrorDataResult<>(errors, "Validation failed"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    public ResponseEntity<ErrorResult> handleException(Exception ex) {
        return new ResponseEntity<>(new ErrorResult("An unexpected error occurred: " + ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
