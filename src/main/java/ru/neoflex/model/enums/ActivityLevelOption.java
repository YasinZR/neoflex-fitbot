package ru.neoflex.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ActivityLevelOption {
    LOW("Низкий", "activity_low"),
    MEDIUM("Средний", "activity_medium"),
    HIGH("Высокий", "activity_high"),
    PRO("🏃 Профи", "activity_pro");

    private final String text;
    private final String callback;
}
