package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // 409
public class DialogueOrderConflictException extends RuntimeException {
  public DialogueOrderConflictException(String message) {
    super(message);
  }
}
