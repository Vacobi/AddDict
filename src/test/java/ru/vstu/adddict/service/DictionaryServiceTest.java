package ru.vstu.adddict.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import ru.vstu.adddict.config.TestContainersConfig;
import ru.vstu.adddict.dto.*;
import ru.vstu.adddict.mapper.DictionaryMapper;
import ru.vstu.adddict.repository.DictionariesRepository;
import ru.vstu.adddict.testutils.ClearableTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static ru.vstu.adddict.testutils.TestAsserts.assertDictionariesDtoEquals;

@SpringBootTest
@ContextConfiguration(initializers = TestContainersConfig.class)
@Slf4j
class DictionaryServiceTest {

    @Autowired
    private DictionariesRepository dictionariesRepository;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Nested
    class CreateDictionaryTest extends ClearableTest {

        @Test
        void createDictionary() {
            LocalDateTime now = LocalDateTime.now();
            String name = "test";
            String description = "test description";
            boolean isPublic = true;
            final Long authorId = 1L;
            CreateDictionaryRequestDto requestDto = CreateDictionaryRequestDto.builder()
                    .name(name)
                    .description(description)
                    .isPublic(isPublic)
                    .authorId(authorId)
                    .build();

            DictionaryDto actualDto = dictionaryService.createDictionary(requestDto);

            DictionaryDto expectedDto = DictionaryDto.builder()
                    .id(actualDto.getId())
                    .name(name)
                    .description(description)
                    .isPublic(isPublic)
                    .createdAt(now)
                    .authorId(authorId)
                    .build();

            assertNotNull(actualDto.getId());
            assertDictionariesDtoEquals(expectedDto, actualDto);
            DictionaryDto actualDtoInRepos = dictionaryMapper.toDto(dictionariesRepository.findById(actualDto.getId()).get());
            assertDictionariesDtoEquals(expectedDto, actualDtoInRepos);
        }
    }
}