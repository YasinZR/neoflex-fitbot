package ru.neoflex.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.neoflex.model.User;
import ru.neoflex.model.WaterIntake;
import ru.neoflex.repository.UserRepository;
import ru.neoflex.repository.WaterIntakeRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WaterServiceImpl implements WaterService {

    private final WaterIntakeRepository waterIntakeRepository;
    private final UserRepository userRepository;
    private final OnboardingService onboardingService;

    @Override
    @Transactional
    public void addIntake(Long userId, int volumeMl) {
        User user = userRepository.findByTelegramId(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        WaterIntake entry = WaterIntake.builder()
                .user(user)
                .volumeMl(volumeMl)
                .timestamp(LocalDateTime.now())
                .build();

        waterIntakeRepository.save(entry);
    }

    @Override
    @Transactional
    public void setDailyGoal(Long userId, int goalMl) {
        User user = userRepository.findByTelegramId(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        user.setDailyWaterGoal(goalMl);
        userRepository.save(user);
    }

    @Override
    public int getDailyGoal(Long userId) {
        User user = userRepository.findByTelegramId(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return user.getDailyWaterGoal() != null ? user.getDailyWaterGoal() : 2000;
    }

    @Override
    public int getDailyTotal(Long userId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        return waterIntakeRepository.findAllByTelegramIdAndTimestampBetween(userId, start, end)
                .stream()
                .mapToInt(WaterIntake::getVolumeMl)
                .sum();
    }

    @Override
    public List<WaterIntake> getEntriesForDate(Long userId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        return waterIntakeRepository.findAllByTelegramIdAndTimestampBetween(userId, start, end);
    }

    @Override
    @Transactional
    public void incrementGoal(Long userId, int delta) {
        User user = userRepository.findByTelegramId(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Integer currentGoal = user.getDailyWaterGoal();
        if (currentGoal == null) currentGoal = 2500;

        int newGoal = Math.max(0, currentGoal + delta);
        user.setDailyWaterGoal(newGoal);
        userRepository.save(user);
    }
}
