package ru.vstu.adddict.dto.translation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UpdateTranslationRequestDto {
    private String originText;
    private String translationText;
    private Long requestSenderId;

    public boolean isEmpty() {
        return originText == null
                && translationText == null;
    }
}
