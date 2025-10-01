package com.example.demo.service;

import com.example.demo.entity.Admin;
import com.example.demo.entity.Task;
import com.example.demo.entity.User;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private AdminRepository adminRepository;


    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // -------- existing helpers --------
    public List<User> getAllEmployees() {
        List<User> employees = userRepository.findByRole("EMPLOYEE");
        for (User emp : employees) {
            emp.setTasks(taskRepository.findByUserId(emp.getId()));
        }
        return employees;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    //Total task assigned

//    public Long getTotalTasksAssigned() {
//        return taskRepository.count();
//    }

    // ----------- NEW: Total Completed Tasks for User -----------
    public int getCompletedTaskCountForUser(Long userId) {
        // JPA query already counts completed tasks (case-sensitive)
        // agar case-insensitive chahiye toh query me LOWER() use kar sakte ho
        return taskRepository.countCompletedTasksByUserId(userId);
    }




    public Long getRemainingTasks(Long userId) {
        return taskRepository.findByUserId(userId)
                .stream()
                .filter(t -> !"COMPLETED".equalsIgnoreCase(t.getStatus()))
                .count();
    }

    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void saveTask(Task task) {
        taskRepository.save(task);
    }

    public Admin findAdminByUsername(String username) {
        return adminRepository.findAll()
                .stream()
                .filter(a -> a.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    // -------- NEW: shift-based escalation logic --------

    // Helper to check pending
    private boolean isPending(Task t) {
        return t.getDueDate() != null && !"COMPLETED".equalsIgnoreCase(t.getStatus());
    }

    // ---------------- DUE TASKS ----------------
    public Map<Long, List<Task>> getDueTasksByUserId() {
        LocalDateTime now = LocalDateTime.now();

        return taskRepository.findAll().stream()
                .filter(this::isPending)
                .filter(t -> t.getUser() != null && t.getUser().getInTime() != null)
                .filter(t -> {
                    User emp = t.getUser();
                    LocalDateTime loginTime = emp.getInTime();

                    double workedHours = ChronoUnit.MINUTES.between(loginTime, now) / 60.0;

                    //  DUE condition: half shift done, full shift not done, deadline not passed
                    return workedHours >= 4.5 && workedHours < 9 && now.isBefore(t.getDueDate());
                })
                .collect(Collectors.groupingBy(t -> t.getUser().getId()));
    }

    // ---------------- ESCALATED TASKS ----------------
    public Map<Long, List<Task>> getEscalatedTasksByUserId() {
        LocalDateTime now = LocalDateTime.now();

        return taskRepository.findAll().stream()
                .filter(this::isPending)
                .filter(t -> t.getUser() != null && t.getUser().getInTime() != null)
                .filter(t -> {
                    User emp = t.getUser();
                    LocalDateTime loginTime = emp.getInTime();

                    double workedHours = ChronoUnit.MINUTES.between(loginTime, now) / 60.0;

                    //  Escalation: full shift done OR deadline passed
                    return workedHours >= 9 || !now.isBefore(t.getDueDate());
                })
                .collect(Collectors.groupingBy(t -> t.getUser().getId()));
    }

    public List<User> getEmployeesWithDueTasks() {
        Map<Long, List<Task>> map = getDueTasksByUserId();
        if (map.isEmpty()) return Collections.emptyList();
        return userRepository.findAllById(map.keySet());
    }

    public List<User> getEmployeesWithEscalatedTasks() {
        Map<Long, List<Task>> map = getEscalatedTasksByUserId();
        if (map.isEmpty()) return Collections.emptyList();
        return userRepository.findAllById(map.keySet());
    }

    // Escalation Data Logic (UPDATED)
    public Map<String, Object> getEscalationData() {
        LocalDate today = LocalDate.now();

        List<User> allEmployees = userRepository.findAll();

        List<User> dueEmployees = new ArrayList<>();
        List<User> escalatedEmployees = new ArrayList<>();
        Map<Long, List<Task>> dueTasksMap = new HashMap<>();
        Map<Long, List<Task>> escalatedTasksMap = new HashMap<>();
        Map<Long, Long> escalationCountMap = new HashMap<>();

        for (User emp : allEmployees) {
            List<Task> tasks = emp.getTasks();
            if (tasks == null || tasks.isEmpty()) continue;

            List<Task> dueTasks = new ArrayList<>();
            List<Task> escalatedTasks = new ArrayList<>();

            for (Task task : tasks) {
                if (task.getDueDate() == null) continue;
                if ("COMPLETED".equalsIgnoreCase(task.getStatus())) continue;

                LocalDate deadlineDate = task.getDueDate().toLocalDate();

                // ------------------ DUE TASKS ------------------
                if (deadlineDate.equals(today) && emp.getInTime() != null) {
                    Duration duration = Duration.between(emp.getInTime(), LocalDateTime.now());
                    long hoursWorked = duration.toHours();

                    if (hoursWorked >= 5 && hoursWorked < 9) {
                        dueTasks.add(task);
                    }
                }

                // ------------------ ESCALATION TASKS ------------------
                if (deadlineDate.isBefore(today)) {
                    escalatedTasks.add(task);
                } else if (deadlineDate.equals(today) && emp.getInTime() != null) {
                    Duration duration = Duration.between(emp.getInTime(), LocalDateTime.now());
                    long hoursWorked = duration.toHours();

                    if (hoursWorked >= 9) {
                        escalatedTasks.add(task);
                    }
                }
            }

            if (!dueTasks.isEmpty()) {
                dueEmployees.add(emp);
                dueTasksMap.put(emp.getId(), dueTasks);
            }

            if (!escalatedTasks.isEmpty()) {
                escalatedEmployees.add(emp);
                escalatedTasksMap.put(emp.getId(), escalatedTasks);
                escalationCountMap.put(emp.getId(), (long) escalatedTasks.size());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("dueEmployees", dueEmployees);
        result.put("escalatedEmployees", escalatedEmployees);
        result.put("dueTasksMap", dueTasksMap);
        result.put("escalatedTasksMap", escalatedTasksMap);
        result.put("escalationCountMap", escalationCountMap);

        return result;
    }

    //save login time
    public Admin getAdminById(Long id) {
        return adminRepository.findById(id).orElse(null);
    }

    public void saveAdmin(Admin admin) {
        adminRepository.save(admin);
    }


    // ✅ New method to mark task as completed
    public void markTaskCompleted(Long taskId) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task != null) {
            task.setStatus("Completed");
            taskRepository.save(task);
        }
    }
}
