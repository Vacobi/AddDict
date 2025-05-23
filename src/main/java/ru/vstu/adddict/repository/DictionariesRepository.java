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
import ru.vstu.adddict.entity.dictionary.Dictionary;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.UnaryOperator;

public interface DictionariesRepository extends JpaRepository<Dictionary, Long> {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Lock(LockModeType.PESSIMISTIC_READ)
    default Dictionary updateWithLock(Long id, UnaryOperator<Dictionary> modifier) {
        return update(id, true, modifier);
    }

    private Dictionary update(Long id, boolean updateIfUnmodified, UnaryOperator<Dictionary> modifier) {

        Dictionary dictionary = findById(id).orElseThrow(NoSuchElementException::new);

        Dictionary updated = modifier.apply(dictionary);

        if (updateIfUnmodified || !dictionary.equals(updated)) {
            return save(updated);
        }

        return updated;
    }

    List<Dictionary> getDictionaryById(Long id);

    Page<Dictionary> getDictionariesByAuthorId(Long authorId, Pageable pageable);

    Page<Dictionary> getDictionariesByAuthorIdAndIsPublic(Long authorId, boolean b, Pageable pageable);

    @Query("""
            SELECT d
            FROM Dictionary d
            JOIN SubscribeDictionary sd ON sd.dictionaryId = d.id
            WHERE sd.userId = :userIdValue
            """)
    Page<Dictionary> findSubscribedDictionaries(@Param("userIdValue") Long userId, Pageable pageable);

    @Query(
            value = """
                    SELECT d.* 
                    FROM dictionaries d 
                    INNER JOIN subscribe s ON d.author_id = s.author 
                    WHERE s.subscriber = :userId 
                    ORDER BY d.created_at DESC
                    """,
            countQuery = """
                    SELECT COUNT(d.*) 
                    FROM dictionaries d 
                    INNER JOIN subscribe s ON d.author_id = s.author 
                    WHERE s.subscriber = :userId
                    """,
            nativeQuery = true
    )
    Page<Dictionary> findPublishersDictionaries(
            @Param("userId") Long userId,
            Pageable pageable
    );
}
