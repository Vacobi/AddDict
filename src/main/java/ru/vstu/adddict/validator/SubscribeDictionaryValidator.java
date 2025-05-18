package ru.vstu.adddict.validator;

import org.springframework.stereotype.Component;
import ru.vstu.adddict.dto.subscribedictionary.CreateSubscribeDictionaryRequestDto;
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
}
