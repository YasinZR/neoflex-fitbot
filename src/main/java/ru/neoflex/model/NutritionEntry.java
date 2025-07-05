package ru.neoflex.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "nutrition_entries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NutritionEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String mealType;

    @Column(nullable = false)
    private Double protein;

    @Column(nullable = false)
    private Double fat;

    @Column(nullable = false)
    private Double carbs;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
