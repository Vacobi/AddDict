package ru.vstu.adddict.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.vstu.adddict.dto.dictionary.CreateDictionaryRequestDto;
import ru.vstu.adddict.dto.dictionary.DictionaryDto;
import ru.vstu.adddict.dto.dictionary.DictionaryResponseDto;
import ru.vstu.adddict.dto.dictionary.UpdateDictionaryRequestDto;
import ru.vstu.adddict.entity.dictionary.BaseDictionary;
import ru.vstu.adddict.entity.dictionary.Dictionary;

import java.time.LocalDateTime;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.FIELD,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        imports = {LocalDateTime.class}
)
public interface DictionaryMapper {

    default Dictionary toDictionary(CreateDictionaryRequestDto createDictionaryRequestDto) {
        LocalDateTime now = LocalDateTime.now();

        Dictionary dictionary = Dictionary.builder()
                .id(null)
                .name(createDictionaryRequestDto.getName())
                .description(createDictionaryRequestDto.getDescription())
                .isPublic(createDictionaryRequestDto.getIsPublic())
                .createdAt(now)
                .authorId(createDictionaryRequestDto.getAuthorId())
                .build();

        return dictionary;
    }

    @Mapping(source = "public", target = "isPublic")
    DictionaryDto toDto(Dictionary dictionary);

    default <T extends BaseDictionary> T fromUpdateRequest(T persisted, UpdateDictionaryRequestDto updateRequest) {
        if (updateRequest.getName() != null) {
            persisted.setName(updateRequest.getName());
        }

        if (updateRequest.getDescription() != null) {
            persisted.setDescription(updateRequest.getDescription());
        }

        if (updateRequest.getIsPublic() != null) {
            persisted.setPublic(updateRequest.getIsPublic());
        }

        return persisted;
    }

    DictionaryResponseDto toDictionaryResponseDto(DictionaryDto dictionary);
}
