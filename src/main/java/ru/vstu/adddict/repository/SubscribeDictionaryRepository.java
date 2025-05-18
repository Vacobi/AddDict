package ru.vstu.adddict.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.vstu.adddict.entity.subscribedictionary.SubscribeDictionary;

import java.util.Optional;

public interface SubscribeDictionaryRepository extends JpaRepository<SubscribeDictionary, Long> {
    Optional<SubscribeDictionary> findByUserIdAndDictionaryId(Long userId, Long dictionaryId);

    Optional<SubscribeDictionary> findByDictionaryIdAndUserId(Long dictionaryId, Long userId);
}
