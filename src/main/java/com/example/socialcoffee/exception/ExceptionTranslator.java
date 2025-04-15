package com.example.socialcoffee.exception;

import com.example.socialcoffee.dto.response.MetaDTO;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.enums.MetaData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * Controller advice to translate the server side exceptions to client-friendly json structures.
 * The error response follows RFC7807 - Problem Details for HTTP APIs (https://tools.ietf.org/html/rfc7807).
 */
@ControllerAdvice
@Slf4j
public class ExceptionTranslator {

    @ExceptionHandler
    public ResponseEntity<ResponseMetaData> handleException(Exception ex) {
        log.warn("Error", ex);
        return ResponseEntity.status(MetaData.INTERNAL_SERVER_ERROR.getMetaCode()).body(new ResponseMetaData(new MetaDTO(MetaData.INTERNAL_SERVER_ERROR), ""));
    }
    @ExceptionHandler
    public ResponseEntity<ResponseMetaData> handleInsufficientAuthenticationException(InsufficientAuthenticationException ex) {
        log.warn("Error", ex);
        return ResponseEntity.status(MetaData.UNAUTHORIZED.getMetaCode()).body(new ResponseMetaData(new MetaDTO(MetaData.UNAUTHORIZED)));
    }
    @ExceptionHandler
    public ResponseEntity<ResponseMetaData> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        log.warn("MissingServletRequestParameterException ", ex.getMessage());
        return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.PARAMETERS_MISSING), ""));
    }

    @ExceptionHandler
    public ResponseEntity<ResponseMetaData> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("handleIllegalArgumentException ", ex.getMessage());
        return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.ILLEGAL_ARGUMENT, ex.getMessage()), ""));
    }

//
//    @ExceptionHandler
//    public ResponseEntity<ResponseMetaData> handleExpiredJwtExceptionException(ExpiredJwtException ex) {
//        log.error("ExpiredJwtException ", ex);
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(new ResponseMetaData(new MetaDTO(MetaData.TOKEN_EXPIRED), ""));
//    }

    @ExceptionHandler
    public ResponseEntity<ResponseMetaData> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        log.warn("Http Media Type Not Supported Exception ", ex);
        return ResponseEntity
                .status(MetaData.HTTP_MEDIA_TYPE_NOT_SUPPORTED.getMetaCode())
                .body(new ResponseMetaData(new MetaDTO(MetaData.HTTP_MEDIA_TYPE_NOT_SUPPORTED), ""));
    }

    @ExceptionHandler
    public ResponseEntity<ResponseMetaData> handleMissingServletRequestPartException(MissingServletRequestPartException ex) {
        log.warn("Missing Servlet Request Part Exception ", ex);
        return ResponseEntity
                .status(MetaData.MISSING_SERVLET_REQUEST_PART.getMetaCode())
                .body(new ResponseMetaData(new MetaDTO(MetaData.MISSING_SERVLET_REQUEST_PART), ""));
    }

    @ExceptionHandler
    public ResponseEntity<ResponseMetaData> handleMultipartException(MultipartException ex) {
        log.warn("Multipart Exception ", ex);
        return ResponseEntity
                .status(MetaData.MULTIPART_IS_NULL.getMetaCode())
                .body(new ResponseMetaData(new MetaDTO(MetaData.MULTIPART_IS_NULL), ""));
    }

    @ExceptionHandler
    public ResponseEntity<ResponseMetaData> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.warn("MethodArgumentTypeMismatchException ", ex);
        return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.MULTIPART_IS_NULL), ""));
    }

    @ExceptionHandler
    public ResponseEntity<ResponseMetaData> handleNumberFormatException(NumberFormatException ex) {
        log.warn("MethodArgumentTypeMismatchException ", ex);
        return ResponseEntity.status(MetaData.BAD_REQUEST.getMetaCode()).body(new ResponseMetaData(new MetaDTO(MetaData.ONLY_NUMBER), null));
    }

    @ExceptionHandler
    public ResponseEntity<ResponseMetaData> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("Input request body NOT correct: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.REQUEST_BODY_INVALID), null));
    }

//    @ExceptionHandler
//    public ResponseEntity<ResponseMetaData> handleEncodingDataInvalidException(EncodingDataInvalidException ex) {
//        log.warn("Input Encoding Data Invalid Exception ", ex);
//        return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.INPUT_ENCODING_DATA_INVALID), null));
//    }

    @ExceptionHandler
    public ResponseEntity<ResponseMetaData> handleActionNotAllowedException(ActionNotAllowedException ex) {
        return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.ACTION_NOT_ALLOWED), null));
    }

    @ExceptionHandler
    public ResponseEntity<ResponseMetaData> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.warn("Http Request Method Not Supported Exception ", ex);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new ResponseMetaData(new MetaDTO(MetaData.METHOD_NOT_ALLOWED), null));
    }

    @ExceptionHandler
    public ResponseEntity<ResponseMetaData> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        log.warn("Missing request header, {}", ex.getMessage());
        return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.MISSING_REQUEST_HEADER), null));
    }
}

