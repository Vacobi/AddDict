package ru.vstu.adddict.exception;

import org.springframework.http.HttpStatus;

public class NotAllowedException extends BaseClientException {
    public NotAllowedException(String reason) {
        super(reason, ClientExceptionName.NOT_ALLOWED, HttpStatus.FORBIDDEN);
    }
}
