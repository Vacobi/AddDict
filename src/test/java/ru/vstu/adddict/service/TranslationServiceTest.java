package ru.vstu.adddict.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.relational.core.sql.In;
import org.springframework.test.context.ContextConfiguration;
import ru.vstu.adddict.config.TestContainersConfig;
import ru.vstu.adddict.dto.dictionary.CreateDictionaryRequestDto;
import ru.vstu.adddict.dto.dictionary.DictionaryDto;
import ru.vstu.adddict.dto.translation.*;
import ru.vstu.adddict.entity.translation.Translation;
import ru.vstu.adddict.exception.DictionaryNonExistException;
import ru.vstu.adddict.exception.NotAllowedDictionaryException;
import ru.vstu.adddict.exception.NotAllowedException;
import ru.vstu.adddict.mapper.TranslationMapper;
import ru.vstu.adddict.repository.TranslationRepository;
import ru.vstu.adddict.testutils.ClearableTest;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.vstu.adddict.testutils.TestAsserts.assertListOfTranslationsContainsSameTranslations;
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

    @Nested
    class DeleteTranslation {

        @Test
        void deleteTranslationInOwnDictionary() {
            Long authorId = 1L;
            DictionaryDto dictionaryDto = createDictionary(true, authorId);
            Long dictionaryId = dictionaryDto.getId();
            TranslationDto translationDto = addTranslationInDictionary(dictionaryDto.getId());
            Long translationId = translationDto.getId();
            boolean expectedDeleted = true;

            Long requestSenderId = 1L;

            long translationsInReposBeforeDelete = translationRepository.count();
            boolean actualDeleted = translationService.deleteTranslation(dictionaryId, translationId, requestSenderId);
            long translationsInReposAfterDelete = translationRepository.count();

            assertEquals(expectedDeleted, actualDeleted);
            assertEquals(translationsInReposBeforeDelete - 1, translationsInReposAfterDelete);
            assertEquals(0, translationRepository.getTranslationsById(translationId).size());
        }

        @Test
        void deleteTranslationInNonOwnedDictionary() {
            Long authorId = 1L;
            DictionaryDto dictionaryDto = createDictionary(true, authorId);
            Long dictionaryId = dictionaryDto.getId();
            TranslationDto translationDto = addTranslationInDictionary(dictionaryDto.getId());
            Long translationId = translationDto.getId();

            Long requestSenderId = 2L;

            long translationsInReposBeforeDelete = translationRepository.count();
            assertThrows(NotAllowedException.class,
                    () -> translationService.deleteTranslation(dictionaryId, translationId, requestSenderId)
            );
            long translationsInReposAfterDelete = translationRepository.count();

            assertEquals(translationsInReposBeforeDelete, translationsInReposAfterDelete);
            assertEquals(1, translationRepository.getTranslationsById(translationId).size());
        }
    }

    @Nested
    class ShuffleTest {

        @Autowired
        private int shuffleParticionPageSize;

        private List<TranslationDto> fillDictionary(Long dictionaryId, int count) {
            List<TranslationDto> translationDtos = new LinkedList<>();

            for (int i = 0; i < count; i++) {
                translationDtos.add(addTranslationInDictionary(dictionaryId));

            }

            return translationDtos;
        }

        @Test
        void shuffleInOneOwnPublicDictionary() {
            Long authorId = 1L;
            DictionaryDto dictionaryDto = createDictionary(true, authorId);

            List<TranslationDto> expectedTranslations = new LinkedList<>();
            expectedTranslations.addAll(fillDictionary(dictionaryDto.getId(), shuffleParticionPageSize / 2));

            List<TranslationDto> actualTranslations = new LinkedList<>();
            long pageSize;
            int currentPage = 0;
            do {
                ShuffleRequestDto requestDto = ShuffleRequestDto.builder()
                        .dictionaryIds(List.of(dictionaryDto.getId()))
                        .page(currentPage++)
                        .userId(authorId)
                        .build();
                ShuffleResponseDto<TranslationDto> shuffleResponseDto = translationService.getShuffledTranslations(requestDto);
                actualTranslations.addAll(shuffleResponseDto.getPage().getContent());
                pageSize = shuffleResponseDto.getPage().getPageSize();
            } while (pageSize != 0);

            assertListOfTranslationsContainsSameTranslations(expectedTranslations, actualTranslations);
        }

        @Test
        void shuffleInOneOwnPrivateDictionary() {
            Long authorId = 1L;
            DictionaryDto dictionaryDto = createDictionary(false, authorId);

            List<TranslationDto> expectedTranslations = new LinkedList<>();
            expectedTranslations.addAll(fillDictionary(dictionaryDto.getId(), shuffleParticionPageSize / 2));

            List<TranslationDto> actualTranslations = new LinkedList<>();
            long pageSize;
            int currentPage = 0;
            do {
                ShuffleRequestDto requestDto = ShuffleRequestDto.builder()
                        .dictionaryIds(List.of(dictionaryDto.getId()))
                        .page(currentPage++)
                        .userId(authorId)
                        .build();
                ShuffleResponseDto<TranslationDto> shuffleResponseDto = translationService.getShuffledTranslations(requestDto);
                actualTranslations.addAll(shuffleResponseDto.getPage().getContent());
                pageSize = shuffleResponseDto.getPage().getPageSize();
            } while (pageSize != 0);

            assertListOfTranslationsContainsSameTranslations(expectedTranslations, actualTranslations);
        }

        @Test
        void shuffleInOneOwnPublicDictionaryWithFewPages() {
            Long authorId = 1L;
            DictionaryDto dictionaryDto = createDictionary(true, authorId);

            List<TranslationDto> expectedTranslations = new LinkedList<>();
            expectedTranslations.addAll(fillDictionary(dictionaryDto.getId(), shuffleParticionPageSize * 3));

            List<TranslationDto> actualTranslations = new LinkedList<>();
            long pageSize;
            int currentPage = 0;
            do {
                ShuffleRequestDto requestDto = ShuffleRequestDto.builder()
                        .dictionaryIds(List.of(dictionaryDto.getId()))
                        .page(currentPage++)
                        .userId(authorId)
                        .build();
                ShuffleResponseDto<TranslationDto> shuffleResponseDto = translationService.getShuffledTranslations(requestDto);
                actualTranslations.addAll(shuffleResponseDto.getPage().getContent());
                pageSize = shuffleResponseDto.getPage().getPageSize();
            } while (pageSize != 0);

            assertListOfTranslationsContainsSameTranslations(expectedTranslations, actualTranslations);
        }

        @Test
        void shuffleInFewOwnDictionariesInOnePage() {
            List<Long> dictionaryIds = new LinkedList<>();
            List<TranslationDto> expectedTranslations = new LinkedList<>();

            Long authorId = 1L;
            dictionaryIds.add(createDictionary(true, authorId).getId());
            dictionaryIds.add(createDictionary(true, authorId).getId());
            dictionaryIds.add(createDictionary(true, authorId).getId());

            for (Long id : dictionaryIds) {
                expectedTranslations.addAll(fillDictionary(id, shuffleParticionPageSize / 3));
            }


            List<TranslationDto> actualTranslations = new LinkedList<>();
            long pageSize;
            int currentPage = 0;
            do {
                ShuffleRequestDto requestDto = ShuffleRequestDto.builder()
                        .dictionaryIds(dictionaryIds)
                        .page(currentPage++)
                        .userId(authorId)
                        .build();
                ShuffleResponseDto<TranslationDto> shuffleResponseDto = translationService.getShuffledTranslations(requestDto);
                actualTranslations.addAll(shuffleResponseDto.getPage().getContent());
                pageSize = shuffleResponseDto.getPage().getPageSize();
            } while (pageSize != 0);


            assertListOfTranslationsContainsSameTranslations(expectedTranslations, actualTranslations);
        }

        @Test
        void shuffleInFewOwnDictionariesInFewPages() {
            List<Long> dictionaryIds = new LinkedList<>();
            List<TranslationDto> expectedTranslations = new LinkedList<>();

            Long authorId = 1L;
            dictionaryIds.add(createDictionary(true, authorId).getId());
            dictionaryIds.add(createDictionary(true, authorId).getId());
            dictionaryIds.add(createDictionary(true, authorId).getId());

            for (Long id : dictionaryIds) {
                expectedTranslations.addAll(fillDictionary(id, shuffleParticionPageSize * 2));
            }


            List<TranslationDto> actualTranslations = new LinkedList<>();
            long pageSize;
            int currentPage = 0;
            do {
                ShuffleRequestDto requestDto = ShuffleRequestDto.builder()
                        .dictionaryIds(dictionaryIds)
                        .page(currentPage++)
                        .userId(authorId)
                        .build();
                ShuffleResponseDto<TranslationDto> shuffleResponseDto = translationService.getShuffledTranslations(requestDto);
                actualTranslations.addAll(shuffleResponseDto.getPage().getContent());
                pageSize = shuffleResponseDto.getPage().getPageSize();
            } while (pageSize != 0);


            assertListOfTranslationsContainsSameTranslations(expectedTranslations, actualTranslations);
        }

        @Test
        void shuffleInOneNonOwnedPublicDictionary() {
            Long authorId = 1L;
            DictionaryDto dictionaryDto = createDictionary(true, authorId);

            Long requestSenderId = 2L;
            List<TranslationDto> expectedTranslations = new LinkedList<>();
            expectedTranslations.addAll(fillDictionary(dictionaryDto.getId(), shuffleParticionPageSize / 2));

            List<TranslationDto> actualTranslations = new LinkedList<>();
            long pageSize;
            int currentPage = 0;
            do {
                ShuffleRequestDto requestDto = ShuffleRequestDto.builder()
                        .dictionaryIds(List.of(dictionaryDto.getId()))
                        .page(currentPage++)
                        .userId(requestSenderId)
                        .build();
                ShuffleResponseDto<TranslationDto> shuffleResponseDto = translationService.getShuffledTranslations(requestDto);
                actualTranslations.addAll(shuffleResponseDto.getPage().getContent());
                pageSize = shuffleResponseDto.getPage().getPageSize();
            } while (pageSize != 0);

            assertListOfTranslationsContainsSameTranslations(expectedTranslations, actualTranslations);
        }

        @Test
        void shuffleInOneNonOwnedPrivateDictionary() {
            Long authorId = 1L;
            DictionaryDto dictionaryDto = createDictionary(false, authorId);
            Long requestSenderId = 2L;

            ShuffleRequestDto requestDto = ShuffleRequestDto.builder()
                    .dictionaryIds(List.of(dictionaryDto.getId()))
                    .page(0)
                    .userId(requestSenderId)
                    .build();

            assertThrows(NotAllowedDictionaryException.class, () -> translationService.getShuffledTranslations(requestDto));
        }

        @Test
        void shuffleInFewNonOwnedDictionariesOneIsPrivate() {
            Long authorId = 1L;
            Long requestSenderId = 2L;

            List<Long> dictionaryIds = new LinkedList<>();
            dictionaryIds.add(createDictionary(true, authorId).getId());
            dictionaryIds.add(createDictionary(false, authorId).getId());
            dictionaryIds.add(createDictionary(true, authorId).getId());

            ShuffleRequestDto requestDto = ShuffleRequestDto.builder()
                    .dictionaryIds(dictionaryIds)
                    .page(0)
                    .userId(requestSenderId)
                    .build();

            assertThrows(NotAllowedDictionaryException.class, () -> translationService.getShuffledTranslations(requestDto));
        }

        @Test
        void shuffleInOneNonExistingDictionary() {
            Long requestSenderId = 2L;
            Long dictionaryId = Long.MAX_VALUE;

            ShuffleRequestDto requestDto = ShuffleRequestDto.builder()
                    .dictionaryIds(List.of(dictionaryId))
                    .page(0)
                    .userId(requestSenderId)
                    .build();

            assertThrows(DictionaryNonExistException.class, () -> translationService.getShuffledTranslations(requestDto));
        }

        @Test
        void shuffleInFewDictionariesOneIsNonExisting() {
            Long authorId = 1L;
            Long requestSenderId = 2L;

            List<Long> dictionaryIds = new LinkedList<>();
            dictionaryIds.add(createDictionary(true, authorId).getId());
            dictionaryIds.add(Long.MAX_VALUE);
            dictionaryIds.add(createDictionary(true, authorId).getId());

            ShuffleRequestDto requestDto = ShuffleRequestDto.builder()
                    .dictionaryIds(dictionaryIds)
                    .page(0)
                    .userId(requestSenderId)
                    .build();

            assertThrows(DictionaryNonExistException.class, () -> translationService.getShuffledTranslations(requestDto));
        }
    }
}
