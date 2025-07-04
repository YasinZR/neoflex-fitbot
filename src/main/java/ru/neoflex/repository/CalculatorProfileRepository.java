package ru.neoflex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.neoflex.model.CalculatorProfile;
import java.util.Optional;

public interface CalculatorProfileRepository extends JpaRepository<CalculatorProfile, Long> {
    Optional<CalculatorProfile> findByUserId(Long userId);
}

