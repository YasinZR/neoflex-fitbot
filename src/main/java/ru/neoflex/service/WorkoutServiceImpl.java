package ru.neoflex.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.neoflex.model.WorkoutLog;
import ru.neoflex.repository.WorkoutLogRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkoutServiceImpl implements WorkoutService {

    private final WorkoutLogRepository workoutLogRepository;

    @Override
    @Transactional
    public WorkoutLog addWorkout(WorkoutLog workoutLog) {
        return workoutLogRepository.save(workoutLog);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutLog> listWorkouts(Long userId) {
        return workoutLogRepository.findAllByUserIdOrderByTimestampDesc(userId);
    }

    @Override
    @Transactional
    public WorkoutLog updateWorkout(WorkoutLog workoutLog) {
        return workoutLogRepository.save(workoutLog);
    }

    @Override
    @Transactional
    public void deleteWorkout(Long workoutId) {
        workoutLogRepository.deleteById(workoutId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WorkoutLog> getWorkoutById(Long workoutId) {
        return workoutLogRepository.findById(workoutId);
    }




}
