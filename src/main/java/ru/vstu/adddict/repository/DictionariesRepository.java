package ru.vstu.adddict.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vstu.adddict.entity.Dictionary;

public interface DictionariesRepository extends JpaRepository<Dictionary, Long> {
}
