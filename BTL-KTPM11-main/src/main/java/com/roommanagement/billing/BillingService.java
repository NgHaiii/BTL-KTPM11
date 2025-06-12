/*package com.roommanagement.billing;

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
}*/

package com.roommanagement.billing;

import com.roommanagement.database.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillingService {

    /**
     * Tạo hóa đơn cho người thuê.
     *
     * @param tenantName  Tên người thuê.
     * @param amount      Số tiền hóa đơn.
     * @param description Mô tả hóa đơn.
     * @return true nếu tạo hóa đơn thành công; false nếu không tìm thấy người thuê hoặc xảy ra lỗi.
     */
    public boolean createBill(String tenantName, double amount, String description) {
        String findTenantSql = "SELECT id, room_id FROM tenants WHERE name = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement findTenantStmt = conn.prepareStatement(findTenantSql)) {

            findTenantStmt.setString(1, tenantName);
            try (ResultSet rs = findTenantStmt.executeQuery()) {
                if (rs.next()) {
                    int tenantId = rs.getInt("id");
                    Integer roomId = rs.getInt("room_id");

                    if (rs.wasNull()) {
                        roomId = null;
                    }

                    String sql = "INSERT INTO bills (tenant_id, room_id, amount, description, status) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setInt(1, tenantId);
                        if (roomId != null) {
                            pstmt.setInt(2, roomId);
                        } else {
                            pstmt.setNull(2, Types.INTEGER);
                        }
                        pstmt.setDouble(3, amount);
                        pstmt.setString(4, description);
                        pstmt.setString(5, "pending");
                        pstmt.executeUpdate();
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Xóa hóa đơn theo ID.
     *
     * @param bill BillEntry cần xóa.
     */
    public void deleteBill(BillEntry bill) {
        String sql = "DELETE FROM bills WHERE id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bill.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tải danh sách hóa đơn từ cơ sở dữ liệu.
     *
     * @return Danh sách hóa đơn.
     */
    public List<BillEntry> loadBills() {
        List<BillEntry> list = new ArrayList<>();
        String sql = "SELECT b.id, b.amount, b.description, t.name as tenantName " +
                     "FROM bills b JOIN tenants t ON b.tenant_id = t.id";

        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new BillEntry(
                        rs.getInt("id"),
                        rs.getString("tenantName"), // Đảm bảo lấy tên người thuê
                        rs.getDouble("amount"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- Model dữ liệu hóa đơn ---
    public static class BillEntry {
        private int id;
        private String tenantName;
        private double amount;
        private String description;

        public BillEntry(int id, String tenantName, double amount, String description) {
            this.id = id;
            this.tenantName = tenantName;
            this.amount = amount;
            this.description = description;
        }

        public int getId() {
            return id;
        }

        public String getTenantName() {
            return tenantName;
        }

        public double getAmount() {
            return amount;
        }

        public String getDescription() {
            return description;
        }
    }
}
