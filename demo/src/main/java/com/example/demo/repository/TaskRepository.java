package com.example.demo.repository;

import com.example.demo.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);
    Long countByUserId(Long userId);
    Long countByStatus(String status);

    // Tasks due but not completed
    @Query("SELECT t FROM Task t WHERE t.dueDate <= :now AND t.status <> 'Completed'")
    List<Task> findDueTasks(LocalDateTime now);

    // Tasks escalated (past deadline logic)
    @Query("SELECT t FROM Task t WHERE t.dueDate <= :escalationTime AND t.status <> 'Completed'")
    List<Task> findEscalatedTasks(LocalDateTime escalationTime);


    // ----------------- Employee ke liye Completed Tasks Count -----------------
    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId AND UPPER(t.status) = 'COMPLETED'")
    int countCompletedTasksByUserId(@Param("userId") Long userId);

}
