package ru.vstu.adddict.dto.dictionary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vstu.adddict.dto.PageResponseDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class GetDictionaryTranslationsResponseDto<D> {
    private Long dictionaryId;
    private PageResponseDto<D> page;
}