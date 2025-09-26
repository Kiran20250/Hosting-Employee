package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "registration")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;   // NEW
    private String lastName;    // NEW
    private String username;

    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$",
            message = "Password must be at least 8 characters, contain uppercase, lowercase & number"
    )
    private String password;

    @Transient
    private String confirmPassword;

    private String phone;
    private String role = "EMPLOYEE";

    private LocalDateTime inTime;
    private LocalDateTime outTime;

    private String academicMarksheet; // stores uploaded file path

    @Transient
    private MultipartFile marksheetFile; // file upload

    private String aadharCard;        // aadhar file path
    private String panCard;           // pan file path


    @Transient
    private MultipartFile aadharFile;
    @Transient
    private MultipartFile panFile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Task> tasks = new ArrayList<>();

    // ===== Getters & Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getInTime() { return inTime; }
    public void setInTime(LocalDateTime inTime) { this.inTime = inTime; }

    public LocalDateTime getOutTime() { return outTime; }
    public void setOutTime(LocalDateTime outTime) { this.outTime = outTime; }

    public String getAcademicMarksheet() { return academicMarksheet; }
    public void setAcademicMarksheet(String academicMarksheet) { this.academicMarksheet = academicMarksheet; }

    public MultipartFile getMarksheetFile() { return marksheetFile; }
    public void setMarksheetFile(MultipartFile marksheetFile) { this.marksheetFile = marksheetFile; }

    public List<Task> getTasks() { return tasks; }
    public void setTasks(List<Task> tasks) { this.tasks = tasks; }

    public String getAadharCard() { return aadharCard; }
    public void setAadharCard(String aadharCard) { this.aadharCard = aadharCard; }

    public String getPanCard() { return panCard; }
    public void setPanCard(String panCard) { this.panCard = panCard; }

    public MultipartFile getAadharFile() { return aadharFile; }
    public void setAadharFile(MultipartFile aadharFile) { this.aadharFile = aadharFile; }

    public MultipartFile getPanFile() { return panFile; }
    public void setPanFile(MultipartFile panFile) { this.panFile = panFile; }

}
