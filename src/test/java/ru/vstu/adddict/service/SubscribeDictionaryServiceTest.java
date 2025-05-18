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
import ru.vstu.adddict.dto.subscribedictionary.CreateSubscribeDictionaryRequestDto;
import ru.vstu.adddict.dto.subscribedictionary.SubscribeDictionaryDto;
import ru.vstu.adddict.dto.translation.CreateTranslationRequestDto;
import ru.vstu.adddict.dto.translation.TranslationDto;
import ru.vstu.adddict.exception.NotAllowedException;
import ru.vstu.adddict.mapper.SubscribeDictionaryMapper;
import ru.vstu.adddict.repository.SubscribeDictionaryRepository;
import ru.vstu.adddict.testutils.ClearableTest;

import static org.junit.jupiter.api.Assertions.*;
import static ru.vstu.adddict.testutils.TestAsserts.assertSubscribeDictionaryDtoEquals;

@SpringBootTest
@ContextConfiguration(initializers = TestContainersConfig.class)
@Slf4j
class SubscribeDictionaryServiceTest extends ClearableTest {

    @Autowired
    private SubscribeDictionaryService subscribeDictionaryService;

    @Autowired
    private SubscribeDictionaryRepository subscribeDictionaryRepository;

    @Autowired
    private SubscribeDictionaryMapper subscribeDictionaryMapper;

    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private TranslationService translationService;

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

    private SubscribeDictionaryDto getSubscribeDictionary(Long dictionaryId, Long userId) {
        CreateSubscribeDictionaryRequestDto requestDto = CreateSubscribeDictionaryRequestDto.builder()
                .userId(userId)
                .dictionaryId(dictionaryId)
                .build();

        return subscribeDictionaryService.subscribeDictionary(requestDto);
    }

    @Nested
    class SubscribeDictionaryTest {

        @Test
        void subscribeToPublicDictionary() {
            DictionaryDto dictionaryDto = createDictionary(true, 9999L);
            Long dictionaryId = dictionaryDto.getId();
            Long userId = 1L;
            CreateSubscribeDictionaryRequestDto requestDto = CreateSubscribeDictionaryRequestDto.builder()
                    .userId(userId)
                    .dictionaryId(dictionaryId)
                    .build();

            SubscribeDictionaryDto actualDto = subscribeDictionaryService.subscribeDictionary(requestDto);

            SubscribeDictionaryDto expectedDto = SubscribeDictionaryDto.builder()
                    .id(actualDto.getId())
                    .userId(userId)
                    .dictionaryId(dictionaryId)
                    .build();

            assertNotNull(actualDto.getId());
            assertSubscribeDictionaryDtoEquals(expectedDto, actualDto);
            SubscribeDictionaryDto dtoInRepos = subscribeDictionaryMapper.toDto(subscribeDictionaryRepository.findById(actualDto.getId()).get());
            assertEquals(expectedDto, dtoInRepos);
        }

        @Test
        void subscribeToPrivateDictionary() {
            DictionaryDto dictionaryDto = createDictionary(false, 9999L);
            Long dictionaryId = dictionaryDto.getId();
            Long userId = 1L;
            CreateSubscribeDictionaryRequestDto requestDto = CreateSubscribeDictionaryRequestDto.builder()
                    .userId(userId)
                    .dictionaryId(dictionaryId)
                    .build();

            assertThrows(NotAllowedException.class, () -> subscribeDictionaryService.subscribeDictionary(requestDto));
        }

        @Test
        void subscribeToOwnDictionary() {
            Long userId = 1L;
            DictionaryDto dictionaryDto = createDictionary(false, userId);
            Long dictionaryId = dictionaryDto.getId();
            CreateSubscribeDictionaryRequestDto requestDto = CreateSubscribeDictionaryRequestDto.builder()
                    .userId(userId)
                    .dictionaryId(dictionaryId)
                    .build();

            assertThrows(NotAllowedException.class, () -> subscribeDictionaryService.subscribeDictionary(requestDto));
        }
    }

    @Nested
    class UnsubscribeDictionaryTest {

        @Test
        void unsubscribeFromDictionary() {
            DictionaryDto dictionaryDto = createDictionary(true, 9999L);
            Long userId = 1L;
            boolean expectedDeleted = true;

            SubscribeDictionaryDto subscribeDictionaryDto = getSubscribeDictionary(dictionaryDto.getId(), userId);

            long subscribesDictionariesCountBeforeDelete = subscribeDictionaryRepository.count();
            boolean actualDeleted = subscribeDictionaryService.unsubscribeToDictionary(subscribeDictionaryDto.getId(), userId);
            long subscribesDictionariesCountAfterDelete = subscribeDictionaryRepository.count();

            assertEquals(expectedDeleted, actualDeleted);
            assertEquals(subscribesDictionariesCountBeforeDelete - 1, subscribesDictionariesCountAfterDelete);
            assertTrue(subscribeDictionaryRepository.findById(subscribeDictionaryDto.getId()).isEmpty());
        }

        @Test
        void unsubscribeFromDictionaryByOtherUserRequest() {
            DictionaryDto dictionaryDto = createDictionary(true, 9999L);
            Long userId = 1L;

            SubscribeDictionaryDto subscribeDictionaryDto = getSubscribeDictionary(dictionaryDto.getId(), userId);

            long subscribesDictionariesCountBeforeDelete = subscribeDictionaryRepository.count();
            assertThrows(NotAllowedException.class, () -> subscribeDictionaryService.unsubscribeToDictionary(subscribeDictionaryDto.getId(), userId + 1));
            long subscribesDictionariesCountAfterDelete = subscribeDictionaryRepository.count();

            assertEquals(subscribesDictionariesCountBeforeDelete, subscribesDictionariesCountAfterDelete);
            assertTrue(subscribeDictionaryRepository.findById(subscribeDictionaryDto.getId()).isPresent());
        }
    }

    @Nested
    class UnsubscribeDictionaryByDictionaryIdTest {
        @Test
        void unsubscribeFromDictionary() {
            DictionaryDto dictionaryDto = createDictionary(true, 9999L);
            Long userId = 1L;
            boolean expectedDeleted = true;

            SubscribeDictionaryDto subscribeDictionaryDto = getSubscribeDictionary(dictionaryDto.getId(), userId);

            long subscribesDictionariesCountBeforeDelete = subscribeDictionaryRepository.count();
            boolean actualDeleted = subscribeDictionaryService.unsubscribeToDictionaryByDictionaryId(dictionaryDto.getId(), userId);
            long subscribesDictionariesCountAfterDelete = subscribeDictionaryRepository.count();

            assertEquals(expectedDeleted, actualDeleted);
            assertEquals(subscribesDictionariesCountBeforeDelete - 1, subscribesDictionariesCountAfterDelete);
            assertTrue(subscribeDictionaryRepository.findById(subscribeDictionaryDto.getId()).isEmpty());
        }
    }
}