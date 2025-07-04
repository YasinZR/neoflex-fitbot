package ru.neoflex.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "workout_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Integer durationMin;

    @Column(nullable = false)
    private Integer calories;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
