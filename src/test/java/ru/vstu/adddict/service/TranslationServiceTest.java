package ru.vstu.adddict.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import ru.vstu.adddict.config.TestContainersConfig;
import ru.vstu.adddict.dto.*;
import ru.vstu.adddict.entity.Translation;
import ru.vstu.adddict.exception.NotAllowedException;
import ru.vstu.adddict.mapper.TranslationMapper;
import ru.vstu.adddict.repository.TranslationRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.vstu.adddict.testutils.TestAsserts.assertTranslationsDtoEquals;

@SpringBootTest
@ContextConfiguration(initializers = TestContainersConfig.class)
@Slf4j
class TranslationServiceTest {

    @Autowired
    private TranslationService translationService;

    @Autowired
    private TranslationRepository translationRepository;

    @Autowired
    private TranslationMapper translationMapper;

    @Autowired
    private DictionaryService dictionaryService;

    private DictionaryDto createDictionary(boolean isPublic, Long authorId) {
        CreateDictionaryRequestDto createDictionaryRequestDto = CreateDictionaryRequestDto.builder()
                .name("Test name")
                .description("Test description")
                .isPublic(isPublic)
                .authorId(authorId)
                .build();
        return dictionaryService.createDictionary(createDictionaryRequestDto);
    }

    @Nested
    class GetTranslation {

        @Test
        void getTheOnlyOneTranslationInOwnedPublicDictionary() {
            Long authorId = 1L;
            DictionaryDto dictionaryDto = createDictionary(true, authorId);
            Long dictionaryId = dictionaryDto.getId();

            Long translationId = 1L;
            String translationText = "Test translation 1";
            String originalText = "Origin translation 1";
            Translation translation = Translation.builder()
                    .id(translationId)
                    .translationText(translationText)
                    .originText(originalText)
                    .dictionaryId(dictionaryId)
                    .build();
            translationRepository.save(translation);

            GetTranslationRequestDto requestDto = GetTranslationRequestDto.builder()
                    .translationId(translationId)
                    .dictionaryId(dictionaryId)
                    .build();

            TranslationDto expectedDto = TranslationDto.builder()
                    .id(translationId)
                    .translationText(translationText)
                    .originText(originalText)
                    .dictionaryId(dictionaryId)
                    .build();

            TranslationDto actualDto = translationService.getTranslation(requestDto, authorId);

            assertTranslationsDtoEquals(expectedDto, actualDto);
        }

        @Test
        void getTranslationFromPrivateDictionaryOfOtherOwnerDictionary() {
            Long authorId = 1L;
            DictionaryDto dictionaryDto = createDictionary(false, authorId);
            Long dictionaryId = dictionaryDto.getId();

            Long translationId = 1L;
            String translationText = "Test translation 1";
            String originalText = "Origin translation 1";
            Translation translation = Translation.builder()
                    .id(translationId)
                    .translationText(translationText)
                    .originText(originalText)
                    .dictionaryId(dictionaryId)
                    .build();
            translationRepository.save(translation);

            GetTranslationRequestDto requestDto = GetTranslationRequestDto.builder()
                    .translationId(translationId)
                    .dictionaryId(dictionaryId)
                    .build();

            Long requestSenderId = 2L;
            assertThrows(NotAllowedException.class, () -> translationService.getTranslation(requestDto, requestSenderId));
        }
    }
}