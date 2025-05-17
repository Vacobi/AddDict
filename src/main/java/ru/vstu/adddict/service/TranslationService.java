package ru.vstu.adddict.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.vstu.adddict.dto.*;
import ru.vstu.adddict.dto.dictionary.DictionaryDto;
import ru.vstu.adddict.dto.dictionary.GetDictionaryRequestDto;
import ru.vstu.adddict.dto.dictionary.GetDictionaryTranslationsRequestDto;
import ru.vstu.adddict.dto.dictionary.GetDictionaryTranslationsResponseDto;
import ru.vstu.adddict.dto.translation.CreateTranslationRequestDto;
import ru.vstu.adddict.dto.translation.GetTranslationRequestDto;
import ru.vstu.adddict.dto.translation.TranslationDto;
import ru.vstu.adddict.dto.translation.UpdateTranslationRequestDto;
import ru.vstu.adddict.entity.translation.Translation;
import ru.vstu.adddict.exception.NotAllowedException;
import ru.vstu.adddict.exception.TranslationNonExistException;
import ru.vstu.adddict.mapper.TranslationMapper;
import ru.vstu.adddict.repository.TranslationRepository;
import ru.vstu.adddict.validator.TranslationValidator;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranslationService {

    private final TranslationRepository translationRepository;

    private final TranslationValidator translationValidator;

    private final TranslationMapper translationMapper;

    private final DictionaryService dictionaryService;

    private final int translationsPageSize;

    @Transactional
    public TranslationDto getTranslation(GetTranslationRequestDto getDictionaryRequestDto, Long userId) {
        if (forbiddenToGetByUser(getDictionaryRequestDto.getDictionaryId(), userId)) {
            throw new NotAllowedException("Can't get translation in dictionary with id: "
                    + getDictionaryRequestDto.getDictionaryId()
                    + ". This dictionary is private and belongs to other user.");
        }

        Optional<Translation> optionalTranslationInRepos = getTranslation(getDictionaryRequestDto.getTranslationId(), getDictionaryRequestDto.getDictionaryId());

        if (optionalTranslationInRepos.isEmpty()) {
            throw new TranslationNonExistException(getDictionaryRequestDto.getDictionaryId(), getDictionaryRequestDto.getTranslationId());
        }

        Translation translation = optionalTranslationInRepos.get();

        return translationMapper.toTranslationDto(translation);
    }

    private Optional<Translation> getTranslation(Long id, Long dictID) {
        return translationRepository.getTranslationsByIdAndDictionaryId(id, dictID);
    }

    @Transactional
    public TranslationDto createTranslation(CreateTranslationRequestDto createTranslationRequestDto) {
        translationValidator.validateCreateTranslationRequest(createTranslationRequestDto).ifPresent(e -> {
            throw e;
        });

        if (forbiddenToChangeByUser(createTranslationRequestDto.getDictionaryId(), createTranslationRequestDto.getRequestSenderId())) {
            throw new NotAllowedException("Can't add translation to dictionary with id: "
                    + createTranslationRequestDto.getDictionaryId()
                    + ". This dictionary belongs to other user.");
        }

        Translation translation = translationMapper.toTranslation(createTranslationRequestDto);
        Translation savedTranslation = translationRepository.save(translation);

        return translationMapper.toTranslationDto(savedTranslation);
    }

    @Transactional
    public TranslationDto updateTranslation(UpdateTranslationRequestDto updateTranslationRequestDto, Long dictionaryId, Long translationId) {
        if (forbiddenToChangeByUser(dictionaryId, updateTranslationRequestDto.getRequestSenderId())) {
            throw new NotAllowedException("Can't update translation in dictionary with id: "
                    + dictionaryId
                    + ". This dictionary belongs to other user.");
        }

        translationValidator.validateUpdateTranslationRequest(updateTranslationRequestDto).ifPresent(e -> {
            throw e;
        });

        TranslationDto translationDto = updateTranslationInRepository(updateTranslationRequestDto, dictionaryId, translationId);

        return translationDto;
    }

    private TranslationDto updateTranslationInRepository(UpdateTranslationRequestDto updateTranslationRequestDto, Long dictionaryId, Long translationId) {
        try {
            Translation updated = translationRepository.updateWithLock(
                    updateTranslationRequestDto.getRequestSenderId(),
                    persisted -> {
                        persisted = translationMapper.fromUpdateRequest(persisted, updateTranslationRequestDto);
                        return persisted;
                    });

            return translationMapper.toTranslationDto(updated);
        } catch (NoSuchElementException e) {
            throw new TranslationNonExistException(dictionaryId, translationId);
        }
    }

    @Transactional
    public GetDictionaryTranslationsResponseDto<TranslationDto> getTranslations(GetDictionaryTranslationsRequestDto requestDto) {
        translationValidator.validateGetDictionaryTranslationsRequestDto(requestDto).ifPresent(e -> {
            throw e;
        });

        if (forbiddenToGetByUser(requestDto.getDictionaryId(), requestDto.getUserId())) {
            throw new NotAllowedException("Can't get translations in dictionary with id: "
                    + requestDto.getDictionaryId()
                    + ". This dictionary is private and belongs to other user.");
        }

        Page<Translation> page = getTranslationsPageByDictionaryId(requestDto.getDictionaryId(), requestDto.getPage());

        List<TranslationDto> translations = page
                .stream()
                .map(translationMapper::toTranslationDto)
                .toList();

        return GetDictionaryTranslationsResponseDto.<TranslationDto>builder()
                .dictionaryId(requestDto.getDictionaryId())
                .page(PageResponseDto.<TranslationDto>builder()
                        .content(translations)
                        .page(page.getNumber())
                        .pageSize(translations.size())
                        .totalElements(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .build()
                ).build();
    }

    private Page<Translation> getTranslationsPageByDictionaryId(long dictionaryId, int page) {
        return translationRepository.findTranslationsByDictionaryId(dictionaryId, PageRequest.of(page, translationsPageSize));
    }

    private boolean forbiddenToGetByUser(Long dictId, Long userId) {
        GetDictionaryRequestDto dictionaryRequestDto = GetDictionaryRequestDto.builder()
                .id(dictId)
                .requestSenderId(userId)
                .build();
        DictionaryDto dictionary = dictionaryService.getDictionary(dictionaryRequestDto);

        return !dictionary.getIsPublic() && !dictionary.isOwner(userId);
    }

    private boolean forbiddenToChangeByUser(Long dictId, Long userId) {
        GetDictionaryRequestDto dictionaryRequestDto = GetDictionaryRequestDto.builder()
                .id(dictId)
                .requestSenderId(userId)
                .build();

        DictionaryDto dictionary;
        try {
            dictionary = dictionaryService.getDictionary(dictionaryRequestDto);
        } catch (NotAllowedException e) {
            return true;
        }

        return !dictionary.isOwner(userId);
    }
}
