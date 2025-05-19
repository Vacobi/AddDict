package ru.vstu.adddict.exception;

import org.springframework.http.HttpStatus;

public class NotAllowedDictionaryException extends BaseClientException{
    public NotAllowedDictionaryException(String reason) {
        super(reason, ClientExceptionName.NOT_ALLOWED_DICTIONARY, HttpStatus.FORBIDDEN);
    }
}
