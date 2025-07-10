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

    @Column
    private String gender;

    @Column
    private Integer age;

    @Column
    private Double weight;

    @Column
    private Double height;

    @Column
    private String activityLevel;

    @Column
    private String goal;
}
