package ru.neoflex.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.model.CalculatorProfile;
import ru.neoflex.model.OnboardingStep;
import ru.neoflex.model.User;
import ru.neoflex.repository.CalculatorProfileRepository;
import ru.neoflex.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class OnboardingServiceImpl implements OnboardingService {

    private final UserRepository userRepository;
    private final CalculatorProfileRepository calculatorProfileRepository;

    @Override
    public void startOnboarding(Long telegramId) {
        User user = userRepository.findByTelegramId(telegramId)
                .orElseGet(() -> User.builder()
                        .telegramId(telegramId)
                        .profileComplete(false)
                        .onboardingStep(OnboardingStep.GENDER)
                        .build());

        user.setOnboardingStep(OnboardingStep.GENDER);
        user.setProfileComplete(false);

        // Создаём пустой профиль, если его ещё нет
        if (user.getCalculatorProfile() == null) {
            CalculatorProfile profile = new CalculatorProfile();
            profile.setUser(user);
            user.setCalculatorProfile(profile);
        }

        userRepository.save(user);
    }

    @Override
    public void saveOnboardingStep(Long telegramId, OnboardingStep step) {
        User user = userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setOnboardingStep(step);
        userRepository.save(user);
    }

    @Override
    public void saveGender(Long telegramId, String gender) {
        userRepository.findByTelegramId(telegramId).ifPresent(user -> {
            CalculatorProfile profile = getOrCreateProfile(user);
            profile.setGender(gender);
            calculatorProfileRepository.save(profile);
        });
    }

    @Override
    public void saveAgeRange(Long telegramId, String ageRange) {
        userRepository.findByTelegramId(telegramId).ifPresent(user -> {
            CalculatorProfile profile = getOrCreateProfile(user);
            profile.setAge(Integer.parseInt(ageRange));
            calculatorProfileRepository.save(profile);
        });
    }

    @Override
    public void saveWeight(Long telegramId, String weight) {
        userRepository.findByTelegramId(telegramId).ifPresent(user -> {
            CalculatorProfile profile = getOrCreateProfile(user);
            profile.setWeight(Double.parseDouble(weight));
            calculatorProfileRepository.save(profile);
        });
    }

    @Override
    public void saveHeight(Long telegramId, String height) {
        userRepository.findByTelegramId(telegramId).ifPresent(user -> {
            CalculatorProfile profile = getOrCreateProfile(user);
            profile.setHeight(Double.parseDouble(height));
            calculatorProfileRepository.save(profile);
        });
    }



    @Override
    public void saveActivityLevel(Long telegramId, String level) {
        userRepository.findByTelegramId(telegramId).ifPresent(user -> {
            CalculatorProfile profile = getOrCreateProfile(user);
            profile.setActivityLevel(level);
            calculatorProfileRepository.save(profile);
        });
    }

    @Override
    public void saveGoal(Long telegramId, String goal) {
        userRepository.findByTelegramId(telegramId).ifPresent(user -> {
            CalculatorProfile profile = getOrCreateProfile(user);
            profile.setGoal(goal);
            calculatorProfileRepository.save(profile);

            user.setProfileComplete(true); // Завершение онбординга
            userRepository.save(user);
        });
    }

    // Приватный метод для безопасного получения или создания CalculatorProfile
    private CalculatorProfile getOrCreateProfile(User user) {
        CalculatorProfile profile = user.getCalculatorProfile();
        if (profile == null) {
            profile = new CalculatorProfile();
            profile.setUser(user);
            user.setCalculatorProfile(profile);
        }
        return profile;
    }

    @Override
    public User getUserByTelegramId(Long telegramId) {
        return userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


}
