package ru.vstu.adddict.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vstu.adddict.dto.PageResponseDto;
import ru.vstu.adddict.dto.dictionary.DictionaryDto;
import ru.vstu.adddict.dto.dictionary.GetDictionaryRequestDto;
import ru.vstu.adddict.dto.subscribedictionary.*;
import ru.vstu.adddict.entity.subscribedictionary.SubscribeDictionary;
import ru.vstu.adddict.exception.NotAllowedException;
import ru.vstu.adddict.exception.SubscribeDictionaryAlreadyExists;
import ru.vstu.adddict.exception.SubscribeNonExistsException;
import ru.vstu.adddict.mapper.SubscribeDictionaryMapper;
import ru.vstu.adddict.repository.SubscribeDictionaryRepository;
import ru.vstu.adddict.validator.SubscribeDictionaryValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscribeDictionaryService {

    private final SubscribeDictionaryRepository subscribeDictionaryRepository;

    private final SubscribeDictionaryValidator subscribeDictionaryValidator;

    private final SubscribeDictionaryMapper subscribeDictionaryMapper;

    private final DictionaryService dictionaryService;

    private final int subscribesDictionariesPageSize;

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

        if (alreadySubscribed(requestDto)) {
            throw new SubscribeDictionaryAlreadyExists(requestDto.getDictionaryId(), requestDto.getUserId());
        }

        SubscribeDictionary subscribeDictionary = subscribeDictionaryMapper.toSubscribeDictionary(requestDto);
        SubscribeDictionary savedSubscribeDictionary = subscribeDictionaryRepository.save(subscribeDictionary);

        return subscribeDictionaryMapper.toDto(savedSubscribeDictionary);
    }

    private boolean alreadySubscribed(CreateSubscribeDictionaryRequestDto requestDto) {
        Optional<SubscribeDictionary> subscribeDictionary =
                subscribeDictionaryRepository.findByUserIdAndDictionaryId(requestDto.getUserId(), requestDto.getDictionaryId());
        return subscribeDictionary.isPresent();
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

    @Transactional
    public boolean unsubscribeToDictionary(Long id, Long userId) {
        Optional<SubscribeDictionary> optionalSubscribeDictionary = getSubscribeDictionary(id);

        if (optionalSubscribeDictionary.isEmpty()) {
            throw new SubscribeNonExistsException(id);
        }

        SubscribeDictionary subscribeDictionary = optionalSubscribeDictionary.get();

        if (!subscribeDictionary.getUserId().equals(userId)) {
            String description = "Subscribe with id: " +
                    id +
                    " belongs to other user";
            throw new NotAllowedException(description);
        }

        subscribeDictionaryRepository.deleteById(id);

        return true;
    }

    private Optional<SubscribeDictionary> getSubscribeDictionary(Long id) {
        return subscribeDictionaryRepository.findById(id);
    }

    @Transactional
    public boolean unsubscribeToDictionaryByDictionaryId(Long dictionaryId, Long userId) {
        Optional<SubscribeDictionary> optionalSubscribeDictionary = getSubscribeDictionary(dictionaryId, userId);

        if (optionalSubscribeDictionary.isEmpty()) {
            throw new SubscribeNonExistsException(userId, dictionaryId);
        }

        SubscribeDictionary subscribeDictionary = optionalSubscribeDictionary.get();

        if (!subscribeDictionary.getUserId().equals(userId)) {
            String description = "Subscribe with id: " +
                    subscribeDictionary.getId() +
                    " belongs to other user";
            throw new NotAllowedException(description);
        }

        subscribeDictionaryRepository.deleteById(subscribeDictionary.getId());

        return true;
    }

    private Optional<SubscribeDictionary> getSubscribeDictionary(Long dictionaryId, Long subscriberId) {
        return subscribeDictionaryRepository.findByDictionaryIdAndUserId(dictionaryId, subscriberId);
    }

    public GetUserSubscribesDictionariesResponseDto<SubscribeDictionaryDto> getUserSubscribes(GetUserSubscribesDictionariesRequestDto requestDto) {
        subscribeDictionaryValidator.validateGetUserSubscribesDictionariesRequestDto(requestDto).ifPresent(e -> {
            throw e;
        });

        Page<SubscribeDictionary> page =
                subscribeDictionaryRepository.getSubscribeDictionariesByUserId(requestDto.getUserId(), PageRequest.of(requestDto.getPage(), subscribesDictionariesPageSize));

        List<SubscribeDictionaryDto> subscribeDictionaries = page
                .stream()
                .map(subscribeDictionaryMapper::toDto)
                .collect(Collectors.toList());

        return GetUserSubscribesDictionariesResponseDto.<SubscribeDictionaryDto>builder()
                .userId(requestDto.getUserId())
                .page(PageResponseDto.<SubscribeDictionaryDto>builder()
                        .content(subscribeDictionaries)
                        .page(page.getNumber())
                        .pageSize(subscribeDictionaries.size())
                        .totalElements(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .build()
                ).build();
    }

    public boolean subscribedToDictionary(Long dictionaryId, Long userId) {
        return subscribeDictionaryRepository.findByDictionaryIdAndUserId(dictionaryId, userId).isPresent();
    }
}
