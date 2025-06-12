/*package com.roommanagement.auth;

import com.roommanagement.database.DatabaseManager;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;



@SuppressWarnings("unchecked") 
public class AdminService extends Application {
    
    private ObservableList<TenantEntry> tenantList = FXCollections.observableArrayList();
    private ObservableList<RoomEntry> roomList = FXCollections.observableArrayList();
    
private ObservableList<BillEntry> billList = FXCollections.observableArrayList();  
    
    private Stage primaryStage;

    // Đăng nhập Admin: 
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
    // Thêm người thuê:
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
    // Thêm phòng: 
public void addRoom(String name, String size, String type, String status) {
    try (Connection conn = DatabaseManager.connect()) {
        String sql = "INSERT INTO rooms (name, size, type, status) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, name);
        pstmt.setString(2, size);
        pstmt.setString(3, type);
        pstmt.setString(4, status);
        pstmt.executeUpdate();
        System.out.println("Thêm phòng thành công!");
    } catch (SQLException e) {
        System.err.println("Lỗi khi thêm phòng: " + e.getMessage());
        e.printStackTrace();
    }
}
    // 2. PHƯƠNG THỨC LOAD DỮ LIỆU TỪ CSDL

    private List<TenantEntry> loadTenantData() {
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
    
// giao diện đăng nhập/đăng ký
public void registerAdmin(String email, String phone, String username, String password, Label lblRegStatus) {
    try (Connection conn = DatabaseManager.connect()) {
        // Kiểm tra username đã tồn tại chưa
        String checkSql = "SELECT * FROM users WHERE username = ?";
        PreparedStatement checkStmt = conn.prepareStatement(checkSql);
        checkStmt.setString(1, username);
        ResultSet rs = checkStmt.executeQuery();
        if (rs.next()) {
            lblRegStatus.setText("Tên đăng nhập đã tồn tại!");
            return;
        }
        // Nếu chưa tồn tại thì thêm mới
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
private void showAuthPane() {
    primaryStage.setTitle("Admin: Đăng Kí / Đăng Nhập");

    
    TabPane tabPane = new TabPane();
    tabPane.setStyle(
        "-fx-background-radius: 24;" +
        "-fx-padding: 0;" +
        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 18, 0, 2, 2);"
    );

    // Tab Đăng Kí
    Tab tabRegister = new Tab("Đăng Kí");
    GridPane regPane = new GridPane();
    regPane.setHgap(18);
    regPane.setVgap(22);
    regPane.setPadding(new Insets(36));
    regPane.setStyle(
        "-fx-background-color: rgba(255,255,255,0.65);" +
        "-fx-background-radius: 32;" +
        "-fx-effect: dropshadow(gaussian, rgba(80,80,160,0.18), 24, 0, 2, 2);" +
        "-fx-border-radius: 32;" +
        "-fx-border-color: rgba(120,120,255,0.18);" +
        "-fx-border-width: 2;"
    );

    // Icon cho từng dòng (emoji, có thể thay bằng ImageView)
    Label iconEmail = new Label("\uD83D\uDCE7"); // 📧
    Label iconPhone = new Label("\uD83D\uDCDE"); // 📞
    Label iconUser = new Label("\uD83D\uDC64"); // 👤
    Label iconPass = new Label("\uD83D\uDD12"); // 🔒
    iconEmail.setStyle("-fx-font-size: 18px;");
    iconPhone.setStyle("-fx-font-size: 18px;");
    iconUser.setStyle("-fx-font-size: 18px;");
    iconPass.setStyle("-fx-font-size: 18px;");

    TextField txtEmail = new TextField();
    txtEmail.setPromptText("Nhập email");
    TextField txtPhone = new TextField();
    txtPhone.setPromptText("Nhập số điện thoại");
    TextField txtUsername = new TextField();
    txtUsername.setPromptText("Nhập tên đăng nhập");
    PasswordField txtPassword = new PasswordField();
    txtPassword.setPromptText("Nhập mật khẩu");

    Button btnRegister = new Button("Đăng Kí");
    btnRegister.setStyle(
        "-fx-background-color: #6a5af9;" +
        "-fx-text-fill: white;" +
        "-fx-font-weight: bold;" +
        "-fx-background-radius: 18;" +
        "-fx-font-size: 18px;" +
        "-fx-padding: 10 0 10 0;" +
        "-fx-min-width: 180px;"
    );
Label lblRegStatus = new Label();

btnRegister.setOnAction(e -> {
    String email = txtEmail.getText().trim();
    String phone = txtPhone.getText().trim();
    String username = txtUsername.getText().trim();
    String password = txtPassword.getText().trim();
    if(email.isEmpty() || phone.isEmpty() || username.isEmpty() || password.isEmpty()) {
        lblRegStatus.setText("Vui lòng điền đầy đủ thông tin.");
    } else {
        registerAdmin(email, phone, username, password, lblRegStatus);
        txtEmail.clear();
        txtPhone.clear();
        txtUsername.clear();
        txtPassword.clear();
    }
});
    // Thêm icon vào cột 0, label vào cột 1, input vào cột 2
    regPane.add(iconEmail, 0, 0);
    regPane.add(new Label("Email:"), 1, 0);
    regPane.add(txtEmail, 2, 0);

    regPane.add(iconPhone, 0, 1);
    regPane.add(new Label("SĐT:"), 1, 1);
    regPane.add(txtPhone, 2, 1);

    regPane.add(iconUser, 0, 2);
    regPane.add(new Label("Tên đăng nhập:"), 1, 2);
    regPane.add(txtUsername, 2, 2);

    regPane.add(iconPass, 0, 3);
    regPane.add(new Label("Mật khẩu:"), 1, 3);
    regPane.add(txtPassword, 2, 3);

    regPane.add(btnRegister, 2, 4);
    regPane.add(lblRegStatus, 2, 5);

    tabRegister.setContent(regPane);
    tabRegister.setClosable(false);

    // Tab Đăng Nhập
    Tab tabLogin = new Tab("Đăng Nhập");
    GridPane loginPane = new GridPane();
    loginPane.setHgap(18);
    loginPane.setVgap(22);
    loginPane.setPadding(new Insets(36));
    loginPane.setStyle(
        "-fx-background-color: rgba(255,255,255,0.65);" +
        "-fx-background-radius: 32;" +
        "-fx-effect: dropshadow(gaussian, rgba(80,80,160,0.18), 24, 0, 2, 2);" +
        "-fx-border-radius: 32;" +
        "-fx-border-color: rgba(120,120,255,0.18);" +
        "-fx-border-width: 2;"
    );

    Label iconUserLogin = new Label("\uD83D\uDC64"); // 👤
    Label iconPassLogin = new Label("\uD83D\uDD12"); // 🔒
    iconUserLogin.setStyle("-fx-font-size: 18px;");
    iconPassLogin.setStyle("-fx-font-size: 18px;");

    TextField txtLoginUsername = new TextField();
    txtLoginUsername.setPromptText("Nhập tên đăng nhập");
    PasswordField txtLoginPassword = new PasswordField();
    txtLoginPassword.setPromptText("Nhập mật khẩu");

    Button btnLogin = new Button("Đăng Nhập");
    btnLogin.setStyle(
        "-fx-background-color: #6a5af9;" +
        "-fx-text-fill: white;" +
        "-fx-font-weight: bold;" +
        "-fx-background-radius: 18;" +
        "-fx-font-size: 18px;" +
        "-fx-padding: 10 0 10 0;" +
        "-fx-min-width: 180px;"
    );
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

    loginPane.add(iconUserLogin, 0, 0);
    loginPane.add(new Label("Tên đăng nhập:"), 1, 0);
    loginPane.add(txtLoginUsername, 2, 0);

    loginPane.add(iconPassLogin, 0, 1);
    loginPane.add(new Label("Mật khẩu:"), 1, 1);
    loginPane.add(txtLoginPassword, 2, 1);

    loginPane.add(btnLogin, 2, 2);
    loginPane.add(lblLoginStatus, 2, 3);

    tabLogin.setContent(loginPane);
    tabLogin.setClosable(false);

    tabPane.getTabs().addAll(tabRegister, tabLogin);
    tabPane.setMaxWidth(480);
    tabPane.setMaxHeight(540);

    // StackPane để căn giữa tabPane và đặt ảnh nền
    StackPane root = new StackPane();
    root.getChildren().add(tabPane);
    StackPane.setAlignment(tabPane, Pos.CENTER);

    String bgPath = getClass().getResource("/images/anh-nen.jpg").toExternalForm();
root.setStyle(
    "-fx-background-image: url('" + bgPath + "');" +
    "-fx-background-size: cover;" +
    "-fx-background-position: center center;"
);

    Scene authScene = new Scene(root, 700, 700);
    primaryStage.setScene(authScene);
    primaryStage.show();

}


// 1. Tạo giao diện quản lý phòng
private void updateRoomStatus(String roomName, String newStatus) {
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
private void refreshRoomTable() {
    roomList.setAll(loadRoomData());
}

private List<RoomEntry> loadRoomData() {
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

private Pane getRoomManagementPaneStyled() {
    Pane pane = getRoomManagementPane(); // Đảm bảo đã có hàm getRoomManagementPane()
    if (pane instanceof Region region) {
        region.setStyle("-fx-background-color: #fff; -fx-background-radius: 18; -fx-padding: 24;");
    }
    styleTableViewsInPane(pane);
    return pane;
}

private Pane getRoomManagementPane() {
    roomList.setAll(loadRoomData());
    VBox pane = new VBox(18);
    pane.setPadding(new Insets(32, 32, 32, 32));
    pane.setStyle("-fx-background-color: #fff; -fx-background-radius: 18;");

    Label lblTitle = new Label("Quản Lý Phòng");
    lblTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-padding: 0 0 12 0;");

    TableView<RoomEntry> roomTable = new TableView<>(roomList);
    roomTable.setPrefHeight(300);

    TableColumn<RoomEntry, String> colRoomName = new TableColumn<>("Tên phòng");
    colRoomName.setCellValueFactory(new PropertyValueFactory<>("name"));
    colRoomName.setPrefWidth(120);

    TableColumn<RoomEntry, String> colSize = new TableColumn<>("Diện tích");
    colSize.setCellValueFactory(new PropertyValueFactory<>("size"));
    colSize.setPrefWidth(80);

    TableColumn<RoomEntry, String> colType = new TableColumn<>("Loại phòng");
    colType.setCellValueFactory(new PropertyValueFactory<>("type"));
    colType.setPrefWidth(100);

    TableColumn<RoomEntry, String> colStatus = new TableColumn<>("Trạng thái");
    colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    colStatus.setPrefWidth(100);

    TableColumn<RoomEntry, Void> colDelete = new TableColumn<>("Xóa");
    colDelete.setPrefWidth(60);
    colDelete.setCellFactory(param -> new TableCell<>() {
        private final Button btnDelete = new Button("🗑");
        {
            btnDelete.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand;");
            btnDelete.setOnAction(event -> {
                RoomEntry room = getTableView().getItems().get(getIndex());
                roomList.remove(room);
            });
        }
        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(empty ? null : btnDelete);
        }
    });
    colStatus.setCellFactory(column -> new TableCell<RoomEntry, String>() {
    private final ComboBox<String> comboBox = new ComboBox<>(
        FXCollections.observableArrayList("Trống", "Cho thuê", "Bảo trì")
    );

    {
        comboBox.setStyle("-fx-background-color: #fffbe7; -fx-border-radius: 6; -fx-background-radius: 6;");
        comboBox.setOnAction(e -> {
            RoomEntry room = getTableView().getItems().get(getIndex());
            String newStatus = comboBox.getValue();
            updateRoomStatus(room.getName(), newStatus);
            roomList.setAll(loadRoomData());
        });
    }

    @Override
    protected void updateItem(String status, boolean empty) {
        super.updateItem(status, empty);
        if (empty) {
            setGraphic(null);
        } else {
            comboBox.setValue(status);
            setGraphic(comboBox);
        }
    }
});

    roomTable.getColumns().addAll(colRoomName, colSize, colType, colStatus, colDelete);
    

    // Các trường nhập liệu
    TextField txtRoomName = new TextField();
    txtRoomName.setPromptText("Tên phòng");
    txtRoomName.setStyle("-fx-background-radius: 12; -fx-padding: 8;");

    TextField txtSize = new TextField();
    txtSize.setPromptText("Diện tích (m2)");
    txtSize.setStyle("-fx-background-radius: 12; -fx-padding: 8;");

    ComboBox<String> cbType = new ComboBox<>();
    cbType.getItems().addAll("Thường", "VIP", "Đơn", "Đôi");
    cbType.setPromptText("Loại phòng");
    cbType.setStyle("-fx-background-radius: 12; -fx-padding: 8;");

    ComboBox<String> cbStatus = new ComboBox<>();
    cbStatus.getItems().addAll("Trống", "Đã thuê", "Bảo trì");
    cbStatus.setPromptText("Trạng thái");
    cbStatus.setStyle("-fx-background-radius: 12; -fx-padding: 8;");

    Button btnAddRoom = new Button("➕ Thêm phòng");
    btnAddRoom.setStyle(
        "-fx-background-color: linear-gradient(to right, #43e97b, #38f9d7);" +
        "-fx-text-fill: #fff; -fx-font-weight: bold; -fx-background-radius: 18; -fx-padding: 10 24 10 24; -fx-font-size: 16px;"
    );
    Label lblStatus = new Label();

    btnAddRoom.setOnAction(e -> {
    String name = txtRoomName.getText().trim();
    String size = txtSize.getText().trim();
    String type = cbType.getValue();
    String status = cbStatus.getValue();

    if (name.isEmpty() || size.isEmpty() || type == null || status == null) {
        lblStatus.setText("Vui lòng nhập đầy đủ thông tin phòng.");
        return;
    }
    addRoom(name, size, type, status); // 1. Lưu vào database
    roomList.setAll(loadRoomData());   // 2. Load lại từ database để TableView luôn đúng
    lblStatus.setText("Đã thêm phòng mới!");
    // Xóa trắng các trường nhập
    txtRoomName.clear();
    txtSize.clear();
    cbType.setValue(null);
    cbStatus.setValue(null);
});
    

    HBox inputBox = new HBox(12, txtRoomName, txtSize, cbType, cbStatus, btnAddRoom);
    inputBox.setAlignment(Pos.CENTER_LEFT);

    pane.getChildren().addAll(lblTitle, roomTable, inputBox, lblStatus);
    return pane;
}

// Lấy danh sách phòng "Trống" để cho thuê
private List<String> getAvailableRooms() {
    return roomList.stream()
        .filter(room -> room.getStatus().equals("Trống"))
        .map(RoomEntry::getName)
        .collect(Collectors.toList());
}

// Khi khách thuê phòng, cập nhật trạng thái phòng
private void setRoomStatusToRented(String roomName) {
    for (RoomEntry room : roomList) {
        if (room.getName().equals(roomName)) {
            room.setStatus("Đã thuê");
            break;
        }
    }
}

// Khi xóa khách thuê, chuyển trạng thái phòng về "Trống"
private void setRoomStatusToEmpty(String roomName) {
    for (RoomEntry room : roomList) {
        if (room.getName().equals(roomName)) {
            room.setStatus("Trống");
            break;
        }
    }
}

// 1. Tạo giao diện quản lý người thuê
private void deleteTenant(TenantEntry tenant) {
    try (Connection conn = DatabaseManager.connect()) {
        // Lấy room_id từ tên phòng
        int roomId = -1;
        try (PreparedStatement pstmt = conn.prepareStatement(
                "SELECT id FROM rooms WHERE name = ?")) {
            pstmt.setString(1, tenant.getRoom());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                roomId = rs.getInt("id");
            }
        }
        // Xóa tenant
        try (PreparedStatement pstmt = conn.prepareStatement(
                "DELETE FROM tenants WHERE name = ? AND phone = ? AND address = ?")) {
            pstmt.setString(1, tenant.getName());
            pstmt.setString(2, tenant.getPhone());
            pstmt.setString(3, tenant.getAddress());
            pstmt.executeUpdate();
        }
        // Cập nhật trạng thái phòng về "Trống"
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
    tenantList.setAll(loadTenantData());
    roomList.setAll(loadRoomData());
}
private Pane getTenantManagementPaneStyled() {
    Pane pane = getTenantManagementPane();
    if (pane instanceof Region region) {
        region.setStyle("-fx-background-color: #fff; -fx-background-radius: 18; -fx-padding: 24;");
    }
    return pane;
}
private Pane getTenantManagementPane() {
    VBox tenantPane = new VBox(10);
    tenantPane.setPadding(new Insets(10));

    HBox formBox = new HBox(10);

    // ComboBox chỉ hiển thị phòng "Trống"
    ComboBox<RoomEntry> cbRoom = new ComboBox<>();
    cbRoom.setPromptText("Chọn phòng");
    Runnable updateRoomChoices = () -> {
        cbRoom.getItems().setAll(
            roomList.stream()
                .filter(room -> "Trống".equals(room.getStatus()))
                .toList()
        );
    };
    
    updateRoomChoices.run();

    TextField txtName = new TextField();
    txtName.setPromptText("Tên người thuê");
    TextField txtPhone = new TextField();
    txtPhone.setPromptText("SĐT");
    TextField txtAddress = new TextField();
    txtAddress.setPromptText("Địa chỉ");
    Button btnAdd = new Button("Thêm");
    Label lblStatus = new Label();
    formBox.getChildren().addAll(cbRoom, txtName, txtPhone, txtAddress, btnAdd, lblStatus);

    TableView<TenantEntry> table = new TableView<>();
    table.setPrefHeight(300);
    table.setPrefWidth(600);

    TableColumn<TenantEntry, String> colName = new TableColumn<>("Tên");
colName.setCellValueFactory(new PropertyValueFactory<>("name"));
colName.setPrefWidth(180);

TableColumn<TenantEntry, String> colPhone = new TableColumn<>("SĐT");
colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
colPhone.setPrefWidth(120);

TableColumn<TenantEntry, String> colAddress = new TableColumn<>("Địa chỉ");
colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
colAddress.setPrefWidth(180);

TableColumn<TenantEntry, String> colRoom = new TableColumn<>("Phòng");
colRoom.setCellValueFactory(new PropertyValueFactory<>("room"));
colRoom.setPrefWidth(100);

TableColumn<TenantEntry, Void> colDelete = new TableColumn<>("Xóa");
colDelete.setPrefWidth(60);
colDelete.setCellFactory(param -> new TableCell<>() {
    private final Button btnDelete = new Button("Xóa");
    {
        btnDelete.setOnAction(event -> {
            TenantEntry tenant = getTableView().getItems().get(getIndex());
            deleteTenant(tenant);
            tenantList.setAll(loadTenantData());
            roomList.setAll(loadRoomData());
            updateRoomChoices.run();
        });
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(empty ? null : btnDelete);
    }
});

// Thêm tất cả các cột vào TableView sau khi đã khai báo xong
table.getColumns().setAll(colName, colPhone, colAddress, colRoom, colDelete);
    tenantList.setAll(loadTenantData());
    table.setItems(tenantList);

    btnAdd.setOnAction(e -> {
        RoomEntry selectedRoom = cbRoom.getValue();
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();

        if (selectedRoom == null || name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            lblStatus.setText("Vui lòng nhập đầy đủ thông tin.");
        } else {
            // Lấy room_id từ tên phòng
            int roomId = -1;
            try (Connection conn = DatabaseManager.connect();
                 PreparedStatement pstmt = conn.prepareStatement("SELECT id FROM rooms WHERE name = ? LIMIT 1")) {
                pstmt.setString(1, selectedRoom.getName());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    roomId = rs.getInt("id");
                }
            } catch (Exception ex) {
                lblStatus.setText("Lỗi khi lấy ID phòng!");
                return;
            }
            if (roomId == -1) {
                lblStatus.setText("Không tìm thấy phòng!");
                return;
            }
            addTenant(roomId, name, phone, address);

            // Cập nhật trạng thái phòng sang "Cho thuê"
            try (Connection conn = DatabaseManager.connect();
                 PreparedStatement pstmt = conn.prepareStatement("UPDATE rooms SET status = 'Cho thuê' WHERE id = ?")) {
                pstmt.setInt(1, roomId);
                pstmt.executeUpdate();
            } catch (Exception ex) {
                lblStatus.setText("Lỗi khi cập nhật trạng thái phòng!");
            }

            lblStatus.setText("Đã thêm: " + name);
            cbRoom.setValue(null);
            txtName.clear();
            txtPhone.clear();
            txtAddress.clear();
            tenantList.setAll(loadTenantData());
            roomList.setAll(loadRoomData());
            updateRoomChoices.run();
        }
    });

    roomList.addListener((javafx.collections.ListChangeListener<RoomEntry>) change -> updateRoomChoices.run());

    tenantPane.getChildren().clear();
    tenantPane.getChildren().addAll(new Label("Quản Lý Người Thuê"), formBox, table);
    return tenantPane;
}

// 2. Đặt phần giao diện chính showDashboard như sau:
private void showDashboard(String adminUsername) {
    primaryStage.setTitle("Dashboard - " + adminUsername);

    BorderPane bp = new BorderPane();
    bp.setPadding(new Insets(0));

    // Sidebar hiện đại
    VBox sidebar = new VBox(18);
    sidebar.setPadding(new Insets(32, 0, 32, 0));
    sidebar.setStyle(
        "-fx-background-color: linear-gradient(to bottom, #43cea2, #185a9d);" +
        "-fx-background-radius: 24 0 0 24;" +
        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 16, 0, 2, 2);"
    );
    sidebar.setPrefWidth(240);

    // Nút menu sidebar
    Button btnTenant = createSidebarButton("👤", "Quản Lý Người Thuê", true);
    Button btnRoom = createSidebarButton("🏠", "Quản Lý Phòng", false);
    Button btnBill = createSidebarButton("💵", "Tạo Hóa Đơn", false);
    Button btnNotify = createSidebarButton("🔔", "Gửi Thông Báo", false);
    Button btnLogout = createSidebarButton("⏻", "Đăng Xuất", false);
    btnLogout.setStyle(btnLogout.getStyle() + "-fx-background-color: #f7971e; -fx-text-fill: white;");

    sidebar.getChildren().addAll(btnTenant, btnRoom, btnBill, btnNotify, btnLogout);
    bp.setLeft(sidebar);

    // Main content area
    StackPane centerPane = new StackPane();
    centerPane.setStyle("-fx-background-color: #f7fafd; -fx-background-radius: 0 24 24 0;");
    bp.setCenter(centerPane);

    // Sự kiện sidebar
    btnTenant.setOnAction(e -> {
        setSidebarActive(sidebar, btnTenant);
        bp.setCenter(getTenantManagementPaneStyled());
    });
    btnRoom.setOnAction(e -> {
        setSidebarActive(sidebar, btnRoom);
        bp.setCenter(getRoomManagementPaneStyled());
    });
    btnBill.setOnAction(e -> {
        setSidebarActive(sidebar, btnBill);
        bp.setCenter(getBillManagementPaneStyled());
    });
    btnNotify.setOnAction(e -> {
        setSidebarActive(sidebar, btnNotify);
        bp.setCenter(getNotifyPaneStyled());
    });
    btnLogout.setOnAction(e -> showAuthPane());

    // Hiển thị mặc định là quản lý người thuê
    bp.setCenter(getTenantManagementPaneStyled());
    Scene scene = new Scene(bp, 1100, 700);
    primaryStage.setScene(scene);
    primaryStage.show();
}

// Tạo nút sidebar có icon, active là nút đang chọn
private Button createSidebarButton(String icon, String text, boolean active) {
    Label iconLabel = new Label(icon);
    iconLabel.setStyle("-fx-font-size: 18px; -fx-padding: 0 8 0 0;");
    Label textLabel = new Label(text);
    textLabel.setStyle("-fx-text-fill: #2193b0; -fx-font-size: 16px; -fx-font-weight: bold;");
    HBox box = new HBox(10, iconLabel, textLabel);
    box.setAlignment(Pos.CENTER_LEFT);
    Button btn = new Button();
    btn.setGraphic(box);
    btn.setPrefWidth(200);
    btn.setPrefHeight(54);
    btn.setStyle(
        (active ?
            "-fx-background-color: #fff; -fx-text-fill: #2193b0;" :
            "-fx-background-color: #fff8; -fx-text-fill: #fff;"
        ) +
        "-fx-background-radius: 18;" +
        "-fx-padding: 0 0 0 24;" +
        "-fx-cursor: hand;" +
        "-fx-font-size: 16px;" +
        "-fx-font-weight: bold;" +
        "-fx-effect: dropshadow(gaussian, rgba(33,147,176,0.08), 6, 0, 2, 2);"
    );
    btn.setOnMouseEntered(e -> btn.setStyle(
        "-fx-background-color: #fff; -fx-text-fill: #2193b0;" +
        "-fx-background-radius: 18;" +
        "-fx-padding: 0 0 0 24;" +
        "-fx-cursor: hand;" +
        "-fx-font-size: 16px;" +
        "-fx-font-weight: bold;" +
        "-fx-effect: dropshadow(gaussian, rgba(33,147,176,0.18), 8, 0, 2, 2);"
    ));
    btn.setOnMouseExited(e -> btn.setStyle(
        (active ?
            "-fx-background-color: #fff; -fx-text-fill: #2193b0;" :
            "-fx-background-color: #fff8; -fx-text-fill: #fff;"
        ) +
        "-fx-background-radius: 18;" +
        "-fx-padding: 0 0 0 24;" +
        "-fx-cursor: hand;" +
        "-fx-font-size: 16px;" +
        "-fx-font-weight: bold;" +
        "-fx-effect: dropshadow(gaussian, rgba(33,147,176,0.08), 6, 0, 2, 2);"
    ));
    return btn;
}

// Đặt lại trạng thái active cho sidebar
private void setSidebarActive(VBox sidebar, Button activeBtn) {
    for (Node node : sidebar.getChildren()) {
        if (node instanceof Button btn) {
            if (btn == activeBtn) {
                btn.setStyle(
                    "-fx-background-color: #fff; -fx-text-fill: #2193b0;" +
                    "-fx-background-radius: 18;" +
                    "-fx-padding: 0 0 0 24;" +
                    "-fx-cursor: hand;" +
                    "-fx-font-size: 16px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-effect: dropshadow(gaussian, rgba(33,147,176,0.18), 8, 0, 2, 2);"
                );
            } else if (!btn.getText().contains("Đăng Xuất")) {
                btn.setStyle(
                    "-fx-background-color: #fff8; -fx-text-fill: #fff;" +
                    "-fx-background-radius: 18;" +
                    "-fx-padding: 0 0 0 24;" +
                    "-fx-cursor: hand;" +
                    "-fx-font-size: 16px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-effect: dropshadow(gaussian, rgba(33,147,176,0.08), 6, 0, 2, 2);"
                );
            }
        }
    }
}


private List<String> getTenantNames() {
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


// Nạp danh sách hóa đơn từ CSDL
private void loadBills() {
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
}

// Tạo hóa đơn (style đẹp, không lỗi)
private Pane getBillManagementPaneStyled() {
    VBox billPane = new VBox(18);
    billPane.setPadding(new Insets(32, 32, 32, 32));
    billPane.setStyle("-fx-background-color: #fff; -fx-background-radius: 18;");

    Label lblTitle = new Label("Tạo Hóa Đơn");
    lblTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-padding: 0 0 12 0;");

    List<String> tenantNames = getTenantNames();
    ComboBox<String> cbTenant = new ComboBox<>(FXCollections.observableArrayList(tenantNames));
    cbTenant.setPromptText("Chọn người thuê");
    cbTenant.setStyle("-fx-background-radius: 12; -fx-padding: 8;");

    TextField txtAmount = new TextField();
    txtAmount.setPromptText("Số tiền");
    txtAmount.setStyle("-fx-background-radius: 12; -fx-padding: 8;");

    TextField txtDesc = new TextField();
    txtDesc.setPromptText("Mô tả");
    txtDesc.setStyle("-fx-background-radius: 12; -fx-padding: 8;");

    Button btnAddBill = new Button("➕ Thêm hóa đơn");
    btnAddBill.setStyle(
        "-fx-background-color: linear-gradient(to right, #43e97b, #38f9d7);" +
        "-fx-text-fill: #fff; -fx-font-weight: bold; -fx-background-radius: 18; -fx-padding: 10 24 10 24; -fx-font-size: 16px;"
    );
    Label lblBillStatus = new Label();

    // TableView styled
    TableView<BillEntry> billTable = new TableView<>(billList);
    billTable.setPrefHeight(220);
    billTable.setStyle("-fx-background-radius: 12; -fx-background-color: #f8fafc; -fx-padding: 8;");

    TableColumn<BillEntry, String> colTenant = new TableColumn<>("Người thuê");
    colTenant.setCellValueFactory(new PropertyValueFactory<>("tenantName"));
    colTenant.setPrefWidth(150);

    TableColumn<BillEntry, Double> colAmount = new TableColumn<>("Số tiền");
    colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
    colAmount.setPrefWidth(100);

    TableColumn<BillEntry, String> colDesc = new TableColumn<>("Mô tả");
    colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
    colDesc.setPrefWidth(200);

    TableColumn<BillEntry, Void> colDelete = new TableColumn<>("Xóa");
    colDelete.setPrefWidth(60);
    colDelete.setCellFactory(param -> new TableCell<>() {
        private final Button btnDelete = new Button("🗑");

        {
            btnDelete.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand;");
            btnDelete.setOnAction(event -> {
                BillEntry bill = getTableView().getItems().get(getIndex());
                deleteBill(bill); 
                loadBills();  
            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(empty ? null : btnDelete);
        }
    });

    billTable.getColumns().setAll(colTenant, colAmount, colDesc, colDelete);

    // Nạp danh sách hóa đơn từ CSDL
    loadBills();

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
                    loadBills();
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

    billPane.getChildren().addAll(lblTitle, cbTenant, txtAmount, txtDesc, btnAddBill, lblBillStatus, billTable);
    return billPane;
}
// Gửi thông báo (style đẹp, không lỗi)
private Pane getNotifyPaneStyled() {
    VBox notifyPane = new VBox(18);
    notifyPane.setPadding(new Insets(32, 32, 32, 32));
    notifyPane.setStyle("-fx-background-color: #fff; -fx-background-radius: 18;");

    Label lblTitle = new Label("Gửi Thông Báo");
    lblTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-padding: 0 0 12 0;");

    TextField txtTenantName = new TextField();
    txtTenantName.setPromptText("Tên người thuê");
    txtTenantName.setStyle("-fx-background-radius: 12; -fx-padding: 8;");

    TextField txtMessage = new TextField();
    txtMessage.setPromptText("Nội dung thông báo");
    txtMessage.setStyle("-fx-background-radius: 12; -fx-padding: 8;");

    Button btnSend = new Button("📧 Gửi thông báo");
    btnSend.setStyle(
        "-fx-background-color: linear-gradient(to right, #43e97b, #38f9d7);" +
        "-fx-text-fill: #fff; -fx-font-weight: bold; -fx-background-radius: 18; -fx-padding: 10 24 10 24; -fx-font-size: 16px;"
    );
    Label lblNotifyStatus = new Label();

    btnSend.setOnAction(ev -> {
        String tenantName = txtTenantName.getText().trim();
        String message = txtMessage.getText().trim();
        if (tenantName.isEmpty() || message.isEmpty()) {
            lblNotifyStatus.setText("Vui lòng nhập đủ tên người thuê và nội dung.");
            return;
        }
        try (Connection conn = DatabaseManager.connect()) {
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

    notifyPane.getChildren().addAll(lblTitle, txtTenantName, txtMessage, btnSend, lblNotifyStatus);
    return notifyPane;
}

// Style cho TableView trong các Pane
private void styleTableViewsInPane(Pane pane) {
    for (Node node : pane.lookupAll(".table-view")) {
        if (node instanceof TableView) {
            TableView<?> table = (TableView<?>) node;
            table.setStyle(
                "-fx-background-radius: 12;" +
                "-fx-background-color: #f8fafc;" +
                "-fx-effect: dropshadow(gaussian, rgba(33,147,176,0.08), 8, 0, 2, 2);" +
                "-fx-padding: 8;"
            );
            table.lookupAll(".column-header-background").forEach(header -> header.setStyle(
                "-fx-background-color: linear-gradient(to right, #43cea2, #185a9d);" +
                "-fx-background-radius: 12 12 0 0;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;"
            ));
        }
    }
}

    // 5. Các lớp mô hình (model)
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
    private final SimpleStringProperty room;

    public TenantEntry(String name, String phone, String address, String room) {
        this.name = new SimpleStringProperty(name);
        this.phone = new SimpleStringProperty(phone);
        this.address = new SimpleStringProperty(address);
        this.room = new SimpleStringProperty(room);
    }

    public String getName() { return name.get(); }
    public String getPhone() { return phone.get(); }
    public String getAddress() { return address.get(); }
    public String getRoom() { return room.get(); }
}

public static class RoomEntry {
    private final SimpleStringProperty name;
    private final SimpleStringProperty size;
    private final SimpleStringProperty type;
    private final SimpleStringProperty status;

    public RoomEntry(String name, String size, String type, String status) {
        this.name = new SimpleStringProperty(name);
        this.size = new SimpleStringProperty(size);
        this.type = new SimpleStringProperty(type);
        this.status = new SimpleStringProperty(status);
    }

    public String getName() { return name.get(); }
    public String getSize() { return size.get(); }
    public String getType() { return type.get(); }
    public String getStatus() { return status.get(); }
    public void setStatus(String status) { this.status.set(status); } // Thêm setter nếu cần

    @Override
    public String toString() {
        return getName(); // Để ComboBox hiển thị tên phòng
    }
}

    
public static class BillEntry {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty tenantName;
    private final SimpleDoubleProperty amount;
    private final SimpleStringProperty description;
    private String status;// Đảm bảo có dòng này

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
    public void setStatus(String status) { this.status = status; } // Thêm dòng này


}

    // 6.  start và main
   
@Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showAuthPane();
    }
public static void main(String[] args) {
        launch(args);
    }}*/


    package com.roommanagement.auth;

import com.roommanagement.database.DatabaseManager;
import com.roommanagement.auth.AdminModel.*;
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
    public List<BillEntry> loadBills() {
        List<BillEntry> bills = new ArrayList<>();
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT b.id, b.amount, b.description, t.name as tenantName " +
                             "FROM bills b JOIN tenants t ON b.tenant_id = t.id")) {
            while (rs.next()) {
                bills.add(new BillEntry(
                        rs.getInt("id"),
                        rs.getString("tenantName"),
                        rs.getDouble("amount"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return bills;
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
}
    