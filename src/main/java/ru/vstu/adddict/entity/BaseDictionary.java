package ru.vstu.adddict.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import static ru.vstu.adddict.util.TimeUtil.localDateTimeAreEquals;

public abstract class BaseDictionary {
    public abstract void setId(Long id);
    public abstract Long getId();

    public abstract void setName(String name);
    public abstract String getName();

    public abstract void setDescription(String description);
    public abstract String getDescription();

    public abstract void setPublic(boolean isPublic);
    public abstract boolean isPublic();

    public abstract void setCreatedAt(LocalDateTime createdAt);
    public abstract LocalDateTime getCreatedAt();

    public abstract void setAuthorId(Long authorId);
    public abstract Long getAuthorId();

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (this == other) return true;
        if (!(other instanceof BaseDictionary o)) return false;

        if (!Objects.equals(getId(), o.getId())) return false;
        if (!Objects.equals(getName(), o.getName())) return false;
        if (!Objects.equals(getDescription(),o.getDescription())) return false;
        if (!Objects.equals(isPublic(), o.isPublic())) return false;
        if (!localDateTimeAreEquals(getCreatedAt(), o.getCreatedAt())) return false;
        return Objects.equals(getAuthorId(), o.getAuthorId());
    }
}
