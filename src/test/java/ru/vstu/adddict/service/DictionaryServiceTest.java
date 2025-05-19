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
import ru.vstu.adddict.dto.dictionary.GetDictionaryRequestDto;
import ru.vstu.adddict.dto.dictionary.UpdateDictionaryRequestDto;
import ru.vstu.adddict.entity.dictionary.Dictionary;
import ru.vstu.adddict.entity.subscribedictionary.SubscribeDictionary;
import ru.vstu.adddict.entity.translation.Translation;
import ru.vstu.adddict.exception.NotAllowedException;
import ru.vstu.adddict.mapper.DictionaryMapper;
import ru.vstu.adddict.repository.DictionariesRepository;
import ru.vstu.adddict.repository.SubscribeDictionaryRepository;
import ru.vstu.adddict.repository.TranslationRepository;
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

    @Nested
    class GetDictionaryTest extends ClearableTest {

        @Test
        void getDictionary() {

            final Long authorId = 1L;
            LocalDateTime now = LocalDateTime.now();

            Dictionary dictionaryToPersist = Dictionary.builder()
                    .name("test")
                    .description("test description")
                    .isPublic(true)
                    .createdAt(now)
                    .authorId(authorId)
                    .build();

            Dictionary persistedDictionary = dictionariesRepository.save(dictionaryToPersist);
            final Long dictionaryId = persistedDictionary.getId();

            Dictionary expectedEntity = Dictionary.builder()
                    .id(dictionaryId)
                    .name("test")
                    .description("test description")
                    .isPublic(true)
                    .createdAt(now)
                    .authorId(authorId)
                    .build();

            DictionaryDto expectedDto = DictionaryDto.builder()
                    .id(dictionaryId)
                    .name("test")
                    .description("test description")
                    .isPublic(true)
                    .createdAt(now)
                    .authorId(authorId)
                    .build();

            GetDictionaryRequestDto requestDto = GetDictionaryRequestDto.builder()
                    .id(dictionaryId)
                    .requestSenderId(authorId)
                    .build();

            DictionaryDto actualDto = dictionaryService.getDictionary(requestDto);

            assertEquals(expectedEntity, persistedDictionary);
            assertDictionariesDtoEquals(expectedDto, actualDto);
        }
    }

    @Nested
    class UpdateDictionaryTest extends ClearableTest {

        DictionaryDto generateDictionaryToRepos(Long authorId) {

            String name = "test";
            String description = "test description";
            boolean isPublic = true;
            CreateDictionaryRequestDto createDictionaryRequestDto = CreateDictionaryRequestDto.builder()
                    .name(name)
                    .description(description)
                    .isPublic(isPublic)
                    .authorId(authorId)
                    .build();

            return dictionaryService.createDictionary(createDictionaryRequestDto);
        }

        @Test
        void updateEveryFieldInDictionaryExceptAuthor() {
            long authorId = 1L;
            DictionaryDto dictionaryDtoBeforeUpdate = generateDictionaryToRepos(authorId);

            String name = "new test name";
            String description = "new test description";
            boolean isPublic = false;
            UpdateDictionaryRequestDto requestDto = UpdateDictionaryRequestDto.builder()
                    .name(name)
                    .description(description)
                    .isPublic(isPublic)
                    .requestSenderId(authorId)
                    .build();
            Long dictionaryId = dictionaryDtoBeforeUpdate.getId();

            DictionaryDto expDto = dictionaryMapper.toDto(dictionariesRepository.findById(dictionaryId).get());
            expDto.setName(name);
            expDto.setDescription(description);
            expDto.setIsPublic(isPublic);


            long countOfDictionariesInReposBeforeUpdate = dictionariesRepository.count();
            DictionaryDto actualDto = dictionaryService.updateDictionary(dictionaryId, requestDto);
            long countOfDictionariesInReposAfterUpdate = dictionariesRepository.count();


            assertDictionariesDtoEquals(expDto, actualDto);
            DictionaryDto actualDtoInRepos = dictionaryMapper.toDto(dictionariesRepository.findById(dictionaryId).get());
            assertDictionariesDtoEquals(expDto, actualDtoInRepos);
            assertEquals(countOfDictionariesInReposBeforeUpdate, countOfDictionariesInReposAfterUpdate);
        }
    }

    @Nested
    class DeleteDictionaryTest extends ClearableTest {
        @Autowired
        private TranslationRepository translationRepository;

        @Autowired
        private SubscribeDictionaryRepository subscribeDictionaryRepository;

        DictionaryDto generateDictionaryToRepos(Long authorId) {

            String name = "test";
            String description = "test description";
            boolean isPublic = true;
            CreateDictionaryRequestDto createDictionaryRequestDto = CreateDictionaryRequestDto.builder()
                    .name(name)
                    .description(description)
                    .isPublic(isPublic)
                    .authorId(authorId)
                    .build();

            return dictionaryService.createDictionary(createDictionaryRequestDto);
        }

        @Test
        void deleteDictionaryByAuthor() {
            final Long authorId = 1L;
            LocalDateTime now = LocalDateTime.now();

            Dictionary dictionaryToPersist = Dictionary.builder()
                    .name("test")
                    .description("test description")
                    .isPublic(true)
                    .createdAt(now)
                    .authorId(authorId)
                    .build();

            Dictionary persistedDictionary = dictionariesRepository.save(dictionaryToPersist);
            final Long dictionaryId = persistedDictionary.getId();


            boolean expectedDeleted = true;


            long countOfDictionariesInReposBeforeUpdate = dictionariesRepository.count();
            boolean actualDeleted = dictionaryService.deleteDictionary(dictionaryId, authorId);
            long countOfDictionariesInReposAfterUpdate = dictionariesRepository.count();


            assertEquals(expectedDeleted, actualDeleted);
            assertEquals(countOfDictionariesInReposBeforeUpdate - 1, countOfDictionariesInReposAfterUpdate);
            assertEquals(0, dictionariesRepository.getDictionaryById(dictionaryId).size());
        }

        @Test
        void deleteDictionaryByOtherUser() {
            final Long authorId = 1L;
            LocalDateTime now = LocalDateTime.now();

            Dictionary dictionaryToPersist = Dictionary.builder()
                    .name("test")
                    .description("test description")
                    .isPublic(true)
                    .createdAt(now)
                    .authorId(authorId)
                    .build();

            Dictionary persistedDictionary = dictionariesRepository.save(dictionaryToPersist);
            final Long dictionaryId = persistedDictionary.getId();
            final Long requestSenderId = 2L;


            long countOfDictionariesInReposBeforeUpdate = dictionariesRepository.count();
            assertThrows(
                    NotAllowedException.class,
                    () -> dictionaryService.deleteDictionary(dictionaryId, requestSenderId)
            );
            long countOfDictionariesInReposAfterUpdate = dictionariesRepository.count();


            assertEquals(countOfDictionariesInReposBeforeUpdate, countOfDictionariesInReposAfterUpdate);
            assertEquals(1, dictionariesRepository.getDictionaryById(dictionaryId).size());
        }

        @Test
        void cascadeDelete() {
            final Long authorId = 1L;
            LocalDateTime now = LocalDateTime.now();
            Dictionary dictionaryToPersist = Dictionary.builder()
                    .name("test")
                    .description("test description")
                    .isPublic(true)
                    .createdAt(now)
                    .authorId(authorId)
                    .build();

            Dictionary persistedDictionary = dictionariesRepository.save(dictionaryToPersist);
            final Long dictionaryId = persistedDictionary.getId();

            Translation translationToPersist = Translation.builder()
                    .originText("originText")
                    .translationText("translationText")
                    .dictionaryId(dictionaryId)
                    .build();
            Translation persistedTranslation = translationRepository.save(translationToPersist);

            SubscribeDictionary subscribeDictionaryToPersist = SubscribeDictionary.builder()
                    .dictionaryId(dictionaryId)
                    .userId(authorId + 1)
                    .build();
            SubscribeDictionary persistedSubscribeDictionary = subscribeDictionaryRepository.save(subscribeDictionaryToPersist);

            boolean expectedDeleted = true;


            long countOfDictionariesInReposBeforeDelete = dictionariesRepository.count();
            long countOfTranslationsInReposBeforeDelete = translationRepository.count();
            long countOfSubscribesInReposBeforeDelete = subscribeDictionaryRepository.count();
            boolean actualDeleted = dictionaryService.deleteDictionary(dictionaryId, authorId);
            long countOfDictionariesInReposAfterDelete = dictionariesRepository.count();
            long countOfTranslationsInReposAfterDelete = translationRepository.count();
            long countOfSubscribesInReposAfterDelete = subscribeDictionaryRepository.count();


            assertEquals(expectedDeleted, actualDeleted);
            assertEquals(countOfDictionariesInReposBeforeDelete - 1, countOfDictionariesInReposAfterDelete);
            assertEquals(countOfTranslationsInReposBeforeDelete - 1, countOfTranslationsInReposAfterDelete);
            assertEquals(countOfSubscribesInReposBeforeDelete - 1, countOfSubscribesInReposAfterDelete);
            assertTrue(dictionariesRepository.findById(dictionaryId).isEmpty());
            assertTrue(translationRepository.findById(persistedTranslation.getId()).isEmpty());
            assertTrue(subscribeDictionaryRepository.findById(persistedSubscribeDictionary.getId()).isEmpty());
        }
    }
}
