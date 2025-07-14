package ru.neoflex.service;

import org.springframework.stereotype.Service;
import ru.neoflex.model.CalculatorProfile;

@Service
public class CalculatorServiceImpl implements CalculatorService {

    @Override
    public int calculateCalories(String gender, int age, double weight, double height, String activityLevel, String goal) {
        double bmr;

        if (gender.toLowerCase().startsWith("муж")) {
            bmr = 10 * weight + 6.25 * height - 5 * age + 5;
        } else if (gender.toLowerCase().startsWith("жен")) {
            bmr = 10 * weight + 6.25 * height - 5 * age - 161;
        } else {
            throw new IllegalArgumentException("Неверный пол: " + gender);
        }

        double activityMultiplier = switch (activityLevel.toLowerCase()) {
            case "низкий" -> 1.2;
            case "средний" -> 1.55;
            case "высокий" -> 1.9;
            case "профи" -> 2.0;
            default -> 1.0;
        };

        bmr *= activityMultiplier;

        double goalMultiplier = switch (goal.toLowerCase()) {
            case "похудеть" -> 0.8;
            case "набрать" -> 1.15;
            case "поддерживать" -> 1.0;
            default -> 1.0;
        };

        return (int) Math.round(bmr * goalMultiplier);
    }

    @Override
    public double calculateStoredCalories(CalculatorProfile profile) {
        return calculateCalories(
                profile.getGender(),
                profile.getAge(),
                profile.getWeight(),
                profile.getHeight(),
                profile.getActivityLevel(),
                profile.getGoal()
        );
    }


}
