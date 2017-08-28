
package com.algamoney.api.exceptionhandler;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class AlgamoneyExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    @Autowired
    public AlgamoneyExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String mensagemUsuario = messageSource.getMessage("mensagem.invalida", null, LocaleContextHolder.getLocale());
        String messagemDesenvolvedor = ex.getCause() != null ? ex.getCause().toString() : ex.toString();
        List<Erro> errors = Collections.singletonList(new Erro(mensagemUsuario, messagemDesenvolvedor));
        return handleExceptionInternal(ex, errors, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<Erro> errors = listError(ex.getBindingResult());
        return handleExceptionInternal(ex, errors, headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({EmptyResultDataAccessException.class})
    public ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex, WebRequest request) {
        String mensagemUsuario = messageSource.getMessage("recurso.nao-encontrado", null, LocaleContextHolder.getLocale());
        String messagemDesenvolvedor = ex.toString();
        List<Erro> errors = Collections.singletonList(new Erro(mensagemUsuario, messagemDesenvolvedor));
        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        String mensagemUsuario = messageSource.getMessage("recurso.operacao-nao-permitida", null, LocaleContextHolder.getLocale());
        String messagemDesenvolvedor = ExceptionUtils.getRootCauseMessage(ex);
        List<Erro> errors = Collections.singletonList(new Erro(mensagemUsuario, messagemDesenvolvedor));
        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    private List<Erro> listError(BindingResult bindingResult) {
        List<Erro> errors = new ArrayList<>();
        for(FieldError fildError : bindingResult.getFieldErrors()) {
            String mensagemUsuario = messageSource.getMessage(fildError, LocaleContextHolder.getLocale());
            String messagemDesenvolvedor = fildError.toString();
            errors.add(new Erro(mensagemUsuario, messagemDesenvolvedor));
        }
        return errors;
    }

    public static class Erro {
        private String menssagemUsuario;
        private String messagemDesenvolvedor;

        public Erro(String menssagemUsuario, String messagemDesenvolvedor) {
            this.menssagemUsuario = menssagemUsuario;
            this.messagemDesenvolvedor = messagemDesenvolvedor;
        }

        public String getMenssagemUsuario() {
            return menssagemUsuario;
        }

        public String getMessagemDesenvolvedor() {
            return messagemDesenvolvedor;
        }
    }
}