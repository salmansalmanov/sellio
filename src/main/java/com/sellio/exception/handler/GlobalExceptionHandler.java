package com.sellio.exception.handler;

import com.sellio.exception.custom.CloudinaryException;
import com.sellio.exception.custom.ResourceNotFoundException;
import com.sellio.model.result.ErrorResult;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    public ResponseEntity<ErrorResult> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResult(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CloudinaryException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "500", description = "Cloudinary not working")
    })
    public ResponseEntity<ErrorResult> handleCloudinaryException(CloudinaryException ex) {
        return new ResponseEntity<>(new ErrorResult(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
