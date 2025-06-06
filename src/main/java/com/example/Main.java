package com.example;
import com.roommanagement.auth.AdminService;
import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        // Khởi động ứng dụng 
        Application.launch(AdminService.class, args);
    }
}
