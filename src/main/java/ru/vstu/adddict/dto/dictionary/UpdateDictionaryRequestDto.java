package ru.vstu.adddict.dto.dictionary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UpdateDictionaryRequestDto {
    private String name;
    private String description;
    private Boolean isPublic;
    private Long requestSenderId;

    public boolean isEmpty() {
        return name == null &&
                description == null &&
                isPublic == null &&
                requestSenderId == null;
    }
}
