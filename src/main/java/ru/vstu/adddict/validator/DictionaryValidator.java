package ru.vstu.adddict.validator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.vstu.adddict.dto.dictionary.CreateDictionaryRequestDto;
import ru.vstu.adddict.dto.dictionary.GetUserDictionariesRequestDto;
import ru.vstu.adddict.dto.dictionary.GetUserSubscribedDictionariesRequestDto;
import ru.vstu.adddict.dto.dictionary.UpdateDictionaryRequestDto;
import ru.vstu.adddict.exception.ClientExceptionName;
import ru.vstu.adddict.exception.GroupValidationException;
import ru.vstu.adddict.exception.ValidationException;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Component
public class DictionaryValidator {

    private final int nameLength;
    private final int descriptionLength;

    public DictionaryValidator(
            @Qualifier("dictionaryNameLength") int nameLength,
            @Qualifier("dictionaryNameLength") int descriptionLength
    ) {
        this.nameLength = nameLength;
        this.descriptionLength = descriptionLength;
    }

    public Optional<GroupValidationException> validateCreateDictionaryRequest(CreateDictionaryRequestDto createDictionaryRequestDto) {
        List<ValidationException> exceptions = new LinkedList<>();

        exceptions.addAll(validateName(createDictionaryRequestDto.getName()));
        exceptions.addAll(validateDescription(createDictionaryRequestDto.getDescription()));

        return exceptions.isEmpty() ? Optional.empty() : Optional.of(new GroupValidationException(exceptions));
    }

    public Optional<GroupValidationException> validateUpdateDictionaryRequest(UpdateDictionaryRequestDto updateDictionaryRequestDto) {
        List<ValidationException> exceptions = new LinkedList<>();

        if (updateDictionaryRequestDto.isEmpty()) {
            String exceptionDescription = "All fields in update request are not set.";
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.EMPTY_UPDATE_DICTIONARY_REQUEST));
            return Optional.of(new GroupValidationException(exceptions));
        }

        if (updateDictionaryRequestDto.getName() != null) {
            exceptions.addAll(validateName(updateDictionaryRequestDto.getName()));
        }

        if (updateDictionaryRequestDto.getDescription() != null) {
            exceptions.addAll(validateDescription(updateDictionaryRequestDto.getDescription()));
        }

        return exceptions.isEmpty() ? Optional.empty() : Optional.of(new GroupValidationException(exceptions));
    }

    public List<ValidationException> validateName(String name) {
        List<ValidationException> exceptions = new LinkedList<>();

        if (name == null) {
            String exceptionDescription =
                    "Name of dictionary is empty";

            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_DICTIONARY_NAME));
            return exceptions;
        }

        if (name.length() > nameLength) {
            String exceptionDescription =
                    "Max length of dictionary name is " + nameLength + " symbols.";

            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_DICTIONARY_NAME));
        }

        return exceptions;
    }

    public List<ValidationException> validateDescription(String description) {
        List<ValidationException> exceptions = new LinkedList<>();

        if (description == null) {
            String exceptionDescription =
                    "Description of dictionary is empty.";

            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_DICTIONARY_DESCRIPTION));
            return exceptions;
        }

        if (description.length() > descriptionLength) {
            String exceptionDescription =
                    "Max length of dictionary description is " + descriptionLength + " symbols.";

            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_DICTIONARY_DESCRIPTION));
        }

        return exceptions;
    }

    public Optional<GroupValidationException> validateGetUserDictionariesRequestDto(GetUserDictionariesRequestDto request) {
        List<ValidationException> exceptions = new LinkedList<>();

        if (request.getUserId() == null || request.getUserId() <= 0) {
            String exceptionDescription = "User id must be positive";
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_GET_USER_DICTIONARIES_REQUEST));
        }

        if (request.getRequestSenderId() != null && request.getRequestSenderId() <= 0) {
            String exceptionDescription = "Request sender id must be positive";
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_GET_USER_DICTIONARIES_REQUEST));
        }

        if (request.getPage() < 0) {
            String exceptionDescription = "Page must be non-negative";
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_GET_DICTIONARY_TRANSLATIONS_REQUEST));
        }

        return exceptions.isEmpty() ? Optional.empty() : Optional.of(new GroupValidationException(exceptions));
    }

    public Optional<GroupValidationException> validateGetUserSubscribedDictionariesRequestDto(GetUserSubscribedDictionariesRequestDto request) {
        List<ValidationException> exceptions = new LinkedList<>();

        if (request.getUserId() == null || request.getUserId() <= 0) {
            String exceptionDescription = "User id must be positive";
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_GET_USER_DICTIONARIES_REQUEST));
        }

        if (request.getPage() < 0) {
            String exceptionDescription = "Page must be non-negative";
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_GET_DICTIONARY_TRANSLATIONS_REQUEST));
        }

        return exceptions.isEmpty() ? Optional.empty() : Optional.of(new GroupValidationException(exceptions));
    }
}
