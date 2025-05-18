package ru.vstu.adddict.entity.subscribedictionary;

import java.util.Objects;

public abstract class BaseSubscribeDictionary {
    public abstract void setId(Long id);
    public abstract Long getId();

    public abstract void setUserId(Long userId);
    public abstract Long getUserId();

    public abstract void setDictionaryId(Long dictionaryId);
    public abstract Long getDictionaryId();

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (this == other) return true;
        if (!(other instanceof BaseSubscribeDictionary o)) return false;

        if (!Objects.equals(getId(), o.getId())) return false;
        if (!Objects.equals(getUserId(), o.getUserId())) return false;
        return Objects.equals(getDictionaryId(), o.getDictionaryId());
    }
}
