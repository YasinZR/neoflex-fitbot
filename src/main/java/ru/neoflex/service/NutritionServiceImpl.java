package ru.neoflex.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.neoflex.model.NutritionEntry;
import ru.neoflex.repository.NutritionEntryRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NutritionServiceImpl implements NutritionService {

    private final NutritionEntryRepository nutritionEntryRepository;

    @Override
    @Transactional
    public NutritionEntry addMeal(NutritionEntry entry) {
        return nutritionEntryRepository.save(entry);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NutritionEntry> getMealsByDate(Long userId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        return nutritionEntryRepository.findAllByUserIdAndTimestampBetweenOrderByTimestampDesc(userId, start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NutritionEntry> getMealsForWeek(Long userId, LocalDate weekStart) {
        LocalDateTime start = weekStart.atStartOfDay();
        LocalDateTime end = weekStart.plusDays(6).atTime(LocalTime.MAX);
        return nutritionEntryRepository.findAllByUserIdAndTimestampBetweenOrderByTimestampDesc(userId, start, end);
    }

    @Override
    @Transactional
    public NutritionEntry updateMeal(NutritionEntry entry) {
        return nutritionEntryRepository.save(entry);
    }

    @Override
    @Transactional
    public void deleteMeal(Long id) {
        nutritionEntryRepository.deleteById(id);
    }

    @Override
    public Optional<NutritionEntry> getMealById(Long id) {
        return nutritionEntryRepository.findById(id);
    }
}
