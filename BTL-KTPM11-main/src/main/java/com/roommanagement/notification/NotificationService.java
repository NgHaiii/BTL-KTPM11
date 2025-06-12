package com.roommanagement.notification;

import com.roommanagement.database.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NotificationService {
    public void sendNotification(String tenantName, String message) {
        try (Connection conn = DatabaseManager.connect()) {
            String sql = "INSERT INTO notifications (tenant_name, message) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tenantName);
            pstmt.setString(2, message);
            pstmt.executeUpdate();
            System.out.println("✅ Đã gửi thông báo cho " + tenantName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}