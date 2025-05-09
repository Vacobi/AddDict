package ru.vstu.adddict.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DictionaryResponseDto {
    private Long id;
    private String name;
    private String description;
    private Boolean isPublic;
    private LocalDateTime createdAt;
    private Long authorId;
}
