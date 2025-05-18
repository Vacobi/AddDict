package ru.vstu.adddict.validator;

import org.springframework.stereotype.Component;
import ru.vstu.adddict.dto.subscribedictionary.CreateSubscribeDictionaryRequestDto;
import ru.vstu.adddict.dto.subscribedictionary.GetUserSubscribesDictionariesRequestDto;
import ru.vstu.adddict.exception.ClientExceptionName;
import ru.vstu.adddict.exception.GroupValidationException;
import ru.vstu.adddict.exception.ValidationException;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Component
public class SubscribeDictionaryValidator {

    public Optional<GroupValidationException> validateCreateSubscribeDictionaryRequestDto(CreateSubscribeDictionaryRequestDto requestDto) {

        List<ValidationException> exceptions = new LinkedList<>();

        if (requestDto.getUserId() == null || requestDto.getUserId() <= 0) {
            String exceptionDescription = "User id must be set and be positive";
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_CREATE_SUBSCRIBE_DICTIONARY_REQUEST));
        }

        if (requestDto.getDictionaryId() == null || requestDto.getDictionaryId() <= 0) {
            String exceptionDescription = "Dictionary id must be present and positive";
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_CREATE_SUBSCRIBE_DICTIONARY_REQUEST));
        }

        return exceptions.isEmpty() ? Optional.empty() : Optional.of(new GroupValidationException(exceptions));
    }

    public Optional<GroupValidationException> validateGetUserSubscribesDictionariesRequestDto(GetUserSubscribesDictionariesRequestDto request) {

        List<ValidationException> exceptions = new LinkedList<>();

        if (request.getUserId() == null || request.getUserId() <= 0) {
            String exceptionDescription = "User id must present be positive";
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_GET_USER_SUBSCRIBES_DICTIONARIES_REQUEST));
        }

        if (request.getRequestSenderId() == null || request.getRequestSenderId() <= 0) {
            String exceptionDescription = "Request sender id must be present and positive";
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_GET_USER_SUBSCRIBES_DICTIONARIES_REQUEST));
        }

        if (request.getPage() < 0) {
            String exceptionDescription = "Page must be non-negative";
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_GET_USER_SUBSCRIBES_DICTIONARIES_REQUEST));
        }

        return exceptions.isEmpty() ? Optional.empty() : Optional.of(new GroupValidationException(exceptions));
    }
}
