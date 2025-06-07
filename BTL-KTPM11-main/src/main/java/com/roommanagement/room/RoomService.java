package com.roommanagement.room;

import com.roommanagement.database.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomService {
    public void addRoom(String roomName, String status) {
        try (Connection conn = DatabaseManager.connect()) {
            String sql = "INSERT INTO rooms (room_name, status) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, roomName);
            pstmt.setString(2, status);
            pstmt.executeUpdate();
            System.out.println("✅ Đã thêm phòng: " + roomName + " (Trạng thái: " + status + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<RoomEntry> listRooms() {
        List<RoomEntry> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement("SELECT room_name, status FROM rooms");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(new RoomEntry(rs.getString("room_name"), rs.getString("status")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Model dữ liệu phòng
    public static class RoomEntry {
        private final String roomName;
        private final String status;

        public RoomEntry(String roomName, String status) {
            this.roomName = roomName;
            this.status = status;
        }

        public String getRoomName() { return roomName; }
        public String getStatus() { return status; }
    }
}
