package com.example.demo.controller;

import com.example.demo.entity.Admin;
import com.example.demo.entity.Task;
import com.example.demo.entity.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private TaskRepository taskRepository;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ---------------- ADMIN DASHBOARD ----------------
    @GetMapping("/admin")
    public String adminDashboard(HttpSession session, Model model) {
        String adminName = (String) session.getAttribute("adminName");
        if (adminName == null) return "redirect:/login";

        Admin admin = adminService.findAdminByUsername(adminName);
        if (admin == null) return "redirect:/login";

        LocalDateTime loginTime = admin.getInTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String inTime = (loginTime != null) ? loginTime.format(formatter) : "--:--";

        model.addAttribute("username", admin.getUsername());

        model.addAttribute("inTime", inTime);
        model.addAttribute("taskCount", adminService.getTotalTasksAssigned());
        model.addAttribute("employees", adminService.getAllEmployees());
        model.addAttribute("allTasks", adminService.getAllTasks());

        return "admin";
    }

    // ---------------- ASSIGN TASK ----------------
    @PostMapping("/admin/assign-task")
    public String assignTask(HttpServletRequest request, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin"); // ✅ stored Admin object
        if (admin == null) {
            return "redirect:/login";
        }

        String[] selectedUsers = request.getParameterValues("userIds");
        if (selectedUsers != null) {
            for (String userIdStr : selectedUsers) {
                int userId = Integer.parseInt(userIdStr);

                Task task = new Task();
                task.setTitle(request.getParameter("title"));
                task.setSummary(request.getParameter("summary"));
                task.setDescription(request.getParameter("description"));

                String deadlineStr = request.getParameter("dueDateTime");
                if (deadlineStr != null && !deadlineStr.isEmpty()) {
                    LocalDate deadlineDate = LocalDate.parse(deadlineStr);
                    LocalDateTime deadline = deadlineDate.atTime(23, 59);
                    task.setDueDate(deadline);
                }

                String priorityStr = request.getParameter("priority");
                if (priorityStr != null && !priorityStr.isEmpty()) {
                    try {
                        task.setPriority(Integer.parseInt(priorityStr));
                    } catch (NumberFormatException e) {
                        task.setPriority(3);
                    }
                } else {
                    task.setPriority(3);
                }

                User user = adminService.findUserById((long) userId);
                if (user != null) {
                    task.setUser(user);
                }
                task.setAssignedBy(admin);
                task.setStatus("Not Started");

                taskRepository.save(task);
            }
        }

        return "redirect:/admin";
    }

    // ---------------- ESCALATION VIEW ----------------
    //  Escalation Dashboard
// ---------------- ESCALATION VIEW ----------------
    // ---------------- ESCALATION VIEW ----------------
    @GetMapping("/admin/escalation")
    public String escalationDashboard(HttpSession session, Model model) {
        String adminName = (String) session.getAttribute("adminName");
        if (adminName == null) return "redirect:/login";

        Admin admin = adminService.findAdminByUsername(adminName);
        if (admin == null) return "redirect:/login";

        // ✅ Format login time
        LocalDateTime loginTime = admin.getInTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String inTime = (loginTime != null) ? loginTime.format(formatter) : "--:--";

        // ✅ Get total tasks assigned
        Long taskCount = adminService.getTotalTasksAssigned();


        // ✅ Add to model
        model.addAttribute("username", admin.getUsername());
        model.addAttribute("inTime", inTime);
        model.addAttribute("taskCount", taskCount);

        // ✅ Escalation data
        Map<String, Object> escalationData = adminService.getEscalationData();
        model.addAttribute("dueEmployees", escalationData.get("dueEmployees"));
        model.addAttribute("escalatedEmployees", escalationData.get("escalatedEmployees"));
        model.addAttribute("dueTasksMap", escalationData.get("dueTasksMap"));
        model.addAttribute("escalatedTasksMap", escalationData.get("escalatedTasksMap"));
        model.addAttribute("escalationCountMap", escalationData.get("escalationCountMap"));

        return "escalation";
    }






    // ---------------- UPDATE TASK ----------------
    @PostMapping("/admin/update-task")
    public String updateTask(HttpServletRequest request) {
        Long taskId = Long.parseLong(request.getParameter("taskId"));
        Task task = taskRepository.findById(taskId).orElse(null);

        if (task != null) {
            task.setTitle(request.getParameter("title"));
            task.setSummary(request.getParameter("summary"));
            task.setDescription(request.getParameter("description"));

            String dueDateStr = request.getParameter("dueDate");
            if (dueDateStr != null && !dueDateStr.isEmpty()) {
                try {
                    LocalDate deadlineDate = LocalDate.parse(dueDateStr);
                    task.setDueDate(deadlineDate.atTime(23, 59));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            String priorityStr = request.getParameter("priority");
            if (priorityStr != null && !priorityStr.isEmpty()) {
                try {
                    task.setPriority(Integer.parseInt(priorityStr));
                } catch (NumberFormatException e) {
                    task.setPriority(3);
                }
            }

            taskRepository.save(task);
        }

        //  Always return to admin dashboard
        return "redirect:/admin";
    }


    // ---------------- DELETE TASK ----------------
    @PostMapping("/admin/delete-task")
    public String deleteTask(@RequestParam Long taskId) {
        taskRepository.deleteById(taskId);
        return "redirect:/admin";
    }



}
