package ru.vstu.adddict.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class DictionaryNonExistException extends BaseClientException {
    private final Long nonExistDictionaryId;

    public DictionaryNonExistException(Long nonExistDictionaryId) {
        super(
                String.format("Dictionary with id %d not found", nonExistDictionaryId),
                ClientExceptionName.DICTIONARY_NOT_FOUND,
                HttpStatus.NOT_FOUND
        );
        this.nonExistDictionaryId = nonExistDictionaryId;
    }

    @Override
    public Map<String, Object> properties() {
        return Map.of("id", nonExistDictionaryId);
    }
}
