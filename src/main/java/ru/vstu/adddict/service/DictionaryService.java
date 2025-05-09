package ru.vstu.adddict.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vstu.adddict.dto.CreateDictionaryRequestDto;
import ru.vstu.adddict.dto.DictionaryDto;
import ru.vstu.adddict.dto.GetDictionaryRequestDto;
import ru.vstu.adddict.dto.UpdateDictionaryRequestDto;
import ru.vstu.adddict.entity.Dictionary;
import ru.vstu.adddict.exception.DictionaryNonExistException;
import ru.vstu.adddict.exception.NotAllowedException;
import ru.vstu.adddict.mapper.DictionaryMapper;
import ru.vstu.adddict.repository.DictionariesRepository;
import ru.vstu.adddict.validator.DictionaryValidator;

import java.util.NoSuchElementException;
import java.util.Optional;

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

    @Transactional // For caching in future
    public DictionaryDto getDictionary(GetDictionaryRequestDto getDictionaryRequestDto) {
        Optional<Dictionary> dictionaryInRepos = dictionariesRepository.findById(getDictionaryRequestDto.getId());

        if (dictionaryInRepos.isEmpty()) {
            throw new DictionaryNonExistException(getDictionaryRequestDto.getId());
        }

        Dictionary dictionary = dictionaryInRepos.get();

        if (forbiddenToGetByThisUser(dictionary, getDictionaryRequestDto.getRequestSenderId())) {
            throw new NotAllowedException("Can't let dictionary with id: "
                    + dictionary.getId()
                    + " to this user. This dictionary is private and belongs to other user.");
        }

        return dictionaryMapper.toDto(dictionary);
    }

    public boolean forbiddenToGetByThisUser(Dictionary dictionary, Long requestSenderId) {
        return !dictionary.isPublic() && !dictionary.isDictionaryOwner(requestSenderId);
    }

    @Transactional
    public DictionaryDto updateDictionary(Long dictionaryId, UpdateDictionaryRequestDto updateDictionaryRequestDto) {

        dictionaryValidator.validateUpdateDictionaryRequest(updateDictionaryRequestDto).ifPresent(e -> {
            throw e;
        });

        DictionaryDto updatedDictionary = updateDictionaryInRepository(dictionaryId, updateDictionaryRequestDto);

        return updatedDictionary;
    }

    private DictionaryDto updateDictionaryInRepository(Long dictionaryId, UpdateDictionaryRequestDto updateDictionaryRequestDto) {

        try {
            Dictionary updated = dictionariesRepository.updateWithLock(
                    updateDictionaryRequestDto.getRequestSenderId(),
                    persisted -> {
                        persisted = dictionaryMapper.fromUpdateRequest(persisted, updateDictionaryRequestDto);
                        if (!persisted.isDictionaryOwner(dictionaryId)) {
                            throw new NotAllowedException("Can't update dictionary with id: "
                                    + persisted.getId()
                                    + ". This dictionary belongs to other user.");
                        }
                        return persisted;
                    });

            return dictionaryMapper.toDto(updated);
        } catch (NoSuchElementException e) {
            throw new DictionaryNonExistException(dictionaryId);
        }
    }
}
