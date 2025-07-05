package ru.neoflex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.neoflex.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByTelegramId(Long telegramId);
}
