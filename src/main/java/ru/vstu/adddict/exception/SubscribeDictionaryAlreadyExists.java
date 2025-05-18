package ru.vstu.adddict.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class SubscribeDictionaryAlreadyExists extends BaseClientException {

  private final Long dictionaryId;
  private final Long userId;

  public SubscribeDictionaryAlreadyExists(Long dictionaryId, Long userId) {
    super(
            String.format("User with id %d already subscribed to dictionary with id %d", userId, dictionaryId),
            ClientExceptionName.DUPLICATE_SUBSCRIBE_DICTIONARY_REQUEST,
            HttpStatus.CONFLICT
    );

    this.dictionaryId = dictionaryId;
    this.userId = userId;
  }

  @Override
  public Map<String, Object> properties() {
    return Map.of("dictionaryId", dictionaryId, "userId", userId );
  }
}
