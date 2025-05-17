package ru.vstu.adddict.dto.translation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CreateTranslationRequestDto {
    private String originText;
    private String translationText;
    private Long dictionaryId;
    private Long requestSenderId;
}
