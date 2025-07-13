package ru.neoflex.service;

import ru.neoflex.model.WaterIntake;

import java.time.LocalDate;
import java.util.List;

public interface WaterService {
    void addIntake(Long userId, int volumeMl);
    void setDailyGoal(Long userId, int goalMl);
    int getDailyGoal(Long userId);
    int getDailyTotal(Long userId, LocalDate date);
    List<WaterIntake> getEntriesForDate(Long userId, LocalDate date);
    void incrementGoal(Long userId, int delta);

}
