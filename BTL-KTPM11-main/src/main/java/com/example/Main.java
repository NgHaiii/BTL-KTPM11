

package com.example;

import com.roommanagement.auth.AdminView;
import com.roommanagement.database.DatabaseManager;
import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        // Khởi tạo cơ sở dữ liệu trước khi chạy ứng dụng
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.initializeDatabase();

        System.out.println("Database initialized successfully!");

        // Chạy ứng dụng JavaFX
        Application.launch(AdminView.class, args);
    }
}

