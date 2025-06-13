package com.roommanagement.auth;

import com.roommanagement.auth.AdminModel.*;
import com.roommanagement.database.DatabaseManager;
import com.roommanagement.notification.InvoiceService;
import com.roommanagement.notification.NotificationService;
import java.util.Map;
import javafx.application.Application;
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
import com.roommanagement.auth.AdminService.BillEntry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
 

public class AdminView extends Application {
    private final NotificationService notificationService = new NotificationService();
    private final AdminService service = new AdminService();
    private Stage primaryStage;

    private ObservableList<TenantEntry> tenantList = FXCollections.observableArrayList();
    private ObservableList<RoomEntry> roomList = FXCollections.observableArrayList();
    private ObservableList<BillEntry> billList = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showAuthPane();
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

        Label iconEmail = new Label("\uD83D\uDCE7");
        Label iconPhone = new Label("\uD83D\uDCDE");
        Label iconUser = new Label("\uD83D\uDC64");
        Label iconPass = new Label("\uD83D\uDD12");
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
                service.registerAdmin(email, phone, username, password, lblRegStatus);
                txtEmail.clear();
                txtPhone.clear();
                txtUsername.clear();
                txtPassword.clear();
            }
        });

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

        Label iconUserLogin = new Label("\uD83D\uDC64");
        Label iconPassLogin = new Label("\uD83D\uDD12");
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
                if(service.loginAdmin(username, password)) {
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

    private void showDashboard(String adminUsername) {
        primaryStage.setTitle("Dashboard - " + adminUsername);

        BorderPane bp = new BorderPane();
        bp.setPadding(new Insets(0));

        VBox sidebar = new VBox(18);
        sidebar.setPadding(new Insets(32, 0, 32, 0));
        sidebar.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #43cea2, #185a9d);" +
            "-fx-background-radius: 24 0 0 24;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 16, 0, 2, 2);"
        );
        sidebar.setPrefWidth(240);

        Button btnTenant = createSidebarButton("👤", "Quản Lý Người Thuê", true);
        Button btnRoom = createSidebarButton("🏠", "Quản Lý Phòng", false);
        Button btnBill = createSidebarButton("💵", "Tạo Hóa Đơn", false);
        Button btnNotify = createSidebarButton("🔔", "Gửi Thông Báo", false);
        Button btnLogout = createSidebarButton("⏻", "Đăng Xuất", false);
        btnLogout.setStyle(btnLogout.getStyle() + "-fx-background-color: #f7971e; -fx-text-fill: white;");

        sidebar.getChildren().addAll(btnTenant, btnRoom, btnBill, btnNotify, btnLogout);
        bp.setLeft(sidebar);

        StackPane centerPane = new StackPane();
        centerPane.setStyle("-fx-background-color: #f7fafd; -fx-background-radius: 0 24 24 0;");
        bp.setCenter(centerPane);

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

        bp.setCenter(getTenantManagementPaneStyled());
        Scene scene = new Scene(bp, 1100, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

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
// --- Quản lý hóa đơn ---
    private Pane getRoomManagementPaneStyled() {
    Pane pane = getRoomManagementPane(); 
    if (pane instanceof Region region) {
        region.setStyle("-fx-background-color: #fff; -fx-background-radius: 18; -fx-padding: 24;");
    }
    Pane roomPane = getRoomManagementPane();
styleTableViewsInPane(roomPane);
    return pane;
}
private Pane getRoomManagementPane() {
    roomList.setAll(service.loadRoomData());
    VBox pane = new VBox(18);
    pane.setPadding(new Insets(32, 32, 32, 32));
    pane.setStyle("-fx-background-color: #fff; -fx-background-radius: 18;");

    Label lblTitle = new Label("Quản Lý Phòng");
    lblTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-padding: 0 0 12 0;");

    TableView<RoomEntry> roomTable = new TableView<>(roomList);
    roomTable.setPrefHeight(220);
    roomTable.setMaxHeight(Double.MAX_VALUE);
    VBox.setVgrow(roomTable, Priority.ALWAYS);

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
                // Nếu có chức năng xóa phòng, gọi hàm xóa ở đây
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
                service.updateRoomStatus(room.getName(), newStatus);
                roomList.setAll(service.loadRoomData());
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

    roomTable.getColumns().addAll(List.of(colRoomName, colSize, colType, colStatus, colDelete));

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
        service.addRoom(name, size, type, status);
        roomList.setAll(service.loadRoomData());
        lblStatus.setText("Đã thêm phòng mới!");
        txtRoomName.clear();
        txtSize.clear();
        cbType.setValue(null);
        cbStatus.setValue(null);
    });

    HBox inputBox = new HBox(12, txtRoomName, txtSize, cbType, cbStatus, btnAddRoom);
    inputBox.setAlignment(Pos.CENTER_LEFT);

    pane.getChildren().setAll(lblTitle, roomTable, inputBox, lblStatus);
    return pane;
}

// --- Quản lý hóa đơn ---
    private Pane getTenantManagementPaneStyled() {
    Pane pane = getTenantManagementPane(); 
    if (pane instanceof Region region) {
        region.setStyle("-fx-background-color: #fff; -fx-background-radius: 18; -fx-padding: 24;");
    }
    Pane tenantPane = getTenantManagementPane();
styleTableViewsInPane(tenantPane);

    return pane;
}
private Pane getTenantManagementPane() {
    VBox tenantPane = new VBox(18);
    tenantPane.setPadding(new Insets(32, 32, 32, 32));
    tenantPane.setStyle("-fx-background-color: #fff; -fx-background-radius: 18;");

    Label lblTitle = new Label("Quản Lý Người Thuê");
    lblTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-padding: 0 0 12 0;");

    // Form nhập liệu
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
    btnAdd.setStyle(
        "-fx-background-color: linear-gradient(to right, #43e97b, #38f9d7);" +
        "-fx-text-fill: #fff; -fx-font-weight: bold; -fx-background-radius: 18; -fx-padding: 10 24 10 24; -fx-font-size: 16px;"
    );
    Label lblStatus = new Label();
    HBox formBox = new HBox(10, cbRoom, txtName, txtPhone, txtAddress, btnAdd, lblStatus);

    // TableView
    TableView<TenantEntry> table = new TableView<>(tenantList);
    table.setPrefHeight(220);
    table.setMaxHeight(Double.MAX_VALUE);
    VBox.setVgrow(table, Priority.ALWAYS);

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
            btnDelete.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand;");
            btnDelete.setOnAction(event -> {
                TenantEntry tenant = getTableView().getItems().get(getIndex());
                service.deleteTenant(tenant);
                tenantList.setAll(service.loadTenantData());
                roomList.setAll(service.loadRoomData());
                updateRoomChoices.run();
            });
        }
        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(empty ? null : btnDelete);
        }
    });

    table.getColumns().setAll(List.of(colName, colPhone, colAddress, colRoom, colDelete));

    tenantList.setAll(service.loadTenantData());
    table.setItems(tenantList);

    btnAdd.setOnAction(e -> {
        RoomEntry selectedRoom = cbRoom.getValue();
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();

        if (selectedRoom == null || name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            lblStatus.setText("Vui lòng nhập đầy đủ thông tin.");
        } else {
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
            service.addTenant(roomId, name, phone, address);
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
            tenantList.setAll(service.loadTenantData());
            roomList.setAll(service.loadRoomData());
            updateRoomChoices.run();
        }
    });

    roomList.addListener((javafx.collections.ListChangeListener<RoomEntry>) change -> updateRoomChoices.run());

    tenantPane.getChildren().setAll(lblTitle, formBox, table);
    return tenantPane;
}

// --- Quản lý hóa đơn ---
private Pane getBillManagementPaneStyled() {
    VBox billPane = new VBox(18);
    billPane.setPadding(new Insets(32, 32, 32, 32));
    billPane.setStyle("-fx-background-color: #fff; -fx-background-radius: 18;");

    Label lblTitle = new Label("Tạo Hóa Đơn");
    lblTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-padding: 0 0 12 0;");

    List<String> tenantNames = service.getTenantNames();
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
    billTable.setMinHeight(120);
    billTable.setMaxHeight(Double.MAX_VALUE); 
    VBox.setVgrow(billTable, Priority.ALWAYS); 

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
                service.deleteBill(bill);
                billList.setAll(service.loadBills()); // Luôn load lại từ DB sau khi xóa
            });
        }
        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(empty ? null : btnDelete);
        }
    });
    
    // Luôn load lại dữ liệu từ DB khi khởi tạo
    billList.setAll(service.loadBills());

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
                    billList.setAll(service.loadBills()); 
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
    VBox.setVgrow(billTable, Priority.ALWAYS); 
    return billPane;
}


    // --- Gửi thông báo ---
    /*private Pane getNotifyPaneStyled() {
    VBox notifyPane = new VBox(18);
    notifyPane.setPadding(new Insets(32, 32, 32, 32));
    notifyPane.setStyle("-fx-background-color: #fff; -fx-background-radius: 18;");

    Label lblTitle = new Label("Gửi Hóa Đơn & Thông Báo");
    lblTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-padding: 0 0 12 0;");

    // --- Form nhập thông tin hóa đơn ---
    List<String> tenantNames = service.getTenantNames();
    ComboBox<String> cbTenant = new ComboBox<>(FXCollections.observableArrayList(tenantNames));
    cbTenant.setPromptText("Chọn người thuê");
    cbTenant.setStyle("-fx-background-radius: 12; -fx-padding: 8;");

    TextField txtRoom = new TextField();
    txtRoom.setPromptText("Phòng");
    txtRoom.setStyle("-fx-background-radius: 12; -fx-padding: 8;");

    TextField txtPhone = new TextField();
    txtPhone.setPromptText("SĐT");
    txtPhone.setStyle("-fx-background-radius: 12; -fx-padding: 8;");

    TextField txtAddress = new TextField();
    txtAddress.setPromptText("Địa chỉ thuê");
    txtAddress.setStyle("-fx-background-radius: 12; -fx-padding: 8;");

    TextField txtDesc = new TextField();
    txtDesc.setPromptText("Mô tả dịch vụ/hàng hóa");
    txtDesc.setStyle("-fx-background-radius: 12; -fx-padding: 8;");

    TextField txtQty = new TextField();
    txtQty.setPromptText("Số lượng");
    txtQty.setStyle("-fx-background-radius: 12; -fx-padding: 8;");

    TextField txtUnitPrice = new TextField();
    txtUnitPrice.setPromptText("Đơn giá");
    txtUnitPrice.setStyle("-fx-background-radius: 12; -fx-padding: 8;");

    TextField txtTotal = new TextField();
    txtTotal.setPromptText("Tổng tiền");
    txtTotal.setStyle("-fx-background-radius: 12; -fx-padding: 8;");

    // Tự động tính tổng tiền khi nhập số lượng và đơn giá
    txtQty.textProperty().addListener((obs, oldVal, newVal) -> {
        try {
            int qty = Integer.parseInt(newVal);
            double price = Double.parseDouble(txtUnitPrice.getText());
            txtTotal.setText(String.valueOf(qty * price));
        } catch (Exception ex) {
            txtTotal.setText("");
        }
    });
    txtUnitPrice.textProperty().addListener((obs, oldVal, newVal) -> {
        try {
            int qty = Integer.parseInt(txtQty.getText());
            double price = Double.parseDouble(newVal);
            txtTotal.setText(String.valueOf(qty * price));
        } catch (Exception ex) {
            txtTotal.setText("");
        }
    });

    // --- Form gửi thông báo ---
    TextField txtMessage = new TextField();
    txtMessage.setPromptText("Nội dung thông báo gửi kèm hóa đơn");
    txtMessage.setStyle("-fx-background-radius: 12; -fx-padding: 8;");

    Button btnSendInvoice = new Button("📧 Gửi hóa đơn & thông báo");
    btnSendInvoice.setStyle(
        "-fx-background-color: linear-gradient(to right, #43e97b, #38f9d7);" +
        "-fx-text-fill: #fff; -fx-font-weight: bold; -fx-background-radius: 18; -fx-padding: 10 24 10 24; -fx-font-size: 16px;"
    );
    Label lblNotifyStatus = new Label();

    // --- Sự kiện gửi hóa đơn & thông báo ---
    btnSendInvoice.setOnAction(ev -> {
        String tenant = cbTenant.getValue();
        String room = txtRoom.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();
        String desc = txtDesc.getText().trim();
        String qty = txtQty.getText().trim();
        String unitPrice = txtUnitPrice.getText().trim();
        String total = txtTotal.getText().trim();
        String message = txtMessage.getText().trim();

        if (tenant == null || tenant.isEmpty() || room.isEmpty() || phone.isEmpty() || address.isEmpty()
                || desc.isEmpty() || qty.isEmpty() || unitPrice.isEmpty() || total.isEmpty() || message.isEmpty()) {
            lblNotifyStatus.setText("Vui lòng nhập đầy đủ thông tin hóa đơn và thông báo!");
            return;
        }
        try {
            // 1. Xuất file hóa đơn PDF
            String filePath = "hoadon_" + tenant + "_" + System.currentTimeMillis() + ".pdf";
            exportInvoiceToPDF(filePath, tenant, room, phone, address, desc, qty, unitPrice, total);

            // 2. Lưu thông báo vào database (có thể lưu đường dẫn file hóa đơn nếu muốn)
            try (Connection conn = DatabaseManager.connect()) {
                String sql = "INSERT INTO notifications (tenant_name, message) VALUES (?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, tenant);
                pstmt.setString(2, message + " [File hóa đơn: " + filePath + "]");
                pstmt.executeUpdate();
            }

            lblNotifyStatus.setText("Đã gửi hóa đơn và thông báo cho " + tenant + ". File: " + filePath);
            java.awt.Desktop.getDesktop().open(new java.io.File(filePath));

            // Xóa trắng form
            cbTenant.setValue(null);
            txtRoom.clear();
            txtPhone.clear();
            txtAddress.clear();
            txtDesc.clear();
            txtQty.clear();
            txtUnitPrice.clear();
            txtTotal.clear();
            txtMessage.clear();
        } catch (Exception ex) {
            lblNotifyStatus.setText("Lỗi khi gửi hóa đơn/thông báo: " + ex.getMessage());
            ex.printStackTrace();
        }
    });

    // --- Sắp xếp giao diện ---
    GridPane form = new GridPane();
    form.setHgap(18);
    form.setVgap(14);
    form.setPadding(new Insets(18, 18, 18, 18));
    form.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 12;");

    form.add(new Label("Người thuê:"), 0, 0); form.add(cbTenant, 1, 0);
    form.add(new Label("Phòng:"), 0, 1); form.add(txtRoom, 1, 1);
    form.add(new Label("SĐT:"), 0, 2); form.add(txtPhone, 1, 2);
    form.add(new Label("Địa chỉ thuê:"), 0, 3); form.add(txtAddress, 1, 3);
    form.add(new Label("Mô tả:"), 0, 4); form.add(txtDesc, 1, 4);
    form.add(new Label("Số lượng:"), 0, 5); form.add(txtQty, 1, 5);
    form.add(new Label("Đơn giá:"), 0, 6); form.add(txtUnitPrice, 1, 6);
    form.add(new Label("Tổng tiền:"), 0, 7); form.add(txtTotal, 1, 7);
    form.add(new Label("Nội dung thông báo:"), 0, 8); form.add(txtMessage, 1, 8);

    notifyPane.getChildren().setAll(lblTitle, form, btnSendInvoice, lblNotifyStatus);
    return notifyPane;
}

// Hàm xuất hóa đơn PDF (dùng iText, cần thêm thư viện iText vào project)
private void exportInvoiceToPDF(String filePath, String tenant, String room, String phone, String address, String desc, String qty, String unitPrice, String total) throws Exception {
    com.itextpdf.text.Document document = new com.itextpdf.text.Document();
    com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(filePath));
    document.open();

    com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 20, com.itextpdf.text.Font.BOLD);
    com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("HÓA ĐƠN", titleFont);
    title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
    document.add(title);

    document.add(new com.itextpdf.text.Paragraph("Ngày: " + java.time.LocalDate.now()));
    document.add(new com.itextpdf.text.Paragraph("Người thuê: " + tenant));
    document.add(new com.itextpdf.text.Paragraph("Phòng: " + room));
    document.add(new com.itextpdf.text.Paragraph("SĐT: " + phone));
    document.add(new com.itextpdf.text.Paragraph("Địa chỉ thuê: " + address));
    document.add(new com.itextpdf.text.Paragraph(" "));

    com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(4);
    table.addCell("QTY");
    table.addCell("DESCRIPTION");
    table.addCell("UNIT PRICE");
    table.addCell("LINE TOTAL");
    table.addCell(qty);
    table.addCell(desc);
    table.addCell(unitPrice);
    table.addCell(total);

    document.add(table);
    document.add(new com.itextpdf.text.Paragraph("TỔNG: " + total));
    document.close();
}*/

// --- Gửi thông báo ---
private List<String> getTenantNamesFromDB() {
    List<String> tenantNames = new java.util.ArrayList<>();
    try (java.sql.Connection conn = com.roommanagement.database.DatabaseManager.connect();
         java.sql.Statement stmt = conn.createStatement();
         java.sql.ResultSet rs = stmt.executeQuery("SELECT name FROM tenants")) {
        while (rs.next()) {
            tenantNames.add(rs.getString("name"));
        }
    } catch (java.sql.SQLException ex) {
        ex.printStackTrace();
    }
    return tenantNames;
}
    public Pane getNotifyPaneStyled() {
        VBox notifyPane = new VBox(24);
        notifyPane.setPadding(new Insets(32));
        notifyPane.setStyle("-fx-background-color: #fff; -fx-background-radius: 18;");

        Label lblTitle = new Label("🔔 GỬI THÔNG BÁO");
        lblTitle.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #185a9d; -fx-padding: 0 0 18 0;");

        List<String> tenantNames = getTenantNamesFromDB();
        ComboBox<String> cbTenant = new ComboBox<>(FXCollections.observableArrayList(tenantNames));
        cbTenant.setPromptText("Chọn người thuê");
        cbTenant.setStyle("-fx-background-radius: 10; -fx-padding: 8;");

        TextField txtPhone = new TextField(); txtPhone.setPromptText("SĐT"); txtPhone.setEditable(false);
        TextField txtAddress = new TextField(); txtAddress.setPromptText("Địa chỉ thuê"); txtAddress.setEditable(false);

        TextField txtMessage = new TextField(); txtMessage.setPromptText("Nội dung thông báo");

        // Khi chọn người thuê thì tự động điền thông tin
        cbTenant.setOnAction(ev -> {
            String tenant = cbTenant.getValue();
            Map<String, String> info = notificationService.getTenantInfo(tenant);
            txtPhone.setText(info.getOrDefault("phone", ""));
            txtAddress.setText(info.getOrDefault("address", ""));
        });

        Button btnSend = new Button("📧 Gửi thông báo");
        btnSend.setStyle("-fx-background-color: #6a5af9; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 18; -fx-font-size: 16px; -fx-padding: 10 24 10 24;");
        Label lblStatus = new Label();

        btnSend.setOnAction(ev -> {
            String tenant = cbTenant.getValue();
            String message = txtMessage.getText();
            if (tenant == null || tenant.isEmpty() || message.isEmpty()) {
                lblStatus.setText("Vui lòng chọn người thuê và nhập nội dung.");
                return;
            }
            boolean ok = notificationService.sendNotification(tenant, message);
            if (ok) {
                lblStatus.setText("Đã gửi thông báo cho " + tenant);
                cbTenant.setValue(null);
                txtPhone.clear();
                txtAddress.clear();
                txtMessage.clear();
            } else {
                lblStatus.setText("Lỗi khi gửi thông báo!");
            }
        });

        GridPane form = new GridPane();
        form.setHgap(14); form.setVgap(14);
        form.add(new Label("👤 Người thuê:"), 0, 0); form.add(cbTenant, 1, 0);
        form.add(new Label("📞 SĐT:"), 0, 1); form.add(txtPhone, 1, 1);
        form.add(new Label("📍 Địa chỉ:"), 0, 2); form.add(txtAddress, 1, 2);
        form.add(new Label("✉️ Nội dung:"), 0, 3); form.add(txtMessage, 1, 3);

        VBox vbox = new VBox(18, lblTitle, form, btnSend, lblStatus);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setPadding(new Insets(24));
        vbox.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 18;");

        return vbox;
    }

    // Lấy danh sách người thuê từ DB (hoặc service)
    private List<String> getTenantNamesDBFromDB() {
        
        return service.getTenantNames();
    }

    // --- Style TableView ---
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

    public static void main(String[] args) {
        launch(args);
    }
}