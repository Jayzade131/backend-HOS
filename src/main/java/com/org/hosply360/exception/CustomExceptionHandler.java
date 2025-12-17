package com.org.hosply360.exception;

import com.org.hosply360.dto.authDTO.AppResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomExceptionHandler {

    private static ResponseEntity<AppResponseDTO> prepareExceptionResponse(HttpStatus httpStatus, Object message) {
        return ResponseEntity.status(httpStatus).body(AppResponseDTO.customStatus(message.toString(), httpStatus));
    }

    @ExceptionHandler(value = {GlobalMasterException.class})
    public ResponseEntity<?> globalMasterException(GlobalMasterException globalMasterException) {
        log.error(globalMasterException.getMessage(), globalMasterException);
        return prepareExceptionResponse(globalMasterException.getHttpStatus(), globalMasterException.getMessage());
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<?> genricException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return prepareExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    @ExceptionHandler(value = {UserException.class})
    public ResponseEntity<?> userException(UserException userException) {
        log.error(userException.getMessage(), userException);
        return prepareExceptionResponse(userException.getHttpStatus(), userException.getMessage());
    }

    @ExceptionHandler(value = {CustomException.class})
    public ResponseEntity<?> customException(CustomException customException) {
        log.error(customException.getMessage(), customException);
        return prepareExceptionResponse(customException.getHttpStatus(), customException.getMessage());
    }

    @ExceptionHandler(value = {InternalAuthenticationServiceException.class})
    public ResponseEntity<?> internalAuthServiceException(InternalAuthenticationServiceException internalAuthenticationServiceException) {
        log.error(internalAuthenticationServiceException.getMessage(), internalAuthenticationServiceException);
        return prepareExceptionResponse(HttpStatus.BAD_REQUEST, internalAuthenticationServiceException.getMessage());
    }

    @ExceptionHandler(value = {FrontDeskException.class})
    public ResponseEntity<?> frontDeskException(FrontDeskException frontDeskException) {
        log.error(frontDeskException.getMessage(), frontDeskException);
        return prepareExceptionResponse(frontDeskException.getHttpStatus(), frontDeskException.getMessage());
    }

    @ExceptionHandler(value = {OPDException.class})
    public ResponseEntity<?> opdException(OPDException opdException) {
        log.error(opdException.getMessage(), opdException);
        return prepareExceptionResponse(opdException.getHttpStatus(), opdException.getMessage());
    }

}
