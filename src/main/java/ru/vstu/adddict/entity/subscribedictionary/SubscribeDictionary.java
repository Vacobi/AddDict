package ru.vstu.adddict.entity.subscribedictionary;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder(toBuilder = true)
@Table(name="subscribe_dictionaries")
public class SubscribeDictionary extends BaseSubscribeDictionary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

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
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public Long getUserId() {
        return userId;
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
