package ru.vstu.adddict.dto.dictionary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class GetUserSubscribedDictionariesRequestDto {
    private Long userId;
    private int page;
}
