package com.roommanagement.notification;

import com.roommanagement.database.DatabaseManager;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class NotificationService {
    // Lấy thông tin người thuê (số điện thoại, địa chỉ) từ DB
    public Map<String, String> getTenantInfo(String tenantName) {
        Map<String, String> info = new HashMap<>();
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT phone, address FROM tenants WHERE name = ?")) {
            stmt.setString(1, tenantName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                info.put("phone", rs.getString("phone"));
                info.put("address", rs.getString("address"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return info;
    }

    // Gửi thông báo (lưu vào bảng notifications)
    public boolean sendNotification(String tenantName, String message) {
        try (Connection conn = DatabaseManager.connect()) {
            String sql = "INSERT INTO notifications (tenant_name, message) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tenantName);
            pstmt.setString(2, message);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}