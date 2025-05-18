package ru.vstu.adddict.dto.subscribedictionary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vstu.adddict.dto.PageResponseDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class GetUserSubscribesDictionariesResponseDto<D> {
    private Long userId;
    private PageResponseDto<D> page;
}
