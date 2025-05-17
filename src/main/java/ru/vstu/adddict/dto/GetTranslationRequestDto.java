package ru.vstu.adddict.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class GetTranslationRequestDto {
    private Long translationId;
    private Long dictionaryId;
}
