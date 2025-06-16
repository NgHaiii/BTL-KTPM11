/*package com.roommanagement.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager { 
    private static final String URL = "jdbc:mysql://localhost:3306/quanliphongtro";
    private static final String USER = "root";
    private static final String PASSWORD = "hai120305";
    
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    
    public static void main(String[] args) {
        try (Connection conn = connect()) {
            System.out.println("✅ Kết nối MySQL thành công!");
        } catch (SQLException e) {
            System.out.println("❌ Kết nối thất bại: " + e.getMessage());
        }
    }
}*/


package com.roommanagement.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    public static Connection connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(
                "jdbc:mysql://mysql-24165cb3-nghaiii-205.e.aivencloud.com:18866/defaultdb?ssl-mode=REQUIRED",
                "avnadmin", "AVNS_QyWkSSdtZ1pLMHRex2n"
            );
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void initializeDatabase() {
        Connection conn = DatabaseManager.connect();
        if (conn == null) {
            System.out.println("Could not connect to database!");
            return;
        }
        try (Statement stmt = conn.createStatement()) {
            // 1. users
            stmt.execute("""
    CREATE TABLE IF NOT EXISTS users (
        id INT AUTO_INCREMENT PRIMARY KEY,
        username VARCHAR(100) NOT NULL UNIQUE,
        password VARCHAR(255) NOT NULL,
        full_name VARCHAR(255),
        phone VARCHAR(20),
        email VARCHAR(100),
        role VARCHAR(50)
    );
""");


            // 2. business_addresses
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS business_addresses (
        id INT AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        birthday DATE,
        phone VARCHAR(20),
        so_nha VARCHAR(100),
        address VARCHAR(255),
        province VARCHAR(100),
        district VARCHAR(100),
        ward VARCHAR(100),
        user_id INT,
        FOREIGN KEY (user_id) REFERENCES users(id)
    );
            """);

            // 3. rooms
            stmt.execute("""
    CREATE TABLE IF NOT EXISTS rooms (
        id INT AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        price DECIMAL(10,2),
        status VARCHAR(50),
        owner_id INT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        type VARCHAR(50),
        size VARCHAR(50),
        user_id INT,
        address VARCHAR(255),
        chuHo VARCHAR(255),
        FOREIGN KEY (owner_id) REFERENCES users(id),
        FOREIGN KEY (user_id) REFERENCES users(id)
    );
""");


            // 4. room (nếu bạn dùng bảng này riêng biệt)
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS room (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(50),
                    area VARCHAR(50),
                    type VARCHAR(50),
                    status VARCHAR(50),
                    address VARCHAR(255)
                );
            """);

            // 5. tenants
            stmt.execute("""
    CREATE TABLE IF NOT EXISTS tenants (
        id INT AUTO_INCREMENT PRIMARY KEY,
        user_id INT,
        room_id INT,
        rental_start_date DATE,
        rental_end_date DATE,
        name VARCHAR(255),
        address VARCHAR(255),
        phone VARCHAR(15),
        FOREIGN KEY (user_id) REFERENCES users(id),
        FOREIGN KEY (room_id) REFERENCES rooms(id)
    );
""");


            // 6. bills
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS bills (
        id INT AUTO_INCREMENT PRIMARY KEY,
        tenant_id INT,
        room_id INT,
        amount DECIMAL(12,2),
        bill_date DATETIME,
        status VARCHAR(50),
        description TEXT,
        user_id INT,
        pdf_path VARCHAR(255),
        FOREIGN KEY (tenant_id) REFERENCES tenants(id),
        FOREIGN KEY (room_id) REFERENCES rooms(id),
        FOREIGN KEY (user_id) REFERENCES users(id)
    );
            """);

            // 7. notifications
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS notifications (
        id INT AUTO_INCREMENT PRIMARY KEY,
        user_id INT,
        tenant_id INT,
        message TEXT NOT NULL,
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
        status VARCHAR(50) DEFAULT 'unread',
        tenant_name VARCHAR(255),
        FOREIGN KEY (user_id) REFERENCES users(id),
        FOREIGN KEY (tenant_id) REFERENCES tenants(id)
    );
            """);

            System.out.println("All 7 tables created or already exist!");
        } catch (SQLException e) {
            System.out.println("Error in table creation");
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}