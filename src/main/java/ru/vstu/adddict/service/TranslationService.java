package ru.vstu.adddict.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.vstu.adddict.dto.*;
import ru.vstu.adddict.entity.Translation;
import ru.vstu.adddict.exception.NotAllowedException;
import ru.vstu.adddict.exception.TranslationNonExistException;
import ru.vstu.adddict.mapper.TranslationMapper;
import ru.vstu.adddict.repository.TranslationRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranslationService {

    private final TranslationRepository translationRepository;

    private final TranslationMapper translationMapper;

    private final DictionaryService dictionaryService;

    private boolean forbiddenToUser(Long dictId, Long userId) {
        GetDictionaryRequestDto dictionaryRequestDto = GetDictionaryRequestDto.builder()
                .id(dictId)
                .requestSenderId(userId)
                .build();
        DictionaryDto dictionary = dictionaryService.getDictionary(dictionaryRequestDto);

        return !dictionary.isOwner(userId);
    }

    @Transactional
    public TranslationDto getTranslation(GetTranslationRequestDto getDictionaryRequestDto, Long userId) {
        if (forbiddenToUser(getDictionaryRequestDto.getDictionaryId(), userId)) {
            throw new NotAllowedException("Can't get translation in dictionary with id: "
                    + getDictionaryRequestDto.getDictionaryId()
                    + ". This dictionary is private and belongs to other user.");
        }

        Optional<Translation> optionalTranslationInRepos = getTranslation(getDictionaryRequestDto.getTranslationId(), userId);

        if (optionalTranslationInRepos.isEmpty()) {
            throw new TranslationNonExistException(getDictionaryRequestDto.getDictionaryId(), getDictionaryRequestDto.getTranslationId());
        }

        Translation translation = optionalTranslationInRepos.get();

        return translationMapper.toTranslationDto(translation);
    }

    private Optional<Translation> getTranslation(Long id, Long dictID) {
        return translationRepository.getTranslationsByIdAndDictionaryId(id, dictID);
    }
}
