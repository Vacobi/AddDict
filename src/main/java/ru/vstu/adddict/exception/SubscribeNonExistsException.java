package ru.vstu.adddict.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class SubscribeNonExistsException extends BaseClientException {
    private final Long nonExistSubscribeId;

    public SubscribeNonExistsException(Long nonExistSubscribeId) {
        super(
                String.format("Subscribe with id %d not found", nonExistSubscribeId),
                ClientExceptionName.SUBSCRIBE_DICTIONARY_NOT_FOUND,
                HttpStatus.NOT_FOUND
        );
        this.nonExistSubscribeId = nonExistSubscribeId;
    }

    @Override
    public Map<String, Object> properties() {
        return Map.of("id", nonExistSubscribeId);
    }
}