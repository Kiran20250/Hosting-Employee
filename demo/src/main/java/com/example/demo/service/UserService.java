package com.example.demo.service;

import com.example.demo.entity.Task;
import com.example.demo.entity.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    // Login
    public User login(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password);
    }

    // Save or update user
    public void saveUser(User user) {
        userRepository.save(user);
    }

    // Save task
    public void saveTask(Task task) {
        taskRepository.save(task);
    }

    // Get tasks by user
    public List<Task> getTasksByUserId(Long userId) {
        return taskRepository.findByUserId(userId);
    }

    // Find task by id
    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId).orElse(null);
    }

    // Find user by id
    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    //Escalation
//    public int getTaskCount(Long userId) {
//        return taskRepository.findByUserId(userId).size();
//    }


    //  completed tasks  count
    public int getCompletedTaskCountForUser(Long userId) {
        return (int) taskRepository.findByUserId(userId).stream()
                .filter(task -> "COMPLETED".equalsIgnoreCase(task.getStatus()))
                .count();
    }


    public List<Task> getTasksForUser(Long userId) {
        return taskRepository.findByUserId(userId);
    }

    //  Calculate shift hours from login time
    private long getShiftHours(User user) {
        if (user.getInTime() == null) return 0;
        return Duration.between(user.getInTime(), LocalDateTime.now()).toHours();
    }

    //  Due Tasks
    public List<Task> getDueTasks(User user) {
        long shiftHours = getShiftHours(user);
        LocalDate today = LocalDate.now();

        return taskRepository.findByUserId(user.getId()).stream()
                .filter(task -> !"Completed".equalsIgnoreCase(task.getStatus()))
                .filter(task -> task.getDueDate() != null && task.getDueDate().toLocalDate().isEqual(today))
                .filter(task -> shiftHours >= 5 && shiftHours < 9)
                .collect(Collectors.toList());
    }

    //  Escalated Tasks
    public List<Task> getEscalatedTasks(User user) {
        long shiftHours = getShiftHours(user);
        LocalDate today = LocalDate.now();

        return taskRepository.findByUserId(user.getId()).stream()
                .filter(task -> !"Completed".equalsIgnoreCase(task.getStatus()))
                .filter(task ->
                        // deadline already past
                        (task.getDueDate() != null && task.getDueDate().toLocalDate().isBefore(today))
                                ||
                                // deadline today but shift ≥ 9 hours
                                (task.getDueDate() != null && task.getDueDate().toLocalDate().isEqual(today) && shiftHours >= 9)
                )
                .collect(Collectors.toList());
    }
}
