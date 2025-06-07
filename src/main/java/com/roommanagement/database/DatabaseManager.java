package com.roommanagement.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager { // Đổi tên class từ TestConnection để phù hợp với hệ thống
    private static final String URL = "jdbc:mysql://192.168.1.41:3306/quanliphongtro";
    private static final String USER = "root";
    private static final String PASSWORD = "hai120305";

    // Phương thức kết nối MySQL
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Kiểm tra kết nối
    public static void main(String[] args) {
        try (Connection conn = connect()) {
            System.out.println("✅ Kết nối MySQL thành công!");
        } catch (SQLException e) {
            System.out.println("❌ Kết nối thất bại: " + e.getMessage());
        }
    }
}
