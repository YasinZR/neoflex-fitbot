package ru.neoflex.repository;

import ru.neoflex.model.WorkoutLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Long> {
    List<WorkoutLog> findAllByUserIdOrderByTimestampDesc(Long userId);
}

