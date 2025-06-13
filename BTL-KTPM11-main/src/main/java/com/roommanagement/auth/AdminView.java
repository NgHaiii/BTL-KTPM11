package com.roommanagement.auth;

import com.roommanagement.auth.AdminModel.*;
import com.roommanagement.database.DatabaseManager;
import com.roommanagement.notification.InvoiceService;
import com.roommanagement.notification.NotificationService;
import com.roommanagement.billing.InvoiceFormView;
import com.roommanagement.billing.InvoiceFormController;

import java.util.Map;

import javax.swing.JPanel;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.roommanagement.auth.AdminService.BillEntry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
 import java.util.ArrayList;


public class AdminView extends Application {
    private final NotificationService notificationService = new NotificationService();
    private final AdminService service = new AdminService();
    private Stage primaryStage;
    private ObservableList<TenantEntry> tenantList = FXCollections.observableArrayList();
    private ObservableList<RoomEntry> roomList = FXCollections.observableArrayList();
    private ObservableList<BillEntry> billist = FXCollections.observableArrayList();

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
// --- Hiển thị Dashboard ---
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
// --- Quản lý phòng ---
// ...existing code...

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

// --- Quản lý người thuê---
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

        public Node getBillManagementPaneStyled() {
    InvoiceFormView view = new InvoiceFormView();
    List<String> tenantNames = service.getTenantNames();
    InvoiceFormController controller = new InvoiceFormController(service, view);
    return view.getView(tenantNames);
}
    
/*public class InvoiceFormController {
    private final AdminService service;
    private final InvoiceFormView view;

    public InvoiceFormController(AdminService service, InvoiceFormView view) {
        this.service = service;
        this.view = view;
        initEvents();
    }

    private void initEvents() {
        // Tự động điền thông tin khi chọn người thuê
        view.cbTenant.setOnAction(ev -> {
            String tenant = view.cbTenant.getValue();
            if (tenant != null && !tenant.isEmpty()) {
                AdminService.TenantInfo info = service.getTenantInfo(tenant);
                view.txtRoom.setText(info.room);
                view.txtPhone.setText(info.phone);
                view.txtAddress.setText(info.address);
            }
        });

        // Tự động tính tổng tiền khi thay đổi dịch vụ
        view.items.addListener((javafx.collections.ListChangeListener<InvoiceItem>) c -> updateTotal());
        view.tblServices.setItems(view.items);

        // Khi chỉnh sửa số lượng, đơn giá, tự động tính lại thành tiền và tổng tiền
        view.tblServices.setEditable(true);

        view.btnSendInvoice.setOnAction(ev -> sendInvoice());
    }

    private void updateTotal() {
        double total = 0;
        for (InvoiceItem item : view.items) {
            item.thanhTien = item.soLuong * item.donGia;
            total += item.thanhTien;
        }
        view.tblServices.refresh();
        view.txtTotal.setText(String.format("%,.0f", total));
    }

    private void sendInvoice() {
        String tenant = view.cbTenant.getValue();
        String room = view.txtRoom.getText().trim();
        String phone = view.txtPhone.getText().trim();
        String address = view.txtAddress.getText().trim();
        String chuHo = view.txtChuHo.getText().trim();
        String message = view.txtMessage.getText().trim();

        if (tenant == null || tenant.isEmpty() || room.isEmpty() || phone.isEmpty() || address.isEmpty()
                || chuHo.isEmpty() || view.items.isEmpty() || message.isEmpty()) {
            view.lblNotifyStatus.setText("Vui lòng nhập đầy đủ thông tin hóa đơn và thông báo!");
            return;
        }
        try {
            double tongTien = 0;
            for (InvoiceItem item : view.items) tongTien += item.thanhTien;

            // 1. Xuất file hóa đơn PDF
            String filePath = "hoadon_" + tenant + "_" + System.currentTimeMillis() + ".pdf";
            exportInvoiceToPDF(filePath, tenant, room, phone, address, chuHo, new ArrayList<>(view.items), tongTien);

            // 2. Lưu thông báo vào database
            service.sendNotification(tenant, message + " [File hóa đơn: " + filePath + "]", view.lblNotifyStatus);

            view.lblNotifyStatus.setText("Đã gửi hóa đơn và thông báo cho " + tenant + ". File: " + filePath);
            Platform.runLater(() -> {
                try {
                    java.awt.Desktop.getDesktop().open(new java.io.File(filePath));
                } catch (Exception e) {
                    // ignore
                }
            });

            // Xóa trắng form
            view.cbTenant.setValue(null);
            view.txtRoom.clear();
            view.txtPhone.clear();
            view.txtAddress.clear();
            view.txtChuHo.clear();
            view.txtMessage.clear();
            view.items.clear();
            view.txtTotal.clear();
        } catch (Exception ex) {
            view.lblNotifyStatus.setText("Lỗi khi gửi hóa đơn/thông báo: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Hàm xuất hóa đơn PDF (dùng iText)
    private void exportInvoiceToPDF(
            String filePath,
            String tenant,
            String room,
            String phone,
            String address,
            String chuHo,
            List<InvoiceItem> items,
            double tongTien
    ) throws Exception {
        com.itextpdf.text.Document document = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A4, 40, 40, 40, 40);
        com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(filePath));
        document.open();

        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 22, com.itextpdf.text.Font.BOLD, new com.itextpdf.text.BaseColor(0, 102, 204));
        com.itextpdf.text.Font labelFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12);

        com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("HÓA ĐƠN THANH TOÁN", titleFont);
        title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        title.setSpacingAfter(18f);
        document.add(title);

        com.itextpdf.text.Paragraph info = new com.itextpdf.text.Paragraph(
                "Ngày: " + java.time.LocalDate.now() + "\n" +
                "Khách thuê: " + tenant + "\n" +
                "Phòng: " + room + "\n" +
                "SĐT: " + phone + "\n" +
                "Địa chỉ thuê: " + address + "\n" +
                "Chủ hộ: " + chuHo + "\n"
        , normalFont);
        info.setSpacingAfter(18f);
        document.add(info);

        com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(5);
        table.setWidths(new float[]{1.2f, 3.5f, 1.5f, 2f, 2f});
        table.setWidthPercentage(100);

        addHeaderCell(table, "Số lượng", labelFont);
        addHeaderCell(table, "Nội dung", labelFont);
        addHeaderCell(table, "Đơn vị", labelFont);
        addHeaderCell(table, "Đơn giá", labelFont);
        addHeaderCell(table, "Thành tiền", labelFont);

        for (InvoiceItem item : items) {
            addNormalCell(table, String.valueOf(item.soLuong), normalFont, com.itextpdf.text.Element.ALIGN_CENTER);
            addNormalCell(table, item.tenDichVu, normalFont, com.itextpdf.text.Element.ALIGN_LEFT);
            addNormalCell(table, item.donVi, normalFont, com.itextpdf.text.Element.ALIGN_CENTER);
            addNormalCell(table, String.format("%,.0f", item.donGia), normalFont, com.itextpdf.text.Element.ALIGN_RIGHT);
            addNormalCell(table, String.format("%,.0f", item.thanhTien), normalFont, com.itextpdf.text.Element.ALIGN_RIGHT);
        }

        document.add(table);

        com.itextpdf.text.Paragraph totalP = new com.itextpdf.text.Paragraph("TỔNG CỘNG: " + String.format("%,.0f", tongTien) + " VNĐ", labelFont);
        totalP.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
        totalP.setSpacingBefore(18f);
        document.add(totalP);

        document.close();
    }

    private void addHeaderCell(com.itextpdf.text.pdf.PdfPTable table, String text, com.itextpdf.text.Font font) {
        com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(text, font));
        cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        cell.setBackgroundColor(new com.itextpdf.text.BaseColor(230, 230, 250));
        table.addCell(cell);
    }

    private void addNormalCell(com.itextpdf.text.pdf.PdfPTable table, String text, com.itextpdf.text.Font font, int align) {
        com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(text, font));
        cell.setHorizontalAlignment(align);
        table.addCell(cell);
    }

    // Model dịch vụ hóa đơn
    public static class InvoiceItem {
        public String tenDichVu;
        public int soLuong;
        public String donVi;
        public double donGia;
        public double thanhTien;

        public InvoiceItem(String tenDichVu, int soLuong, String donVi, double donGia) {
            this.tenDichVu = tenDichVu;
            this.soLuong = soLuong;
            this.donVi = donVi;
            this.donGia = donGia;
            this.thanhTien = soLuong * donGia;
        }
    }

    // --- View cho form hóa đơn ---
    public static class InvoiceFormView {
        public ComboBox<String> cbTenant = new ComboBox<>();
        public TextField txtRoom = new TextField();
        public TextField txtPhone = new TextField();
        public TextField txtAddress = new TextField();
        public TextField txtChuHo = new TextField();
        public TextField txtMessage = new TextField();
        public TextField txtTotal = new TextField();
        public Button btnSendInvoice = new Button("📧 Gửi hóa đơn & thông báo");
        public Label lblNotifyStatus = new Label();

        // Table dịch vụ
        public TableView<InvoiceItem> tblServices = new TableView<>();
        public ObservableList<InvoiceItem> items = FXCollections.observableArrayList();

        public Node getView(List<String> tenantNames) {
            cbTenant.setItems(FXCollections.observableArrayList(tenantNames));
            cbTenant.setPromptText("Chọn người thuê");
            cbTenant.setPrefHeight(38);

            txtRoom.setPromptText("Phòng");
            txtRoom.setPrefHeight(38);
            txtRoom.setEditable(false);

            txtPhone.setPromptText("SĐT");
            txtPhone.setPrefHeight(38);
            txtPhone.setEditable(false);

            txtAddress.setPromptText("Địa chỉ thuê");
            txtAddress.setPrefHeight(38);
            txtAddress.setEditable(false);

            txtChuHo.setPromptText("Chủ hộ");
            txtChuHo.setPrefHeight(38);

            txtMessage.setPromptText("Nội dung thông báo gửi kèm hóa đơn");
            txtMessage.setPrefHeight(38);

            txtTotal.setPromptText("Tổng tiền");
            txtTotal.setPrefHeight(38);
            txtTotal.setEditable(false);
            txtTotal.setStyle("-fx-background-color: #e0f7fa;");

            // Table dịch vụ
            TableColumn<InvoiceItem, String> colDichVu = new TableColumn<>("Nội dung");
            colDichVu.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().tenDichVu));
            colDichVu.setCellFactory(TextFieldTableCell.forTableColumn());
            colDichVu.setOnEditCommit(e -> e.getRowValue().tenDichVu = e.getNewValue());

            TableColumn<InvoiceItem, Integer> colSoLuong = new TableColumn<>("Số lượng");
            colSoLuong.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().soLuong).asObject());
            colSoLuong.setCellFactory(TextFieldTableCell.forTableColumn(new javafx.util.converter.IntegerStringConverter()));
            colSoLuong.setOnEditCommit(e -> e.getRowValue().soLuong = e.getNewValue());

            TableColumn<InvoiceItem, String> colDonVi = new TableColumn<>("Đơn vị");
            colDonVi.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().donVi));
            colDonVi.setCellFactory(TextFieldTableCell.forTableColumn());
            colDonVi.setOnEditCommit(e -> e.getRowValue().donVi = e.getNewValue());

            TableColumn<InvoiceItem, Double> colDonGia = new TableColumn<>("Đơn giá");
            colDonGia.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().donGia).asObject());
            colDonGia.setCellFactory(TextFieldTableCell.forTableColumn(new javafx.util.converter.DoubleStringConverter()));
            colDonGia.setOnEditCommit(e -> e.getRowValue().donGia = e.getNewValue());

            TableColumn<InvoiceItem, Double> colThanhTien = new TableColumn<>("Thành tiền");
            colThanhTien.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().thanhTien).asObject());
            colThanhTien.setCellFactory(TextFieldTableCell.forTableColumn(new javafx.util.converter.DoubleStringConverter()));
            colThanhTien.setEditable(false);

            tblServices.getColumns().addAll(colDichVu, colSoLuong, colDonVi, colDonGia, colThanhTien);
            tblServices.setItems(items);
            tblServices.setEditable(true);
            tblServices.setPrefHeight(180);

            // Nút thêm dịch vụ
            Button btnAddService = new Button("Thêm dịch vụ");
            btnAddService.setOnAction(e -> items.add(new InvoiceItem("Tiền nhà", 1, "tháng", 0)));

            // Nút xóa dịch vụ
            Button btnRemoveService = new Button("Xóa dịch vụ");
            btnRemoveService.setOnAction(e -> {
                InvoiceItem selected = tblServices.getSelectionModel().getSelectedItem();
                if (selected != null) items.remove(selected);
            });

            HBox serviceButtons = new HBox(10, btnAddService, btnRemoveService);

            GridPane form = new GridPane();
            form.setHgap(18);
            form.setVgap(14);
            form.setPadding(new Insets(18, 18, 18, 18));
            form.setStyle("-fx-background-color: #fff; -fx-background-radius: 12;");

            form.add(new Label("Người thuê:"), 0, 0); form.add(cbTenant, 1, 0);
            form.add(new Label("Phòng:"), 0, 1); form.add(txtRoom, 1, 1);
            form.add(new Label("SĐT:"), 0, 2); form.add(txtPhone, 1, 2);
            form.add(new Label("Địa chỉ thuê:"), 0, 3); form.add(txtAddress, 1, 3);
            form.add(new Label("Chủ hộ:"), 0, 4); form.add(txtChuHo, 1, 4);

            VBox serviceBox = new VBox(8, tblServices, serviceButtons);
            form.add(new Label("Dịch vụ:"), 0, 5); form.add(serviceBox, 1, 5);

            form.add(new Label("Tổng tiền:"), 0, 6); form.add(txtTotal, 1, 6);
            form.add(new Label("Nội dung thông báo:"), 0, 7); form.add(txtMessage, 1, 7);

            VBox invoicePane = new VBox(18, form, btnSendInvoice, lblNotifyStatus);
            invoicePane.setPadding(new Insets(24));
            invoicePane.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 18;");

            return invoicePane;
        }
    }
}*/
// --- Gửi thông báo ---
// Lấy danh sách người thuê từ DB
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

// Hàm tạo giao diện gửi thông báo
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
