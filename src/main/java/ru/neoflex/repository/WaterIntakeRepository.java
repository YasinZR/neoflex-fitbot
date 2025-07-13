package ru.neoflex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.neoflex.model.WaterIntake;

import java.time.LocalDateTime;
import java.util.List;

public interface WaterIntakeRepository extends JpaRepository<WaterIntake, Long> {

    List<WaterIntake> findAllByUserIdAndTimestampBetween(Long userId, LocalDateTime start, LocalDateTime end);

    @Query("""
        SELECT w FROM WaterIntake w 
        WHERE w.user.telegramId = :telegramId 
        AND w.timestamp BETWEEN :start AND :end
    """)
    List<WaterIntake> findAllByTelegramIdAndTimestampBetween(
            @Param("telegramId") Long telegramId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
