package com.roommanagement.auth;

import com.roommanagement.database.DatabaseManager;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

@SuppressWarnings("unchecked") // Thêm để suppress cảnh báo addAll TableColumn
public class AdminService extends Application {

    // ObservableList dùng để lưu trữ dữ liệu hiển thị tại bảng
    private ObservableList<TenantEntry> tenantList = FXCollections.observableArrayList();
    private ObservableList<RoomEntry> roomList = FXCollections.observableArrayList();
    // ...existing code...
private ObservableList<BillEntry> billList = FXCollections.observableArrayList();
// ...existing code...
    
    // Lưu tham chiếu đến Stage chính để dễ chuyển đổi giao diện
    private Stage primaryStage;

    // ============================
    // 1. PHƯƠNG THỨC CRUD (gọi DatabaseManager)
    // ============================
    
    // Đăng ký Admin: lưu thông tin vào bảng users
    public void registerAdmin(String email, String phone, String username, String password) {
        try (Connection conn = DatabaseManager.connect()) {
            String sql = "INSERT INTO users (email, phone, username, password) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, phone);
            pstmt.setString(3, username);
            pstmt.setString(4, password);
            pstmt.executeUpdate();
            System.out.println("✅ Admin registered: " + username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Đăng nhập Admin: truy vấn bảng users
    public boolean loginAdmin(String username, String password) {
        try (Connection conn = DatabaseManager.connect()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();  // Đăng nhập thành công nếu có dòng trả về
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Thêm người thuê: lưu thông tin vào bảng tenants (trường: user_id, room_id, rental_start, rental_end, name, phone, address)
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
    
    // Thêm phòng: lưu thông tin vào bảng rooms (trường: room_name, status)
    public void addRoom(String roomName, String status) {
        try (Connection conn = DatabaseManager.connect()) {
            String sql = "INSERT INTO rooms (room_name, status) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, roomName);
            pstmt.setString(2, status);
            pstmt.executeUpdate();
            System.out.println("✅ Room added: " + roomName + " (Status: " + status + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ============================
    // 2. PHƯƠNG THỨC LOAD DỮ LIỆU TỪ CSDL
    // ============================
    
    private List<TenantEntry> loadTenantData() {
        List<TenantEntry> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name, phone, address FROM tenants")) {
            while (rs.next()) {
                list.add(new TenantEntry(rs.getString("name"), rs.getString("phone"), rs.getString("address")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    private List<RoomEntry> loadRoomData() {
        List<RoomEntry> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT room_name, status FROM rooms")) {
            while (rs.next()) {
                list.add(new RoomEntry(rs.getString("room_name"), rs.getString("status")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ============================
    // 3. GIAO DIỆN CHÍNH: ĐĂNG KÍ / ĐĂNG NHẬP, DASHBOARD & MENU CHỨC NĂNG
    // ============================
    
    // 3.1. Giao diện đăng ký/đăng nhập (sử dụng TabPane)
    private void showAuthPane() {
        primaryStage.setTitle("Admin: Đăng Kí / Đăng Nhập");

        TabPane tabPane = new TabPane();

        // Tab Đăng Kí
        Tab tabRegister = new Tab("Đăng Kí");
        GridPane regPane = new GridPane();
        regPane.setHgap(10);
        regPane.setVgap(10);
        regPane.setPadding(new Insets(10));

        TextField txtEmail = new TextField();
        txtEmail.setPromptText("Nhập email");
        TextField txtPhone = new TextField();
        txtPhone.setPromptText("Nhập số điện thoại");
        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Nhập tên đăng nhập");
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Nhập mật khẩu");

        Button btnRegister = new Button("Đăng Kí");
        Label lblRegStatus = new Label();

        btnRegister.setOnAction(e -> {
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();
            String username = txtUsername.getText().trim();
            String password = txtPassword.getText().trim();
            if(email.isEmpty() || phone.isEmpty() || username.isEmpty() || password.isEmpty()) {
                lblRegStatus.setText("Vui lòng điền đầy đủ thông tin.");
            } else {
                registerAdmin(email, phone, username, password);
                lblRegStatus.setText("Đăng ký thành công!");
                txtEmail.clear();
                txtPhone.clear();
                txtUsername.clear();
                txtPassword.clear();
            }
        });

        regPane.add(new Label("Email:"), 0, 0);
        regPane.add(txtEmail, 1, 0);
        regPane.add(new Label("SĐT:"), 0, 1);
        regPane.add(txtPhone, 1, 1);
        regPane.add(new Label("Tên đăng nhập:"), 0, 2);
        regPane.add(txtUsername, 1, 2);
        regPane.add(new Label("Mật khẩu:"), 0, 3);
        regPane.add(txtPassword, 1, 3);
        regPane.add(btnRegister, 1, 4);
        regPane.add(lblRegStatus, 1, 5);

        tabRegister.setContent(regPane);
        tabRegister.setClosable(false);

        // Tab Đăng Nhập
        Tab tabLogin = new Tab("Đăng Nhập");
        GridPane loginPane = new GridPane();
        loginPane.setHgap(10);
        loginPane.setVgap(10);
        loginPane.setPadding(new Insets(10));

        TextField txtLoginUsername = new TextField();
        txtLoginUsername.setPromptText("Nhập tên đăng nhập");
        PasswordField txtLoginPassword = new PasswordField();
        txtLoginPassword.setPromptText("Nhập mật khẩu");

        Button btnLogin = new Button("Đăng Nhập");
        Label lblLoginStatus = new Label();

        btnLogin.setOnAction(e -> {
            String username = txtLoginUsername.getText().trim();
            String password = txtLoginPassword.getText().trim();
            if(username.isEmpty() || password.isEmpty()) {
                lblLoginStatus.setText("Vui lòng nhập thông tin đăng nhập.");
            } else {
                if(loginAdmin(username, password)) {
                    lblLoginStatus.setText("Đăng nhập thành công!");
                    showDashboard(username);
                } else {
                    lblLoginStatus.setText("Đăng nhập thất bại. Kiểm tra lại thông tin.");
                }
            }
        });

        loginPane.add(new Label("Tên đăng nhập:"), 0, 0);
        loginPane.add(txtLoginUsername, 1, 0);
        loginPane.add(new Label("Mật khẩu:"), 0, 1);
        loginPane.add(txtLoginPassword, 1, 1);
        loginPane.add(btnLogin, 1, 2);
        loginPane.add(lblLoginStatus, 1, 3);
        tabLogin.setContent(loginPane);
        tabLogin.setClosable(false);

        tabPane.getTabs().addAll(tabRegister, tabLogin);
        Scene authScene = new Scene(tabPane, 400, 300);
        primaryStage.setScene(authScene);
        primaryStage.show();
    }

    // 3.2. Dashboard chính cho Admin với menu chức năng và nội dung động.
    private void showDashboard(String adminUsername) {
        primaryStage.setTitle("Dashboard - " + adminUsername);

        BorderPane bp = new BorderPane();
        bp.setPadding(new Insets(10));

        // Menu bên trái
        VBox menuBox = new VBox(15);
        menuBox.setPadding(new Insets(10));
        menuBox.setStyle("-fx-background-color: #F0F0F0;");
        Button btnQuanLyNguoiThue = new Button("Quản Lý Người Thuê");
        Button btnQuanLyPhong = new Button("Quản Lý Phòng");
        Button btnTaoHoaDon = new Button("Tạo Hóa Đơn");
        Button btnGuiThongBao = new Button("Gửi Thông Báo");
        Button btnLogout = new Button("Đăng Xuất");
        menuBox.getChildren().addAll(btnQuanLyNguoiThue, btnQuanLyPhong, btnTaoHoaDon, btnGuiThongBao, btnLogout);
        bp.setLeft(menuBox);

        // Nơi chứa nội dung cấp trung tâm sẽ thay đổi theo chức năng được chọn
        StackPane centerPane = new StackPane();
        bp.setCenter(centerPane);

        // Gán hành vi cho nút menu:
        btnQuanLyNguoiThue.setOnAction(e -> {
            // Hiển thị giao diện Quản Lý Người Thuê với form thêm và bảng danh sách
            Pane tenantPane = getTenantManagementPane();
            bp.setCenter(tenantPane);
        });

        btnQuanLyPhong.setOnAction(e -> {
            // Hiển thị giao diện Quản Lý Phòng
            Pane roomPane = getRoomManagementPane();
            bp.setCenter(roomPane);
        });

    btnTaoHoaDon.setOnAction(e -> {
    VBox billPane = new VBox(10);
    billPane.setPadding(new Insets(10));

    // Lấy danh sách tên người thuê từ bảng tenants
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

    ComboBox<String> cbTenant = new ComboBox<>(FXCollections.observableArrayList(tenantNames));
    cbTenant.setPromptText("Chọn người thuê");
    TextField txtAmount = new TextField();
    txtAmount.setPromptText("Số tiền");
    TextField txtDesc = new TextField();
    txtDesc.setPromptText("Mô tả");
    Button btnAddBill = new Button("Tạo hóa đơn");
    Label lblBillStatus = new Label();

    // Hàm nạp lại danh sách hóa đơn từ DB (KHAI BÁO TRƯỚC)
    Runnable loadBills = () -> {
        billList.clear();
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT b.id, b.amount, b.description, t.name as tenantName " +
                 "FROM bills b JOIN tenants t ON b.tenant_id = t.id")) {
            while (rs.next()) {
                billList.add(new BillEntry(
                    rs.getInt("id"),
                    rs.getString("tenantName"),
                    rs.getDouble("amount"),
                    rs.getString("description")
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    };

    // TableView hiển thị danh sách hóa đơn
    TableView<BillEntry> billTable = new TableView<>(billList);
    billTable.setPrefHeight(200);

    TableColumn<BillEntry, String> colTenant = new TableColumn<>("Người thuê");
    colTenant.setCellValueFactory(new PropertyValueFactory<>("tenantName"));

    TableColumn<BillEntry, Double> colAmount = new TableColumn<>("Số tiền");
    colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));

    TableColumn<BillEntry, String> colDesc = new TableColumn<>("Mô tả");
    colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));

    // Thêm cột xóa
    TableColumn<BillEntry, Void> colDelete = new TableColumn<>("Xóa");
    colDelete.setCellFactory(param -> new TableCell<>() {
        private final Button btnDelete = new Button("Xóa");

        {
            btnDelete.setOnAction(event -> {
                BillEntry bill = getTableView().getItems().get(getIndex());
                deleteBill(bill); // Gọi hàm xóa hóa đơn
                loadBills.run();  // Cập nhật lại bảng
            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(btnDelete);
            }
        }
    });

    billTable.getColumns().addAll(colTenant, colAmount, colDesc, colDelete);

    // Nạp dữ liệu hóa đơn lần đầu
    loadBills.run();

    btnAddBill.setOnAction(ev -> {
        String tenant = cbTenant.getValue();
        String amountStr = txtAmount.getText().trim();
        String desc = txtDesc.getText().trim();
        if (tenant == null || tenant.isEmpty() || amountStr.isEmpty()) {
            lblBillStatus.setText("Vui lòng chọn người thuê và nhập số tiền.");
            return;
        }
        try {
            double amount = Double.parseDouble(amountStr);
            try (Connection conn = DatabaseManager.connect()) {
                // Lấy tenant_id và room_id từ tên người thuê
                String findTenantSql = "SELECT id, room_id FROM tenants WHERE name = ?";
                PreparedStatement findTenantStmt = conn.prepareStatement(findTenantSql);
                findTenantStmt.setString(1, tenant);
                ResultSet rs = findTenantStmt.executeQuery();
                if (rs.next()) {
                    int tenantId = rs.getInt("id");
                    int roomId = rs.getInt("room_id");
                    String sql = "INSERT INTO bills (tenant_id, room_id, amount, description, status) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, tenantId);
                    pstmt.setInt(2, roomId);
                    pstmt.setDouble(3, amount);
                    pstmt.setString(4, desc);
                    pstmt.setString(5, "pending");
                    pstmt.executeUpdate();
                    lblBillStatus.setText("Đã tạo hóa đơn cho " + tenant + " | Mô tả: " + desc);
                    cbTenant.setValue(null);
                    txtAmount.clear();
                    txtDesc.clear();
                    // Nạp lại danh sách hóa đơn
                    loadBills.run();
                } else {
                    lblBillStatus.setText("Không tìm thấy người thuê này!");
                }
            }
        } catch (NumberFormatException ex) {
            lblBillStatus.setText("Số tiền không hợp lệ!");
        } catch (SQLException ex) {
            lblBillStatus.setText("Lỗi khi tạo hóa đơn: " + ex.getMessage());
            ex.printStackTrace();
        }
    });

    billPane.getChildren().addAll(
        new Label("Tạo Hóa Đơn"),
        cbTenant, txtAmount, txtDesc, btnAddBill, lblBillStatus,
        new Label("Danh sách hóa đơn:"), billTable
    );
    bp.setCenter(billPane);
});
 btnGuiThongBao.setOnAction(e -> {
    VBox notifyPane = new VBox(10);
    notifyPane.setPadding(new Insets(10));
    TextField txtTenantName = new TextField();
    txtTenantName.setPromptText("Tên người thuê");
    TextField txtMessage = new TextField();
    txtMessage.setPromptText("Nội dung thông báo");
    Button btnSend = new Button("Gửi thông báo");
    Label lblNotifyStatus = new Label();

    btnSend.setOnAction(ev -> {
        String tenantName = txtTenantName.getText().trim();
        String message = txtMessage.getText().trim();
        if (tenantName.isEmpty() || message.isEmpty()) {
            lblNotifyStatus.setText("Vui lòng nhập đủ tên người thuê và nội dung.");
            return;
        }
        try (Connection conn = DatabaseManager.connect()) {
            // Lưu trực tiếp tên người thuê vào bảng notifications
            String sql = "INSERT INTO notifications (tenant_name, message) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tenantName);
            pstmt.setString(2, message);
            pstmt.executeUpdate();
            lblNotifyStatus.setText("Đã gửi thông báo cho " + tenantName);
            txtTenantName.clear();
            txtMessage.clear();
        } catch (SQLException ex) {
            lblNotifyStatus.setText("Lỗi khi gửi thông báo: " + ex.getMessage());
            ex.printStackTrace();
        }
    });

    notifyPane.getChildren().addAll(
        new Label("Gửi Thông Báo"),
        txtTenantName, txtMessage, btnSend, lblNotifyStatus
    );
    bp.setCenter(notifyPane);
});
        btnLogout.setOnAction(e -> showAuthPane());

        Scene scene = new Scene(bp, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    // ============================
    // 4. Các giao diện chức năng cụ thể
    // ============================
    
    // 4.1. Giao diện Quản Lý Người Thuê:
    // Form thêm người thuê kèm TableView hiển thị danh sách người thuê
    private Pane getTenantManagementPane() {
    VBox tenantPane = new VBox(10);
    tenantPane.setPadding(new Insets(10));

    HBox formBox = new HBox(10);
    TextField txtRoomId = new TextField();
    txtRoomId.setPromptText("Số phòng thuê");
    TextField txtName = new TextField();
    txtName.setPromptText("Tên người thuê");
    TextField txtPhone = new TextField();
    txtPhone.setPromptText("SĐT");
    TextField txtAddress = new TextField();
    txtAddress.setPromptText("Địa chỉ");
    Button btnAdd = new Button("Thêm");
    Label lblStatus = new Label();
    formBox.getChildren().addAll(txtRoomId, txtName, txtPhone, txtAddress, btnAdd, lblStatus);

    TableView<TenantEntry> table = new TableView<>();
    table.setPrefHeight(300);
    table.setPrefWidth(600);

    TableColumn<TenantEntry, String> colName = new TableColumn<>("Tên");
    colName.setCellValueFactory(new PropertyValueFactory<>("name"));
    colName.setPrefWidth(180); // Thêm dòng này

    TableColumn<TenantEntry, String> colPhone = new TableColumn<>("SĐT");
    colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
    colPhone.setPrefWidth(120); // Thêm dòng này

    TableColumn<TenantEntry, String> colAddress = new TableColumn<>("Địa chỉ");
    colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
    colAddress.setPrefWidth(180); // Thêm dòng này

    table.getColumns().addAll(colName, colPhone, colAddress);

    tenantList.setAll(loadTenantData());
    table.setItems(tenantList);


    btnAdd.setOnAction(e -> {
        String roomIdStr = txtRoomId.getText().trim();
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();

        if (roomIdStr.isEmpty() || name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            lblStatus.setText("Vui lòng nhập đầy đủ thông tin.");
        } else {
            try {
                int roomId = Integer.parseInt(roomIdStr);
                addTenant(roomId, name, phone, address);
                lblStatus.setText("Đã thêm: " + name);
                txtRoomId.clear();
                txtName.clear();
                txtPhone.clear();
                txtAddress.clear();
                refreshTenantTable();
            } catch (Exception ex) {
                lblStatus.setText("Dữ liệu không hợp lệ!");
            }
        }
    });

    tenantPane.getChildren().clear();
    tenantPane.getChildren().addAll(new Label("Quản Lý Người Thuê"), formBox, table);
    return tenantPane;
}
    // 4.2. Giao diện Quản Lý Phòng:
    // Form thêm phòng kèm TableView hiển thị danh sách phòng
    private Pane getRoomManagementPane() {
        VBox roomPane = new VBox(10);
        roomPane.setPadding(new Insets(10));

        // Form nhập thông tin phòng
        HBox formBox = new HBox(10);
        TextField txtRoomName = new TextField();
        txtRoomName.setPromptText("Tên phòng");
        ComboBox<String> cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll("Trống", "Cho thuê");
        cbStatus.setValue("Trống");
        Button btnAdd = new Button("Thêm");
        Label lblStatus = new Label();
        formBox.getChildren().addAll(txtRoomName, cbStatus, btnAdd, lblStatus);

        // TableView cho danh sách phòng
        TableView<RoomEntry> table = new TableView<>();
        TableColumn<RoomEntry, String> colRoomName = new TableColumn<>("Tên phòng");
        TableColumn<RoomEntry, String> colStatus = new TableColumn<>("Tình trạng");

        colRoomName.setCellValueFactory(new PropertyValueFactory<>("roomName"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(colRoomName, colStatus);
        roomList.setAll(loadRoomData());
        table.setItems(roomList);

        btnAdd.setOnAction(e -> {
            String roomName = txtRoomName.getText().trim();
            String status = cbStatus.getValue();
            if (roomName.isEmpty()) {
                lblStatus.setText("Vui lòng nhập tên phòng.");
            } else {
                addRoom(roomName, status);
                lblStatus.setText("Đã thêm: " + roomName);
                txtRoomName.clear();
                refreshRoomTable();
            }
        });

        roomPane.getChildren().addAll(new Label("Quản Lý Phòng"), formBox, table);
        return roomPane;
    }

    // Cập nhật lại danh sách người thuê sau khi thêm
    private void refreshTenantTable() {
        tenantList.setAll(loadTenantData());
    }

    // Cập nhật lại danh sách phòng sau khi thêm
    private void refreshRoomTable() {
        roomList.setAll(loadRoomData());
    }
    
    // ============================
    // 5. Phương thức start và main
    // ============================
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showAuthPane();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    // ============================
    // 6. Các lớp mô hình (model) được khai báo bên trong
    // (Không thêm file mới)
    // ============================
    // Model dữ liệu người thuê
    private void deleteBill(BillEntry bill) {
    try (Connection conn = DatabaseManager.connect()) {
        String sql = "DELETE FROM bills WHERE id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, bill.getId());
        pstmt.executeUpdate();
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
}
    public static class TenantEntry {
        private final SimpleStringProperty name;
        private final SimpleStringProperty phone;
        private final SimpleStringProperty address;
        
        public TenantEntry(String name, String phone, String address) {
            this.name = new SimpleStringProperty(name);
            this.phone = new SimpleStringProperty(phone);
            this.address = new SimpleStringProperty(address);
        }
        
        public String getName() {
            return name.get();
        }
        public String getPhone() {
            return phone.get();
        }
        public String getAddress() {
            return address.get();
        }
    }
    
    public static class RoomEntry {
        private final SimpleStringProperty roomName;
        private final SimpleStringProperty status;
        
        public RoomEntry(String roomName, String status) {
            this.roomName = new SimpleStringProperty(roomName);
            this.status = new SimpleStringProperty(status);
        }
        
        public String getRoomName() {
            return roomName.get();
        }
        public String getStatus() {
            return status.get();
        }
    }

    // Đưa BillEntry vào bên trong AdminService
    // ...existing code...
public static class BillEntry {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty tenantName;
    private final SimpleDoubleProperty amount;
    private final SimpleStringProperty description;

    public BillEntry(int id, String tenantName, double amount, String description) {
        this.id = new SimpleIntegerProperty(id);
        this.tenantName = new SimpleStringProperty(tenantName);
        this.amount = new SimpleDoubleProperty(amount);
        this.description = new SimpleStringProperty(description);
    }

    public int getId() { return id.get(); }
    public String getTenantName() { return tenantName.get(); }
    public double getAmount() { return amount.get(); }
    public String getDescription() { return description.get(); }

    public SimpleStringProperty tenantNameProperty() { return tenantName; }
    public SimpleDoubleProperty amountProperty() { return amount; }
    public SimpleStringProperty descriptionProperty() { return description; }
}} 