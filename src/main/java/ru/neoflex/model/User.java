package ru.neoflex.model;

import jakarta.persistence.*;
import lombok.*;
import ru.neoflex.model.enums.OnboardingStep;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long telegramId;

    @Column(nullable = false)
    private Boolean profileComplete;

    @Column(nullable = false)
    private OnboardingStep onboardingStep;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private CalculatorProfile calculatorProfile;
}
