package com.roommanagement.tenant;

import com.roommanagement.database.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TenantService {
    public void addTenant(String name, String phone, String address) {
        try (Connection conn = DatabaseManager.connect()) {
            String sql = "INSERT INTO tenants (name, phone, address) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, address);
            pstmt.executeUpdate();
            System.out.println("✅ Đã thêm người thuê: " + name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<TenantEntry> listTenants() {
        List<TenantEntry> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement("SELECT name, phone, address FROM tenants");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(new TenantEntry(rs.getString("name"), rs.getString("phone"), rs.getString("address")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Model dữ liệu người thuê
    public static class TenantEntry {
        private final String name;
        private final String phone;
        private final String address;

        public TenantEntry(String name, String phone, String address) {
            this.name = name;
            this.phone = phone;
            this.address = address;
        }

        public String getName() { return name; }
        public String getPhone() { return phone; }
        public String getAddress() { return address; }
    }
}
