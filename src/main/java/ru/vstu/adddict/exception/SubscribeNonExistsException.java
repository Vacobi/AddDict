package ru.vstu.adddict.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class SubscribeNonExistsException extends BaseClientException {
    private final Long nonExistSubscribeId;
    private final Long userId;
    private final Long dictionaryId;

    public SubscribeNonExistsException(Long nonExistSubscribeId) {
        super(
                String.format("Subscribe with id %d not found", nonExistSubscribeId),
                ClientExceptionName.SUBSCRIBE_DICTIONARY_NOT_FOUND,
                HttpStatus.NOT_FOUND
        );
        this.nonExistSubscribeId = nonExistSubscribeId;
        this.userId = null;
        this.dictionaryId = null;
    }

    public SubscribeNonExistsException(Long userId, Long dictionaryId) {
        super(
                String.format("Subscribe to the dictionary with id %d from user with id %d not found", dictionaryId, userId),
                ClientExceptionName.SUBSCRIBE_DICTIONARY_NOT_FOUND,
                HttpStatus.NOT_FOUND
        );
        this.nonExistSubscribeId = null;
        this.userId = userId;
        this.dictionaryId = dictionaryId;
    }

    @Override
    public Map<String, Object> properties() {
        if (nonExistSubscribeId != null) {
            return Map.of("id", nonExistSubscribeId);
        }

        return Map.of("userId", userId, "dictionaryId", dictionaryId);
    }
}