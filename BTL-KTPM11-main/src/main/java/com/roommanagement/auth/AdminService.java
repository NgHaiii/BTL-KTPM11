    package com.roommanagement.auth;

import com.roommanagement.database.DatabaseManager;
import com.roommanagement.auth.AdminModel.RoomEntry;
/*import com.roommanagement.auth.AdminModel.BillEntry;*/
import com.roommanagement.auth.AdminModel.TenantEntry;
import javafx.scene.control.Label;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminService {
    // Đăng nhập Admin
    public boolean loginAdmin(String username, String password) {
        try (Connection conn = DatabaseManager.connect()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    /*private final InvoiceService invoiceService = new InvoiceService();*/

    // Đăng ký Admin
    public void registerAdmin(String email, String phone, String username, String password, Label lblRegStatus) {
        try (Connection conn = DatabaseManager.connect()) {
            String checkSql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                lblRegStatus.setText("Tên đăng nhập đã tồn tại!");
                return;
            }
            String sql = "INSERT INTO users (email, phone, username, password) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, phone);
            pstmt.setString(3, username);
            pstmt.setString(4, password);
            pstmt.executeUpdate();
            lblRegStatus.setText("Đăng ký thành công! Bạn có thể đăng nhập.");
        } catch (SQLException e) {
            lblRegStatus.setText("Lỗi khi đăng ký: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Thêm người thuê
    public void addTenant(int roomId, String name, String phone, String address) {
        try (Connection conn = DatabaseManager.connect()) {
            String sql = "INSERT INTO tenants (room_id, name, phone, address) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, roomId);
            pstmt.setString(2, name);
            pstmt.setString(3, phone);
            pstmt.setString(4, address);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Thêm phòng
    public void addRoom(String name, String size, String type, String status) {
        try (Connection conn = DatabaseManager.connect()) {
            String sql = "INSERT INTO rooms (name, size, type, status) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, size);
            pstmt.setString(3, type);
            pstmt.setString(4, status);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Cập nhật trạng thái phòng
    public void updateRoomStatus(String roomName, String newStatus) {
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE rooms SET status = ? WHERE name = ?")) {
            pstmt.setString(1, newStatus);
            pstmt.setString(2, roomName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Xóa hóa đơn
    public void deleteBill(BillEntry bill) {
        try (Connection conn = DatabaseManager.connect()) {
            String sql = "DELETE FROM bills WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bill.getId());
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Xóa người thuê
    public void deleteTenant(TenantEntry tenant) {
        try (Connection conn = DatabaseManager.connect()) {
            int roomId = -1;
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT id FROM rooms WHERE name = ?")) {
                pstmt.setString(1, tenant.getRoom());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    roomId = rs.getInt("id");
                }
            }
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "DELETE FROM tenants WHERE name = ? AND phone = ? AND address = ?")) {
                pstmt.setString(1, tenant.getName());
                pstmt.setString(2, tenant.getPhone());
                pstmt.setString(3, tenant.getAddress());
                pstmt.executeUpdate();
            }
            if (roomId != -1) {
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "UPDATE rooms SET status = 'Trống' WHERE id = ?")) {
                    pstmt.setInt(1, roomId);
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Load dữ liệu người thuê
    public List<TenantEntry> loadTenantData() {
        List<TenantEntry> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT t.name, t.phone, t.address, r.name as room_name " +
                             "FROM tenants t JOIN rooms r ON t.room_id = r.id")) {
            while (rs.next()) {
                list.add(new TenantEntry(
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("room_name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Load dữ liệu phòng
    public List<RoomEntry> loadRoomData() {
        List<RoomEntry> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name, size, type, status FROM rooms")) {
            while (rs.next()) {
                list.add(new RoomEntry(
                        rs.getString("name"),
                        rs.getString("size"),
                        rs.getString("type"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    // Load dữ liệu hóa đơn
    // ...existing code...
public List<BillEntry> loadBills() {
    List<BillEntry> bills = new ArrayList<>();
    try (Connection conn = DatabaseManager.connect();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(
                 "SELECT b.id, b.amount, b.description, t.name as tenantName, b.pdf_path " +
                         "FROM bills b JOIN tenants t ON b.tenant_id = t.id")) {
        while (rs.next()) {
            bills.add(new BillEntry(
                    rs.getInt("id"),
                    rs.getString("tenantName"),
                    rs.getDouble("amount"),
                    rs.getString("description"),
                    rs.getString("pdf_path")
            ));
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
    return bills;
}

    // Gửi thông báo
    public void sendNotification(String tenantName, String message, Label lblNotifyStatus) {
        try (Connection conn = DatabaseManager.connect()) {
            String sql = "INSERT INTO notifications (tenant_name, message) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tenantName);
            pstmt.setString(2, message);
            pstmt.executeUpdate();
            lblNotifyStatus.setText("Đã gửi thông báo cho " + tenantName);
        } catch (SQLException ex) {
            lblNotifyStatus.setText("Lỗi khi gửi thông báo: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    // Lấy danh sách tên người thuê
    public List<String> getTenantNames() {
        List<String> tenantNames = new ArrayList<>();
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM tenants")) {
            while (rs.next()) {
                tenantNames.add(rs.getString("name"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return tenantNames;
    }

    // Lấy thông tin người thuê (phòng, SĐT, địa chỉ)
    public TenantInfo getTenantInfo(String tenantName) {
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT t.room_id, t.phone, t.address, r.name as room_name FROM tenants t JOIN rooms r ON t.room_id = r.id WHERE t.name = ?")) {
            stmt.setString(1, tenantName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new TenantInfo(
                        rs.getString("room_name"),
                        rs.getString("phone"),
                        rs.getString("address")
                );
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new TenantInfo("", "", "");
    }

    // Lấy danh sách hóa đơn của 1 người thuê
        List<BillEntry> bills = new ArrayList<>();
    public List<BillEntry> getBillsByTenant(String tenantName) {
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT b.id, b.amount, b.description, b.pdf_path FROM bills b " +
                             "JOIN tenants t ON b.tenant_id = t.id WHERE t.name = ?")) {
            stmt.setString(1, tenantName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                BillEntry bill = new BillEntry(
                        rs.getInt("id"),
                        tenantName,
                        rs.getDouble("amount"),
                        rs.getString("description"),
                        rs.getString("pdf_path")
                );
                bills.add(bill);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return bills;
    }

    // Model thông tin người thuê
    public static class TenantInfo {
        public final String room, phone, address;
        public TenantInfo(String room, String phone, String address) {
            this.room = room; this.phone = phone; this.address = address;
        }
    }

    // Model hóa đơn
    public static class BillEntry {
        private final int id;
        private final String tenantName;
        private final double amount;
        private final String description;
        private final String pdfPath;

        public BillEntry(int id, String tenantName, double amount, String description, String pdfPath) {
            this.id = id;
            this.tenantName = tenantName;
            this.amount = amount;
            this.description = description;
            this.pdfPath = pdfPath;
        }
        public int getId() { return id; }
        public String getTenantName() { return tenantName; }
        public double getAmount() { return amount; }
        public String getDescription() { return description; }
        public String getPdfPath() { return pdfPath; }
    }
}