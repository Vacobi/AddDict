package ru.vstu.adddict.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class TranslationNonExistException extends BaseClientException {
    private final Long dictId;
    private final Long translationId;

    public TranslationNonExistException(Long dictId, Long translationId) {
        super(
                String.format("Can't find translation with id %d in dictionary with id %d", translationId, dictId),
                ClientExceptionName.TRANSLATION_NOT_FOUND,
                HttpStatus.NOT_FOUND
        );
        this.dictId = dictId;
        this.translationId = translationId;
    }

    @Override
    public Map<String, Object> properties() {
        return Map.of("dictId", dictId, "translationId", translationId );
    }
}
