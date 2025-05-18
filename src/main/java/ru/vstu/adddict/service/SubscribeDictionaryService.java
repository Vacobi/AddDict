package ru.vstu.adddict.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vstu.adddict.dto.dictionary.DictionaryDto;
import ru.vstu.adddict.dto.dictionary.GetDictionaryRequestDto;
import ru.vstu.adddict.dto.subscribedictionary.CreateSubscribeDictionaryRequestDto;
import ru.vstu.adddict.dto.subscribedictionary.SubscribeDictionaryDto;
import ru.vstu.adddict.entity.subscribedictionary.SubscribeDictionary;
import ru.vstu.adddict.exception.NotAllowedException;
import ru.vstu.adddict.mapper.SubscribeDictionaryMapper;
import ru.vstu.adddict.repository.SubscribeDictionaryRepository;
import ru.vstu.adddict.validator.SubscribeDictionaryValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscribeDictionaryService {

    private final SubscribeDictionaryRepository subscribeDictionaryRepository;

    private final SubscribeDictionaryValidator subscribeDictionaryValidator;

    private final SubscribeDictionaryMapper subscribeDictionaryMapper;

    private final DictionaryService dictionaryService;

    @Transactional
    public SubscribeDictionaryDto subscribeDictionary(CreateSubscribeDictionaryRequestDto requestDto) {
        subscribeDictionaryValidator.validateCreateSubscribeDictionaryRequestDto(requestDto).ifPresent(e -> {
            throw e;
        });

        if (forbiddenToThisUser(requestDto.getDictionaryId(), requestDto.getUserId())) {
            String description = "User with id: " +
                    + requestDto.getUserId() +
                    " can't subscribe to dictionary with id: " +
                    requestDto.getDictionaryId();
            throw new NotAllowedException(description);
        }

        SubscribeDictionary subscribeDictionary = subscribeDictionaryMapper.toSubscribeDictionary(requestDto);
        SubscribeDictionary savedSubscribeDictionary = subscribeDictionaryRepository.save(subscribeDictionary);

        return subscribeDictionaryMapper.toDto(savedSubscribeDictionary);
    }

    private boolean forbiddenToThisUser(Long dictionaryId, Long userId) {
        GetDictionaryRequestDto dictionaryRequestDto = GetDictionaryRequestDto.builder()
                .id(dictionaryId)
                .requestSenderId(userId)
                .build();

        DictionaryDto dictionary;
        try {
            dictionary = dictionaryService.getDictionary(dictionaryRequestDto);
        } catch (NotAllowedException e) {
            return true;
        }

        return dictionary.isOwner(userId);
    }
}
