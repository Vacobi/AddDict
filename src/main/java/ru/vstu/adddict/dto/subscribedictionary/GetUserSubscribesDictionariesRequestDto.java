package ru.vstu.adddict.dto.subscribedictionary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class GetUserSubscribesDictionariesRequestDto {
    private Long userId;
    private Long requestSenderId;
    private int page;
}
