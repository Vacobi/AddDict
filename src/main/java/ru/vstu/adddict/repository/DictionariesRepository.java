package ru.vstu.adddict.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.vstu.adddict.entity.Dictionary;

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
}
