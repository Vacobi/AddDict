package ru.vstu.adddict.dto.translation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vstu.adddict.dto.PageResponseDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ShuffleResponseDto<D> {
    private PageResponseDto<D> page;
    private Long userId;
    private String seed;
}
