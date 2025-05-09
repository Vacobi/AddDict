package ru.vstu.adddict.validator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.vstu.adddict.dto.CreateDictionaryRequestDto;
import ru.vstu.adddict.dto.UpdateDictionaryRequestDto;
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
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.EMPTY_UPDATE_REQUEST));
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

        if (name.length() > nameLength) {
            String exceptionDescription =
                    "Max length of dictionary name is " + nameLength + " symbols.";

            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_DICTIONARY_NAME));
        }

        return exceptions;
    }

    public List<ValidationException> validateDescription(String description) {
        List<ValidationException> exceptions = new LinkedList<>();

        if (description.length() > descriptionLength) {
            String exceptionDescription =
                    "Max length of dictionary description is " + descriptionLength + " symbols.";

            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_DICTIONARY_DESCRIPTION));
        }

        return exceptions;
    }
}
