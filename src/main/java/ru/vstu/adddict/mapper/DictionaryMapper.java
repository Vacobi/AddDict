package ru.vstu.adddict.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.vstu.adddict.dto.CreateDictionaryRequestDto;
import ru.vstu.adddict.entity.Dictionary;

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
}
