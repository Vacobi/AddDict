package ru.vstu.adddict.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TranslationDto {
    private Long id;
    private String originText;
    private String translationText;
    private Long dictionaryId;
}
