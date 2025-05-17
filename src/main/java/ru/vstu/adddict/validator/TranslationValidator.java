package ru.vstu.adddict.validator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.vstu.adddict.dto.translation.CreateTranslationRequestDto;
import ru.vstu.adddict.dto.dictionary.GetDictionaryTranslationsRequestDto;
import ru.vstu.adddict.dto.translation.UpdateTranslationRequestDto;
import ru.vstu.adddict.exception.ClientExceptionName;
import ru.vstu.adddict.exception.GroupValidationException;
import ru.vstu.adddict.exception.ValidationException;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Component
public class TranslationValidator {

    private final int originTextLength;
    private final int translationTextLength;

    public TranslationValidator(
            @Qualifier("originTextLength") int originTextLength,
            @Qualifier("translationTextLength") int translationTextLength
    ) {
        this.originTextLength = originTextLength;
        this.translationTextLength = translationTextLength;
    }

    public Optional<GroupValidationException> validateCreateTranslationRequest(CreateTranslationRequestDto createTranslationRequestDto) {
        List<ValidationException> exceptions = new LinkedList<>();

        exceptions.addAll(validateOriginText(createTranslationRequestDto.getOriginText()));
        exceptions.addAll(validateTranslatedText(createTranslationRequestDto.getTranslationText()));

        return exceptions.isEmpty() ? Optional.empty() : Optional.of(new GroupValidationException(exceptions));
    }

    public List<ValidationException> validateOriginText(String originText) {
        List<ValidationException> exceptions = new LinkedList<>();

        if (originText.length() > originTextLength) {
            String exceptionDescription =
                    "Max length of origin text in translation is " + originTextLength + " symbols.";

            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_ORIGIN_TRANSLATION_TEXT));
        }

        return exceptions;
    }

    public List<ValidationException> validateTranslatedText(String translatedText) {
        List<ValidationException> exceptions = new LinkedList<>();

        if (translatedText.length() > translationTextLength) {
            String exceptionDescription =
                    "Max length of translated text in translation is " + translationTextLength + " symbols.";

            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_TRANSLATION_TEXT));
        }

        return exceptions;
    }

    public Optional<GroupValidationException> validateUpdateTranslationRequest(UpdateTranslationRequestDto updateTranslationRequest) {
        List<ValidationException> exceptions = new LinkedList<>();

        if (updateTranslationRequest.isEmpty()) {
            String exceptionDescription = "All fields in update request are not set.";
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.EMPTY_UPDATE_TRANSLATION_REQUEST));
            return Optional.of(new GroupValidationException(exceptions));
        }

        if (updateTranslationRequest.getOriginText() != null) {
            exceptions.addAll(validateOriginText(updateTranslationRequest.getOriginText()));
        }

        if (updateTranslationRequest.getTranslationText() != null) {
            exceptions.addAll(validateTranslatedText(updateTranslationRequest.getTranslationText()));
        }

        return exceptions.isEmpty() ? Optional.empty() : Optional.of(new GroupValidationException(exceptions));
    }

    public Optional<GroupValidationException> validateGetDictionaryTranslationsRequestDto(GetDictionaryTranslationsRequestDto request) {

        List<ValidationException> exceptions = new LinkedList<>();

        if (request.getUserId() != null && request.getUserId() <= 0) {
            String exceptionDescription = "User id must be positive";
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_GET_DICTIONARY_TRANSLATIONS_REQUEST));
        }

        if (request.getDictionaryId() == null || request.getDictionaryId() <= 0) {
            String exceptionDescription = "User id must be present and positive";
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_GET_DICTIONARY_TRANSLATIONS_REQUEST));
        }

        if (request.getPage() < 0) {
            String exceptionDescription = "Page must be non-negative";
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_GET_DICTIONARY_TRANSLATIONS_REQUEST));
        }

        return exceptions.isEmpty() ? Optional.empty() : Optional.of(new GroupValidationException(exceptions));
    }
}
