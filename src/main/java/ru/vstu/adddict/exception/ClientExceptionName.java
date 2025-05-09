package ru.vstu.adddict.exception;

import lombok.Getter;

@Getter
public enum ClientExceptionName {
    VALIDATION_EXCEPTION(800),
    GROUP_VALIDATION_EXCEPTION(801),

    INVALID_DICTIONARY_NAME(300),
    INVALID_DICTIONARY_DESCRIPTION(301),
    DICTIONARY_NOT_FOUND(350),

    NOT_ALLOWED(400);

    private final int apiErrorCode;

    ClientExceptionName(int apiErrorCode) {
        this.apiErrorCode = apiErrorCode;
    }
}
