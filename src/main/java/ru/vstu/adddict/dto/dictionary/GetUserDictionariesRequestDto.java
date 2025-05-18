package ru.vstu.adddict.dto.dictionary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class GetUserDictionariesRequestDto {
    private Long userId;
    private Long requestSenderId;
    private int page;
}
