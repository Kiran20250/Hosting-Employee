package com.example.demo.controller;

import com.example.demo.entity.Admin;
import com.example.demo.entity.Task;
import com.example.demo.entity.User;
import com.example.demo.repository.AdminRepository;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AdminRepository adminRepository;

    @GetMapping("/")
    public String homeRedirect() {
        return "redirect:/login";
    }

    // ----- LOGIN -----
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute("user") User user, Model model, HttpSession session) {

        Admin admin = adminRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword());
        if (admin != null) {
            session.setAttribute("admin", admin);
            session.setAttribute("adminName", admin.getUsername());

            LocalDate today = LocalDate.now();
            LocalDateTime loginTime = admin.getInTime();
            if (loginTime == null || !loginTime.toLocalDate().equals(today)) {
                admin.setInTime(LocalDateTime.now());
                adminRepository.save(admin);
            }
            return "redirect:/admin";
        }

        User validUser = userService.login(user.getUsername(), user.getPassword());
        if (validUser != null) {
            LocalDate today = LocalDate.now();
            if (validUser.getInTime() == null || !validUser.getInTime().toLocalDate().equals(today)) {
                validUser.setInTime(LocalDateTime.now());
                userService.saveUser(validUser);
            }

            List<Task> tasks = userService.getTasksByUserId(validUser.getId());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            session.setAttribute("userId", validUser.getId());
            model.addAttribute("name", validUser.getUsername());
            model.addAttribute("tasks", tasks);
            model.addAttribute("inTime", validUser.getInTime().format(formatter));
            model.addAttribute("taskCount", tasks.size());

            return "task";
        } else {
            model.addAttribute("error", "Invalid Username or Password!");
            return "login";
        }
    }

    // ----- LOGOUT -----
    @GetMapping("/logout")
    public String logoutUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            User user = userService.findUserById(userId);
            if (user != null) {
                user.setOutTime(LocalDateTime.now());
                userService.saveUser(user);
            }
        }
        session.invalidate();
        return "redirect:/login";
    }

    // ----- REGISTER -----
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult result,
                               Model model) throws IOException {

        if (result.hasErrors()) {
            model.addAttribute("error", result.getFieldError("password").getDefaultMessage());
            return "register";
        }
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            model.addAttribute("error", "Passwords do not match!");
            return "register";
        }

        // Use Render persistent storage
        String uploadsDir = "/mnt/data/uploads/";
        File dir = new File(uploadsDir);
        if (!dir.exists()) dir.mkdirs();

        // Marksheets
        MultipartFile marksheetFile = user.getMarksheetFile();
        if (marksheetFile != null && !marksheetFile.isEmpty()) {
            if (marksheetFile.getSize() > 1024 * 1024) {
                model.addAttribute("error", "Marksheet file size must be <= 1MB");
                return "register";
            }
            String filename = System.currentTimeMillis() + "_marksheet_" + marksheetFile.getOriginalFilename();
            File file = new File(dir, filename);
            marksheetFile.transferTo(file);
            user.setAcademicMarksheet("/uploads/" + filename); // store relative path
        }

        // Aadhar
        MultipartFile aadharFile = user.getAadharFile();
        if (aadharFile != null && !aadharFile.isEmpty()) {
            if (aadharFile.getSize() > 1024 * 1024) {
                model.addAttribute("error", "Aadhar file size must be <= 1MB");
                return "register";
            }
            String filename = System.currentTimeMillis() + "_aadhar_" + aadharFile.getOriginalFilename();
            File file = new File(dir, filename);
            aadharFile.transferTo(file);
            user.setAadharCard("/uploads/" + filename);
        }

        // PAN
        MultipartFile panFile = user.getPanFile();
        if (panFile != null && !panFile.isEmpty()) {
            if (panFile.getSize() > 1024 * 1024) {
                model.addAttribute("error", "PAN file size must be <= 1MB");
                return "register";
            }
            String filename = System.currentTimeMillis() + "_pan_" + panFile.getOriginalFilename();
            File file = new File(dir, filename);
            panFile.transferTo(file);
            user.setPanCard("/uploads/" + filename);
        }

        userService.saveUser(user);
        return "redirect:/login";
    }

    // ----- VIEW PDF -----
    @GetMapping("/view-pdf/{filename}")
    public void viewPDF(@PathVariable String filename, HttpServletResponse response) {
        String uploadsDir = "/mnt/data/uploads/";
        File file = new File(uploadsDir + filename);

        if (!file.exists()) {
            try {
                response.setContentType("text/html");
                response.getWriter().write("<h3>File not found: " + filename + "</h3>");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");

        try (FileInputStream fis = new FileInputStream(file);
             OutputStream os = response.getOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ----- DEBUG PDF LIST -----
    @GetMapping("/debug-pdfs")
    public String debugPDFs(Model model) {
        String uploadsDir = "/mnt/data/uploads/";
        File dir = new File(uploadsDir);
        String[] pdfFiles = dir.list((d, name) -> name.toLowerCase().endsWith(".pdf"));

        model.addAttribute("pdfFiles", pdfFiles != null ? pdfFiles : new String[0]);
        return "viewPdf";
    }

    // ----- TASK PAGE -----
    @GetMapping("/tasks")
    public String showTasks(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User user = userService.findUserById(userId);
        List<Task> tasks = userService.getTasksByUserId(userId);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String inTime = user.getInTime() != null ? user.getInTime().format(timeFormatter) : "";

        model.addAttribute("inTime", inTime);
        model.addAttribute("tasks", tasks);
        model.addAttribute("name", user.getUsername());
        model.addAttribute("taskCount", tasks.size());

        return "task";
    }

    // ----- ACCEPT TASK -----
    @PostMapping("/tasks/accept")
    public String acceptTask(@RequestParam Long taskId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        Task task = userService.getTaskById(taskId);
        if (task != null && "Not Started".equals(task.getStatus())) {
            task.setStatus("In Progress");
            userService.saveTask(task);
        }
        return "redirect:/tasks";
    }

    // ----- EMPLOYEE ESCALATION -----
    @GetMapping("/emp/escalation")
    public String viewEscalations(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User user = userService.findUserById(userId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String inTime = user.getInTime() != null ? user.getInTime().format(formatter) : "";

        model.addAttribute("name", user.getUsername());
        model.addAttribute("inTime", inTime);
        model.addAttribute("taskCount", userService.getTaskCount(user.getId()));

        List<Task> dueTasks = userService.getDueTasks(user);
        List<Task> escalatedTasks = userService.getEscalatedTasks(user);

        model.addAttribute("dueTasks", dueTasks);
        model.addAttribute("escalatedTasks", escalatedTasks);

        return "emp-escalation";
    }
}
