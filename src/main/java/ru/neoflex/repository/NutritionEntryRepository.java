package ru.neoflex.repository;

import ru.neoflex.model.NutritionEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NutritionEntryRepository extends JpaRepository<NutritionEntry, Long> {
    List<NutritionEntry> findAllByUserIdOrderByTimestampDesc(Long userId);
}

