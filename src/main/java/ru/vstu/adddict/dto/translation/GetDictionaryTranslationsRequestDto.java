package ru.vstu.adddict.dto.translation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class GetDictionaryTranslationsRequestDto {
    private Long dictionaryId;
    private Long userId;
    private int page;
}
