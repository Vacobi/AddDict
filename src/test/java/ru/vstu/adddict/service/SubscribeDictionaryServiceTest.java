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
}