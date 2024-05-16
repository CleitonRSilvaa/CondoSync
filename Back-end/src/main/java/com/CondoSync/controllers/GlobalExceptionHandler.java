package com.CondoSync.controllers;

import jakarta.persistence.EntityNotFoundException;
import com.CondoSync.components.ValidateUserException;
import com.CondoSync.models.DTOs.ResponseDTO;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(HttpMessageNotReadableException.class)
  protected ResponseEntity<ResponseDTO> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
    ResponseDTO error = new ResponseDTO(
        HttpStatus.BAD_REQUEST.value(),
        "Requisição inválida",
        "Corpo da requisição inválido");

    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField().toLowerCase();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    var retorno = new ResponseDTO(
        HttpStatus.BAD_REQUEST.value(),
        "Erro de validação",
        "Um ou mais campos estão inválidos",
        errors);

    return new ResponseEntity<>(retorno, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
    Throwable cause = ex.getCause();
    while (cause != null) {
      if (cause.getMessage() != null) {
        if (cause.getMessage().contains("Value too long for column")) {
          return ResponseEntity
              .status(HttpStatus.BAD_REQUEST)
              .body(new ResponseDTO(
                  HttpStatus.BAD_REQUEST.value(),
                  "Data too long for column",
                  "Please ensure your inputs meet the field specifications."));
        }
        if (cause.getMessage().contains("CPF")) {
          return ResponseEntity
              .status(HttpStatus.CONFLICT)
              .body(new ResponseDTO(
                  HttpStatus.CONFLICT.value(),
                  "CPF duplicado",
                  "CPF já cadastrado"));
        }
        if (cause.getMessage().contains("constraint [email]")) {
          return ResponseEntity
              .status(HttpStatus.CONFLICT)
              .body(new ResponseDTO(
                  HttpStatus.CONFLICT.value(),
                  "Email duplicado",
                  "Email já cadastrado"));

        }
        if (cause.getMessage().contains("constraint [fk_morador_roles]")) {
          return ResponseEntity
              .status(HttpStatus.CONFLICT)
              .body(new ResponseDTO(
                  HttpStatus.CONFLICT.value(),
                  "Role não encontrada",
                  "Role não encontrada"));
        }
        if (cause.getMessage().contains("Unique index or primary key violation")
            && cause.getMessage().contains("PUBLIC.USERS(USER_NAME NULLS FIRST)")) {
          // Unique index or primary key violation
          return ResponseEntity
              .status(HttpStatus.CONFLICT)
              .body(new ResponseDTO(
                  HttpStatus.CONFLICT.value(),
                  "Erro de integridade de dados",
                  "Um registro com email já existe."));

        }
        if (cause.getMessage().contains("Unique index or primary key violation")) {
          // Unique index or primary key violation
          return ResponseEntity
              .status(HttpStatus.CONFLICT)
              .body(new ResponseDTO(
                  HttpStatus.CONFLICT.value(),
                  "Erro de integridade de dados",
                  "Um registro com os mesmos dados já existe.",
                  cause.getMessage()));

        }
        // Add more conditions for other specific errors if necessary
      }
      cause = cause.getCause();
    }

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST) // 409 Conflict
        .body(new ResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Erro de integridade de dados", null));
  }

  @ExceptionHandler(InternalAuthenticationServiceException.class)
  public ResponseEntity<?> handleInternalAuthenticationException(
      InternalAuthenticationServiceException ex) {
    ResponseDTO apiError = new ResponseDTO(
        HttpStatus.UNAUTHORIZED.value(),
        "Conta de usuário inativa",
        "Conta de usuário inativa");
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);

  }

  @ExceptionHandler(ValidateUserException.class)
  public ResponseEntity<?> handleValidateUserException(
      ValidateUserException ex) {
    ResponseDTO apiError = new ResponseDTO(
        HttpStatus.CONFLICT.value(),
        ex.getMessage(),
        ex.getMessage(),
        ex.getData());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);

  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ResponseDTO> handleBadCredentialsException(BadCredentialsException ex) {
    ResponseDTO apiError = new ResponseDTO(
        HttpStatus.UNAUTHORIZED.value(),
        "Usuário ou senha incorretos.",
        "Verifique suas credenciais.");
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);

  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex) {

    ResponseDTO apiError = new ResponseDTO(
        HttpStatus.BAD_REQUEST.value(),
        ex.getMessage(),
        ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);

  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<ResponseDTO> handleUsernameNotFoundException(UsernameNotFoundException ex) {

    ResponseDTO apiError = new ResponseDTO(
        HttpStatus.NOT_FOUND.value(),
        "Usuário não encontrado",
        "Usuário não encontrado");
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);

  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ResponseDTO> handleConstraintViolationException(ConstraintViolationException ex) {

    Map<String, String> errors = new HashMap<>();
    ex.getConstraintViolations().forEach((error) -> {
      String fieldName = error.getPropertyPath().toString().toLowerCase();
      String errorMessage = error.getMessage();
      errors.put(fieldName, errorMessage);
    });

    ResponseDTO apiError = new ResponseDTO(
        HttpStatus.BAD_REQUEST.value(),
        "Erro de validação",
        "Um ou mais campos estão inválidos",
        errors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);

  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ResponseDTO> handleMissingServletRequestParameterException(
      MissingServletRequestParameterException ex) {

    ResponseDTO apiError = new ResponseDTO(
        HttpStatus.BAD_REQUEST.value(),
        "Erro de validação",
        "Um ou mais campos estão inválidos");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);

  }

  // LockedException
  @ExceptionHandler(org.springframework.security.authentication.LockedException.class)
  public ResponseEntity<ResponseDTO> handleLockedException(
      org.springframework.security.authentication.LockedException ex) {

    ResponseDTO apiError = new ResponseDTO(
        HttpStatus.UNAUTHORIZED.value(),
        "Conta de usuário bloqueada",
        "Contate o administrador do sistema");
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
  }

  // CredentialsExpiredException
  @ExceptionHandler(org.springframework.security.authentication.CredentialsExpiredException.class)
  public ResponseEntity<ResponseDTO> handleCredentialsExpiredException(
      org.springframework.security.authentication.CredentialsExpiredException ex) {

    ResponseDTO apiError = new ResponseDTO(
        HttpStatus.UNAUTHORIZED.value(),
        "Senha expirada",
        "Altere sua senha");
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
  }

  // EntityNotFoundException
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ResponseDTO> handleEntityNotFoundException(EntityNotFoundException ex) {

    ResponseDTO apiError = new ResponseDTO(
        HttpStatus.NOT_FOUND.value(),
        ex.getMessage(),
        "Recurso não encontrado");
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
  }

  @ExceptionHandler(MissingPathVariableException.class)
  public ResponseEntity<ResponseDTO> handleMissingPathVariableException(MissingPathVariableException ex) {

    Map<String, String> errors = new HashMap<>();
    errors.put(ex.getVariableName(), "O parâmetro " + ex.getVariableName() + " é obrigatório");

    ResponseDTO apiError = new ResponseDTO(
        HttpStatus.BAD_REQUEST.value(),
        "Erro de validação",
        "Um ou mais campos estão inválidos",
        errors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
  }

  // ResourceAccessException

  // @ExceptionHandler(ResourceAccessException.class)
  // public ResponseEntity<ResponseDTO> handleResourceAccessException(
  // ResourceAccessException ex) {

  // }

  // UnexpectedTypeException
  // @ExceptionHandler(UnexpectedTypeException.class)
  // public ResponseEntity<ResponseDTO> handleUnexpectedTypeException(
  // UnexpectedTypeException ex) {

  // Map<String, String> errors = new HashMap<>();

  // ResponseDTO apiError = new ResponseDTO(
  // HttpStatus.BAD_REQUEST.value(),
  // "Erro de validação",
  // "Erro de validação");
  // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
  // }

}
