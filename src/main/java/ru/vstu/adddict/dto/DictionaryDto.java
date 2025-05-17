package ru.vstu.adddict.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class DictionaryDto {
    private Long id;
    private String name;
    private String description;
    private Boolean isPublic;
    private LocalDateTime createdAt;
    private Long authorId;

    public boolean isOwner(Long userId) {
        return userId.equals(authorId);
    }
}
