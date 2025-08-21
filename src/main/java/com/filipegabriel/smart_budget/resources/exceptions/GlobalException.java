package com.filipegabriel.smart_budget.resources.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<StandardError> noSuchElement(NoSuchElementException e, HttpServletRequest request){
        Instant timestamp = Instant.now();
        HttpStatus status = HttpStatus.NOT_FOUND;
        String error = "Id não encontrado";
        String message = e.getMessage();
        String path = request.getRequestURI();
        StandardError standardError = new StandardError(timestamp, status.value(), error, message, path);
        return ResponseEntity.status(status).body(standardError);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardError> illegalArgument(IllegalArgumentException e, HttpServletRequest request){
        Instant timestamp = Instant.now();
        HttpStatus status = HttpStatus.CONFLICT;
        String error = "Exceção de argumento ilegal. Verifique!";
        String message = e.getMessage();
        String path = request.getRequestURI();
        StandardError standardError = new StandardError(timestamp, status.value(), error, message, path);
        return ResponseEntity.status(status).body(standardError);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<StandardError> dateTimeParse(DateTimeParseException e, HttpServletRequest request){
        Instant timestamp = Instant.now();
        HttpStatus status = HttpStatus.NOT_ACCEPTABLE;
        String error = "Formato de data invalido, entre em contato com o suporte, informando essa mensagem!";
        String message = e.getMessage();
        String path = request.getRequestURI();
        StandardError standardError = new StandardError(timestamp, status.value(), error, message, path);
        return ResponseEntity.status(status).body(standardError);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> dataIntegrityViolation(DataIntegrityViolationException e, HttpServletRequest request){
        Instant timestamp = Instant.now();
        HttpStatus status = HttpStatus.CONFLICT;
        String error = "Houve algum erro ao tentar realizar o procedimento.";
        String message = e.getMessage();
        String path = request.getRequestURI();
        StandardError standardError = new StandardError(timestamp, status.value(), error, message, path);
        return ResponseEntity.status(status).body(standardError);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<StandardError> HttpMessageNotReadable(HttpMessageNotReadableException e, HttpServletRequest request){
        Instant timestamp = Instant.now();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String error = "Formato do campo invalido";
        String message = e.getMessage();
        String path = request.getRequestURI();
        StandardError standardError = new StandardError(timestamp, status.value(), error, message, path);
        return ResponseEntity.status(status).body(standardError);
    }

    @ExceptionHandler(VerifyError.class)
    public ResponseEntity<StandardError> VerifyError(VerifyError e, HttpServletRequest request){
        Instant timestamp = Instant.now();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String error = e.getMessage();
        String message = e.getMessage();
        String path = request.getRequestURI();
        StandardError standardError = new StandardError(timestamp, status.value(), error, message, path);
        return ResponseEntity.status(status).body(standardError);
    }

}

