package ru.vstu.adddict.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import ru.vstu.adddict.config.TestContainersConfig;
import ru.vstu.adddict.dto.dictionary.CreateDictionaryRequestDto;
import ru.vstu.adddict.dto.dictionary.DictionaryDto;
import ru.vstu.adddict.dto.translation.CreateTranslationRequestDto;
import ru.vstu.adddict.dto.translation.GetTranslationRequestDto;
import ru.vstu.adddict.dto.translation.TranslationDto;
import ru.vstu.adddict.dto.translation.UpdateTranslationRequestDto;
import ru.vstu.adddict.entity.translation.Translation;
import ru.vstu.adddict.exception.NotAllowedException;
import ru.vstu.adddict.mapper.TranslationMapper;
import ru.vstu.adddict.repository.TranslationRepository;
import ru.vstu.adddict.testutils.ClearableTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.vstu.adddict.testutils.TestAsserts.assertTranslationsDtoEquals;

@SpringBootTest
@ContextConfiguration(initializers = TestContainersConfig.class)
@Slf4j
class TranslationServiceTest extends ClearableTest {

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

    private TranslationDto addTranslationInDictionary(Long dictionaryId) {
        String originalText = "Origin text 1";
        String translationText = "Translation text 1";
        Long requestSenderId = 1L;
        CreateTranslationRequestDto requestDto = CreateTranslationRequestDto.builder()
                .originText(originalText)
                .translationText(translationText)
                .dictionaryId(dictionaryId)
                .requestSenderId(requestSenderId)
                .build();

        return translationService.createTranslation(requestDto);
    }

    @Nested
    class GetTranslation {

        @Test
        void getTheOnlyOneTranslationInOwnedPublicDictionary() {
            Long authorId = 1L;
            DictionaryDto dictionaryDto = createDictionary(true, authorId);
            Long dictionaryId = dictionaryDto.getId();

            String translationText = "Test translation 1";
            String originalText = "Origin translation 1";
            Translation translation = Translation.builder()
                    .translationText(translationText)
                    .originText(originalText)
                    .dictionaryId(dictionaryId)
                    .build();
            Translation persisted = translationRepository.save(translation);
            Long translationId = persisted.getId();

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

            String translationText = "Test translation 1";
            String originalText = "Origin translation 1";
            Translation translation = Translation.builder()
                    .translationText(translationText)
                    .originText(originalText)
                    .dictionaryId(dictionaryId)
                    .build();
            Translation persisted = translationRepository.save(translation);
            Long translationId = persisted.getId();

            GetTranslationRequestDto requestDto = GetTranslationRequestDto.builder()
                    .translationId(translationId)
                    .dictionaryId(dictionaryId)
                    .build();

            Long requestSenderId = 2L;
            assertThrows(NotAllowedException.class, () -> translationService.getTranslation(requestDto, requestSenderId));
        }
    }

    @Nested
    class CreateTranslation {

        @Test
        void createTranslationInOwnDictionary() {
            Long authorId = 1L;
            DictionaryDto dictionaryDto = createDictionary(true, authorId);

            String originalText = "Origin translation 1";
            String translationText = "Origin translation 1";
            Long dictionaryId = dictionaryDto.getId();
            Long requestSenderId = 1L;
            CreateTranslationRequestDto requestDto = CreateTranslationRequestDto.builder()
                    .originText(originalText)
                    .translationText(translationText)
                    .dictionaryId(dictionaryId)
                    .requestSenderId(requestSenderId)
                    .build();

            TranslationDto actualDto = translationService.createTranslation(requestDto);

            TranslationDto expectedDto = TranslationDto.builder()
                    .id(actualDto.getId())
                    .translationText(translationText)
                    .originText(originalText)
                    .dictionaryId(dictionaryId)
                    .build();

            assertTranslationsDtoEquals(expectedDto, actualDto);
        }

        @Test
        void createTranslationInNonOwnedDictionary() {
            Long authorId = 1L;
            DictionaryDto dictionaryDto = createDictionary(true, authorId);

            String originalText = "Origin translation 1";
            String translationText = "Origin translation 1";
            Long dictionaryId = dictionaryDto.getId();
            Long requestSenderId = 2L;
            CreateTranslationRequestDto requestDto = CreateTranslationRequestDto.builder()
                    .originText(originalText)
                    .translationText(translationText)
                    .dictionaryId(dictionaryId)
                    .requestSenderId(requestSenderId)
                    .build();

            assertThrows(NotAllowedException.class, () -> translationService.createTranslation(requestDto));
        }
    }

    @Nested
    class UpdateTranslation {

        @Test
        void updateTranslationInOwnDictionary() {
            Long authorId = 1L;
            DictionaryDto dictionaryDto = createDictionary(true, authorId);
            Long dictionaryId = dictionaryDto.getId();
            TranslationDto translationDto = addTranslationInDictionary(dictionaryDto.getId());
            Long translationId = translationDto.getId();

            String originalText = "Updated origin text";
            String translationText = "Updated translation text";
            Long requestSenderId = 1L;
            UpdateTranslationRequestDto requestDto = UpdateTranslationRequestDto.builder()
                    .originText(originalText)
                    .translationText(translationText)
                    .requestSenderId(requestSenderId)
                    .build();

            TranslationDto actualDto = translationService.updateTranslation(requestDto, dictionaryId, translationId);

            TranslationDto expectedDto = TranslationDto.builder()
                    .id(actualDto.getId())
                    .translationText(translationText)
                    .originText(originalText)
                    .dictionaryId(dictionaryId)
                    .build();

            assertTranslationsDtoEquals(expectedDto, actualDto);
        }

        @Test
        void updateTranslationInNonOwnedDictionary() {
            Long authorId = 1L;
            DictionaryDto dictionaryDto = createDictionary(true, authorId);
            Long dictionaryId = dictionaryDto.getId();
            TranslationDto translationDto = addTranslationInDictionary(dictionaryDto.getId());
            Long translationId = translationDto.getId();

            String originalText = "Updated origin text";
            String translationText = "Updated translation text";
            Long requestSenderId = 2L;
            UpdateTranslationRequestDto requestDto = UpdateTranslationRequestDto.builder()
                    .originText(originalText)
                    .translationText(translationText)
                    .requestSenderId(requestSenderId)
                    .build();

            assertThrows(NotAllowedException.class, () -> translationService.updateTranslation(requestDto, dictionaryId, translationId));
        }
    }
}
