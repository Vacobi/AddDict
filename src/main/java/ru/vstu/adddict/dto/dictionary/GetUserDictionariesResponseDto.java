package ru.vstu.adddict.dto.dictionary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vstu.adddict.dto.PageResponseDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class GetUserDictionariesResponseDto<D> {
    private Long userId;
    private PageResponseDto<D> page;
}
