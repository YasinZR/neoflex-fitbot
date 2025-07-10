package ru.neoflex.service;

import ru.neoflex.model.WorkoutLog;

import java.util.List;

public interface WorkoutService {
    WorkoutLog addWorkout(WorkoutLog workoutLog);
    List<WorkoutLog> listWorkouts(Long userId);
    WorkoutLog updateWorkout(WorkoutLog workoutLog);
    void deleteWorkout(Long workoutId);
    WorkoutLog getWorkoutById(Long workoutId);


}
