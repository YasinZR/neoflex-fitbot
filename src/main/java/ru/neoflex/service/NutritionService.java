package ru.neoflex.service;

import ru.neoflex.model.NutritionEntry;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface NutritionService {
    NutritionEntry addMeal(NutritionEntry entry);
    List<NutritionEntry> getMealsByDate(Long userId, LocalDate date);
    List<NutritionEntry> getMealsForWeek(Long userId, LocalDate weekStart);
    NutritionEntry updateMeal(NutritionEntry entry);
    void deleteMeal(Long id);
    Optional<NutritionEntry> getMealById(Long id);
}
