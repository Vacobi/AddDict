package ru.vstu.adddict.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class GetDictionaryTranslationsResponseDto<D> {
    private Long dictionaryId;
    private PageResponseDto<D> page;
}