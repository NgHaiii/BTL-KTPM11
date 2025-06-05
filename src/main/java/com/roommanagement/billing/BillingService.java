package com.roommanagement.billing;

import com.roommanagement.database.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BillingService {
    public void addBill(String tenantName, double amount) {
        try (Connection conn = DatabaseManager.connect()) {
            String sql = "INSERT INTO bills (tenant_name, amount) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tenantName);
            pstmt.setDouble(2, amount);
            pstmt.executeUpdate();
            System.out.println("✅ Hóa đơn đã được tạo cho " + tenantName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
