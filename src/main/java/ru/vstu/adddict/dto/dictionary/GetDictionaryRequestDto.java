package ru.vstu.adddict.dto.dictionary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class GetDictionaryRequestDto {
    private Long id;
    private Long requestSenderId;
}
