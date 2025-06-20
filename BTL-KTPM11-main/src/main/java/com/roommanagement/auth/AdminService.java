    package com.roommanagement.auth;

import com.roommanagement.database.DatabaseManager;
import com.roommanagement.auth.AdminModel.RoomEntry;
import com.roommanagement.auth.AdminModel.TenantEntry;
import com.roommanagement.tenant.TenantInfo;

import javafx.scene.control.Label;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.roommanagement.businessaddress.BusinessAddressEntry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.nio.charset.StandardCharsets;
import java.io.InputStream;

public class AdminService {
    private List<Province> provinces;
    private List<AdminModel.RoomEntry> roomList = new ArrayList<>();

    public AdminService() {
        try (InputStream is = getClass().getResourceAsStream("/vietnam-provinces.json")) {
    if (is != null) {
        String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        provinces = new Gson().fromJson(json, new TypeToken<List<Province>>(){}.getType());
        System.out.println("Số tỉnh/thành: " + provinces.size());
    } else {
        System.err.println("Không tìm thấy file db.json trong resources!");
        provinces = List.of();
    }
} catch (Exception e) {
    e.printStackTrace();
    provinces = List.of();
}
    }
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

        File userDir = new File("data/" + username);
        if (!userDir.exists()) {
            userDir.mkdirs();
        }

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
         ResultSet rs = stmt.executeQuery("SELECT name, size, type, status, address, chuHo FROM rooms")) {
        while (rs.next()) {
            list.add(new RoomEntry(
                    rs.getString("name"),
                    rs.getString("size"),
                    rs.getString("type"),
                    rs.getString("status"),
                    rs.getString("address"),
                    rs.getString("chuHo")
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return list;
}

    // Load dữ liệu hóa đơn
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
 
// thêm phòng
    public void addRoom(String name, String size, String type, String status, String address, String chuHo) {
        AdminModel.RoomEntry room = new AdminModel.RoomEntry(name, size, type, status, address, chuHo);
        roomList.add(room);

        // Lưu vào database
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO rooms (name, size, type, status, address, chuHo) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, size);
            ps.setString(3, type);
            ps.setString(4, status);
            ps.setString(5, address);
            ps.setString(6, chuHo);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

private Connection getConnection() throws SQLException {
    return DatabaseManager.connect();
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
    // xóa địa chỉ kinh doanh
    public void deleteBusinessAddress(BusinessAddressEntry entry) {
    String sql = "DELETE FROM business_addresses WHERE id = ?";
    try (Connection conn = DatabaseManager.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, entry.getId());
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    // Load dữ liệu địa chỉ kinh doanh
public void addBusinessAddress(String name, String birthday, String phone, String soNha, String address, String province, String district, String ward) {
    String sql = "INSERT INTO business_addresses (name, birthday, phone, so_nha, address, province, district, ward) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    try (Connection conn = DatabaseManager.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, name);
        stmt.setString(2, birthday);
        stmt.setString(3, phone);
        stmt.setString(4, soNha); // Lưu số nhà vào cột so_nha
        stmt.setString(5, address);
        stmt.setString(6, province);
        stmt.setString(7, district);
        stmt.setString(8, ward);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    // địa chỉ kinh doanh
    public List<BusinessAddressEntry> loadBusinessAddresses() {
    List<BusinessAddressEntry> list = new ArrayList<>();
    String sql = "SELECT * FROM business_addresses";
    try (Connection conn = DatabaseManager.connect();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
            BusinessAddressEntry entry = new BusinessAddressEntry(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("birthday"),
                rs.getString("phone"),
                rs.getString("so_nha"), 
                rs.getString("address"),
                rs.getString("province"),
                rs.getString("district"),
                rs.getString("ward")
            );
            list.add(entry);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return list;
}



    // Lấy tên hiển thị cho người dùng
public javafx.scene.image.Image getAvatarForUser(String username) {
    try {
        File avatarFile = new File("avatars", username + ".png");
        if (avatarFile.exists()) {
            return new javafx.scene.image.Image(avatarFile.toURI().toString());
        }
        java.net.URL url = getClass().getResource("/images/mac-dinh.jpg");
        if (url != null) {
            return new javafx.scene.image.Image(url.toExternalForm());
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    // Nếu vẫn không có, trả về null hoặc một ảnh rỗng
    return new javafx.scene.image.Image("https://via.placeholder.com/120"); // Ảnh placeholder online
}

public void saveAvatarForUser(String username, File file) {
    try {
        File dir = new File("avatars");
        if (!dir.exists()) dir.mkdirs();
        File dest = new File(dir, username + ".png");
        java.nio.file.Files.copy(
            file.toPath(),
            dest.toPath(),
            java.nio.file.StandardCopyOption.REPLACE_EXISTING
        );
    } catch (Exception e) {
        e.printStackTrace();
    }
}
public String getPhone(String username) {
    try (Connection conn = DatabaseManager.connect()) {
        PreparedStatement stmt = conn.prepareStatement("SELECT phone FROM users WHERE username = ?");
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            String phone = rs.getString("phone");
            return (phone != null && !phone.isEmpty()) ? phone : "Chưa cập nhật";
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return "Chưa cập nhật";
}

public String getEmail(String username) {
    try (Connection conn = DatabaseManager.connect()) {
        PreparedStatement stmt = conn.prepareStatement("SELECT email FROM users WHERE username = ?");
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            String email = rs.getString("email");
            return (email != null && !email.isEmpty()) ? email : "Chưa cập nhật";
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return "Chưa cập nhật";
}

public String getDisplayName(String username) {
    
    try (Connection conn = DatabaseManager.connect()) {
        PreparedStatement stmt = conn.prepareStatement("SELECT username FROM users WHERE username = ?");
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            String name = rs.getString("username");
            return (name != null && !name.isEmpty()) ? name : "Chưa cập nhật";
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return "Chưa cập nhật";
}
public List<String> getAllUsernames() {
    List<String> usernames = new ArrayList<>();
    try (Connection conn = DatabaseManager.connect()) {
        PreparedStatement stmt = conn.prepareStatement("SELECT username FROM users");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            usernames.add(rs.getString("username"));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return usernames;
}
public void deleteAccount(String username) {
    
}

public Map<String, Map<String, List<String>>> getDiaChiData() {
    Map<String, Map<String, List<String>>> diaChiData = new HashMap<>();
    for (Province province : provinces) {
        Map<String, List<String>> districts = new HashMap<>();
        for (District district : province.getDistricts()) {
            districts.put(district.getName(), district.getWards().stream().map(Ward::getName).collect(Collectors.toList()));
        }
        diaChiData.put(province.getName(), districts);
    }
    return diaChiData;
}

    public void deleteRoom(RoomEntry room) {
    try (Connection conn = DatabaseManager.connect()) {
        String sql = "DELETE FROM rooms WHERE name = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, room.getName());
        pstmt.executeUpdate();
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
}

    public List<AdminModel.RoomEntry> getAllRooms() {
        return roomList;
    }

public void deleteAvatarForUser(String username) {
    File avatarFile = new File("avatars", username + ".png");
    if (avatarFile.exists()) {
        avatarFile.delete();
    }
}
// lấy địa danh
public List<String> getAllProvinces() {
    return provinces.stream().map(Province::getName).toList();
}

public List<String> getDistrictsByProvince(String provinceName) {
    return provinces.stream()
        .filter(p -> p.getName().equals(provinceName))
        .findFirst()
        .map(p -> p.getDistricts().stream().map(District::getName).toList())
        .orElse(List.of());
}

public List<String> getWardsByDistrict(String provinceName, String districtName) {
    return provinces.stream()
        .filter(p -> p.getName().equals(provinceName))
        .findFirst()
        .flatMap(p -> p.getDistricts().stream()
            .filter(d -> d.getName().equals(districtName))
            .findFirst())
        .map(d -> d.getWards().stream().map(Ward::getName).toList())
        .orElse(List.of());
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