package ru.neoflex.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AgeOption {
    AGE_18_25("18–25", "age_18"),
    AGE_26_35("26–35", "age_26"),
    AGE_36_45("36–45", "age_36"),
    AGE_46_PLUS("46+", "age_46");

    private final String text;
    private final String callback;
}
