package ru.neoflex.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "water_intake")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterIntake {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer volumeMl;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
