package ru.vstu.adddict.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.vstu.adddict.entity.translation.Translation;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.UnaryOperator;

public interface TranslationRepository extends JpaRepository<Translation, Long> {
    Optional<Translation> getTranslationsByIdAndDictionaryId(Long id, Long dictID);

    Page<Translation> findTranslationsByDictionaryId(Long dictionaryId, Pageable pageable);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Lock(LockModeType.PESSIMISTIC_READ)
    default Translation updateWithLock(Long id, UnaryOperator<Translation> modifier) {
        return update(id, true, modifier);
    }

    private Translation update(Long id, boolean updateIfUnmodified, UnaryOperator<Translation> modifier) {

        Translation dictionary = findById(id).orElseThrow(NoSuchElementException::new);

        Translation updated = modifier.apply(dictionary);

        if (updateIfUnmodified || !dictionary.equals(updated)) {
            return save(updated);
        }

        return updated;
    }

    List<Translation> getTranslationsById(Long id);

    @Query(
            value = """
            SELECT *
            FROM translations
            WHERE dictionary_id IN (:dictionaryIds)
            ORDER BY md5(CAST(id AS text) || :seed)
            """,
                    countQuery = """
            SELECT count(*)
            FROM translations
            WHERE dictionary_id IN (:dictionaryIds)
            """,
            nativeQuery = true
    )
    Page<Translation> findShuffledTranslations(
            @Param("dictionaryIds") List<Long> dictionaryIds,
            @Param("seed") String seed,
            Pageable pageable
    );
}
