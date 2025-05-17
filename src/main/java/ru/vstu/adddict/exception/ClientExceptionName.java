package ru.vstu.adddict.exception;

import lombok.Getter;

@Getter
public enum ClientExceptionName {
    VALIDATION_EXCEPTION(800),
    GROUP_VALIDATION_EXCEPTION(801),

    INVALID_DICTIONARY_NAME(300),
    INVALID_DICTIONARY_DESCRIPTION(301),
    EMPTY_UPDATE_DICTIONARY_REQUEST(320),
    DICTIONARY_NOT_FOUND(350),

    NOT_ALLOWED(400),

    INVALID_ORIGIN_TRANSLATION_TEXT(200),
    INVALID_TRANSLATION_TEXT(201),
    INVALID_GET_DICTIONARY_TRANSLATIONS_REQUEST(202),
    TRANSLATION_NOT_FOUND(250);

    private final int apiErrorCode;

    ClientExceptionName(int apiErrorCode) {
        this.apiErrorCode = apiErrorCode;
    }
}
