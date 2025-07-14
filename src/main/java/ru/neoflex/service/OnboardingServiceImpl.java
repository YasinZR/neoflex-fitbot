package ru.neoflex.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.neoflex.model.CalculatorProfile;
import ru.neoflex.model.enums.OnboardingStep;
import ru.neoflex.model.User;
import ru.neoflex.repository.CalculatorProfileRepository;
import ru.neoflex.repository.UserRepository;

import java.util.Map;

import static ru.neoflex.util.BotResponseUtils.sendTextWithButtons;

@Service
@RequiredArgsConstructor
public class OnboardingServiceImpl implements OnboardingService {

    private final UserRepository userRepository;
    private final CalculatorProfileRepository calculatorProfileRepository;
    private final CalculatorService calculatorService;


    private void handleStep(Long telegramId, OnboardingStep nextStep, java.util.function.Consumer<CalculatorProfile> updater) {
        User user = userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CalculatorProfile profile = getOrCreateProfile(user);
        updater.accept(profile);

        calculatorProfileRepository.save(profile);

        user.setOnboardingStep(nextStep);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void startOnboarding(Long telegramId) {
        User user = userRepository.findByTelegramId(telegramId).orElse(null);

        if (user == null) {
            user = User.builder()
                    .telegramId(telegramId)
                    .profileComplete(false)
                    .onboardingStep(OnboardingStep.GENDER)
                    .build();
        }

        user.setOnboardingStep(OnboardingStep.GENDER);
        user.setProfileComplete(false);

        if (user.getCalculatorProfile() == null) {
            CalculatorProfile profile = new CalculatorProfile();
            profile.setUser(user);
            user.setCalculatorProfile(profile);
        }

        userRepository.save(user);
    }


    @Transactional
    @Override
    public void saveOnboardingStep(Long telegramId, OnboardingStep step) {
        User user = userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setOnboardingStep(step);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void saveGender(Long telegramId, String gender) {
        handleStep(telegramId, OnboardingStep.AGE, profile -> profile.setGender(gender));
    }

    @Transactional
    @Override
    public void saveAgeRange(Long telegramId, String ageRange) {
        handleStep(telegramId, OnboardingStep.WEIGHT, profile -> profile.setAge(Integer.parseInt(ageRange)));
    }

    @Transactional
    @Override
    public void saveWeight(Long telegramId, String weight) {
        handleStep(telegramId, OnboardingStep.HEIGHT, profile -> profile.setWeight(Double.parseDouble(weight)));
    }

    @Transactional
    @Override
    public void saveHeight(Long telegramId, String height) {
        handleStep(telegramId, OnboardingStep.ACTIVITY_LEVEL, profile -> profile.setHeight(Double.parseDouble(height)));
    }

    @Transactional
    @Override
    public void saveActivityLevel(Long telegramId, String level) {
        handleStep(telegramId, OnboardingStep.GOAL, profile -> profile.setActivityLevel(level));
    }

    @Transactional
    @Override
    public void saveGoal(Long telegramId, String goal) {
        userRepository.findByTelegramId(telegramId).ifPresent(user -> {
            CalculatorProfile profile = getOrCreateProfile(user);
            profile.setGoal(goal);
            calculatorProfileRepository.save(profile);

            user.setProfileComplete(true);
            user.setOnboardingStep(OnboardingStep.COMPLETE);
            userRepository.save(user);
        });
    }


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

    @Override
    @Transactional
    public void completeOnboarding(Long telegramId) {
        User user = userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + telegramId));

        user.setProfileComplete(true);
        user.setOnboardingStep(OnboardingStep.COMPLETE);
        userRepository.save(user);
    }

    public void calculateAndShowCalories(Long chatId) {
        User user = userRepository.findByTelegramId(chatId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        CalculatorProfile profile = calculatorProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Профиль не найден"));

        double calories = calculatorService.calculateCalories(
                profile.getGender(),
                profile.getAge(),
                profile.getWeight(),
                profile.getHeight(),
                profile.getActivityLevel(),
                profile.getGoal()
        );

        Map<String, String> buttons = Map.of(
                "✅ Сохранить в профиль", "SAVE_CALC_PROFILE",
                "❌ Отменить", "CANCEL_CALC_PROFILE"
        );

        sendTextWithButtons(
                chatId,
                "🔥 Твоя суточная потребность: *" + (int) calories + "* ккал",
                buttons
        );
    }


}
