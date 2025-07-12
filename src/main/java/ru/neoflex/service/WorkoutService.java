package ru.neoflex.service;

import ru.neoflex.model.WorkoutLog;

import java.util.List;
import java.util.Optional;

public interface WorkoutService {
    WorkoutLog addWorkout(WorkoutLog workoutLog);
    List<WorkoutLog> listWorkouts(Long userId);
    WorkoutLog updateWorkout(WorkoutLog workoutLog);
    void deleteWorkout(Long workoutId);
    Optional<WorkoutLog> getWorkoutById(Long workoutId);



}
