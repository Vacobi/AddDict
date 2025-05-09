package ru.vstu.adddict.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vstu.adddict.dto.CreateDictionaryRequestDto;
import ru.vstu.adddict.dto.DictionaryDto;
import ru.vstu.adddict.entity.Dictionary;
import ru.vstu.adddict.mapper.DictionaryMapper;
import ru.vstu.adddict.repository.DictionariesRepository;
import ru.vstu.adddict.validator.DictionaryValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class DictionaryService {

    private final DictionaryValidator dictionaryValidator;

    private final DictionaryMapper dictionaryMapper;

    private final DictionariesRepository dictionariesRepository;

    @Transactional
    public DictionaryDto createDictionary(CreateDictionaryRequestDto createDictionaryRequest) {
        dictionaryValidator.validateCreateDictionaryRequest(createDictionaryRequest).ifPresent(e -> {
                throw e;
        });

        Dictionary dictionary = dictionaryMapper.toDictionary(createDictionaryRequest);
        Dictionary savedDictionary = dictionariesRepository.save(dictionary);

        return dictionaryMapper.toDto(savedDictionary);
    }
}
