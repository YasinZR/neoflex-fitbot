package ru.neoflex.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "calculator_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculatorProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false)
    private Double height;

    @Column(nullable = false)
    private String activityLevel;

    @Column(nullable = false)
    private String goal;
}
