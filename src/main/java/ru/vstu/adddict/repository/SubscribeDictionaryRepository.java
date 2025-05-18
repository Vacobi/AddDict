package ru.vstu.adddict.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.vstu.adddict.entity.subscribedictionary.SubscribeDictionary;

public interface SubscribeDictionaryRepository extends JpaRepository<SubscribeDictionary, Long> {
}
