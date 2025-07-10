package ru.neoflex.service;

import ru.neoflex.model.enums.OnboardingStep;
import ru.neoflex.model.User;

public interface OnboardingService {
    void startOnboarding(Long telegramId);
    void saveOnboardingStep(Long telegramId, OnboardingStep step);
    void saveGender(Long telegramId, String gender);
    void saveAgeRange(Long telegramId, String ageRange);
    void saveWeight(Long telegramId, String weight);
    void saveHeight(Long telegramId, String height);
    void saveActivityLevel(Long telegramId, String level);
    void saveGoal(Long telegramId, String goal);
    User getUserByTelegramId(Long telegramId);

}
