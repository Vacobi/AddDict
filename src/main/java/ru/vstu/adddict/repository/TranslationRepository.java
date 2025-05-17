package ru.vstu.adddict.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vstu.adddict.entity.Translation;

import java.util.Optional;

public interface TranslationRepository extends JpaRepository<Translation, Long> {
    Optional<Translation> getTranslationsByIdAndDictionaryId(Long id, Long dictID);
}
