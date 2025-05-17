package ru.vstu.adddict.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Table(name="translations")
public class Translation extends BaseTranslation{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "origin_text", nullable = false)
    private String originText;

    @Column(name = "translation_text", nullable = false)
    private String translationText;

    @Column(name = "dictionary_id", nullable = false)
    private Long dictionaryId;

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setOriginText(String originText) {
        this.originText = originText;
    }

    @Override
    public String getOriginText() {
        return originText;
    }

    @Override
    public void setTranslationText(String translationText) {
        this.translationText = translationText;
    }

    @Override
    public String getTranslationText() {
        return translationText;
    }

    @Override
    public void setDictionaryId(Long dictionaryId) {
        this.dictionaryId = dictionaryId;
    }

    @Override
    public Long getDictionaryId() {
        return dictionaryId;
    }
}
