package ru.vstu.adddict.validator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.vstu.adddict.dto.CreateTranslationRequestDto;
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
}
