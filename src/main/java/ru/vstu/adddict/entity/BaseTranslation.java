package ru.vstu.adddict.entity;

import java.util.Objects;

public abstract class BaseTranslation {
    public abstract void setId(Long id);
    public abstract Long getId();

    public abstract void setOriginText(String originText);
    public abstract String getOriginText();

    public abstract void setTranslationText(String translationText);
    public abstract String getTranslationText();

    public abstract void setDictionaryId(Long dictionaryId);
    public abstract Long getDictionaryId();

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (this == other) return true;
        if (!(other instanceof BaseTranslation o)) return false;

        if (!Objects.equals(getId(), o.getId())) return false;
        if (!Objects.equals(getOriginText(), o.getOriginText())) return false;
        if (!Objects.equals(getTranslationText(),o.getTranslationText())) return false;
        return Objects.equals(getDictionaryId(), o.getDictionaryId());
    }
}
