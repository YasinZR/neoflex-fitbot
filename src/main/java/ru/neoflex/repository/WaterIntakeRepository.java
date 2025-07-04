package ru.neoflex.repository;

import ru.neoflex.model.WaterIntake;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WaterIntakeRepository extends JpaRepository<WaterIntake, Long> {
    List<WaterIntake> findAllByUserIdOrderByTimestampDesc(Long userId);
}

