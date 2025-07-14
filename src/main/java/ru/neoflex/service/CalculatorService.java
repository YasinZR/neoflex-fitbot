package ru.neoflex.service;

import ru.neoflex.model.CalculatorProfile;

public interface CalculatorService {
    int calculateCalories(String gender, int age, double weight, double height, String activityLevel, String goal);
    double calculateStoredCalories(CalculatorProfile profile);

}

