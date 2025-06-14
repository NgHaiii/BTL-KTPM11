package com.roommanagement.auth;

import com.roommanagement.auth.AdminModel.*;
import com.roommanagement.database.DatabaseManager;
import com.roommanagement.notification.NotificationService;
import com.roommanagement.tenant.TenantInfo;
import com.roommanagement.billing.InvoiceFormView;
import com.roommanagement.billing.InvoiceFormController;

import java.util.Map;
import java.io.File;

import javafx.application.Application;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import com.roommanagement.auth.AdminService.BillEntry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.net.URL;
import java.util.List;
 

public class AdminView extends Application {
    private final NotificationService notificationService = new NotificationService();
    private final AdminService service = new AdminService();
    private Stage primaryStage;
    private ObservableList<TenantEntry> tenantList = FXCollections.observableArrayList();
    private ObservableList<RoomEntry> roomList = FXCollections.observableArrayList();
    private ObservableList<BillEntry> billist = FXCollections.observableArrayList();
    private Parent loginRoot;
private TabPane tabPane;
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
    if (username.isEmpty() || password.isEmpty()) {
        lblLoginStatus.setText("Vui lòng nhập thông tin đăng nhập.");
        Alert alert = new Alert(Alert.AlertType.ERROR, "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.", ButtonType.OK);
        alert.showAndWait();
    } else {
        if (service.loginAdmin(username, password)) {
            lblLoginStatus.setText("Đăng nhập thành công!");
            showDashboard(username);

        } else {
            lblLoginStatus.setText("Đăng nhập thất bại. Kiểm tra lại thông tin.");
            Alert alert = new Alert(Alert.AlertType.ERROR, "Đăng nhập thất bại. Kiểm tra lại tên đăng nhập hoặc mật khẩu.", ButtonType.OK);
            alert.showAndWait();
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

// Hiển thị giao diện Dashboard sau khi đăng nhập thành công
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
Button btnHome = createSidebarButton("🏠", "Home", false);
Button btnAccount = createSidebarButton("👤", "Tài khoản", false);
    Button btnTenant = createSidebarButton("👤", "Quản Lý Người Thuê", true);
    Button btnRoom = createSidebarButton("🏠", "Quản Lý Phòng", false);
    Button btnBill = createSidebarButton("💵", "Quản Lí Hóa Đơn", false);
    Button btnNotify = createSidebarButton("🔔", "Gửi Thông Báo", false);
    Button btnLogout = createSidebarButton("⏻", "Đăng Xuất", false);
    btnLogout.setStyle(btnLogout.getStyle() + "-fx-background-color: #f7971e; -fx-text-fill: white;");

    sidebar.getChildren().add(btnHome); // Vị trí 0
sidebar.getChildren().add(btnAccount); // Vị trí 1 (sau Home)
sidebar.getChildren().addAll(btnTenant, btnRoom, btnBill, btnNotify); // Các nút chức năng còn lại
Region spacer = new Region();
VBox.setVgrow(spacer, Priority.ALWAYS);
sidebar.getChildren().addAll(spacer, btnLogout); // Đăng xuất ở dưới cùng
bp.setLeft(sidebar);

    // Ảnh welcome ở trung tâm khi đăng nhập thành công
   StackPane centerPane;
URL imgUrl = getClass().getResource("/images/man-hinh.jpg");
if (imgUrl == null) {
    centerPane = new StackPane(new Label("Không tìm thấy ảnh welcome!")); // chỉ gán, không khai báo lại
} else {
    Image welcomeImg = new Image(imgUrl.toExternalForm());
    ImageView welcomeImage = new ImageView(welcomeImg);
    welcomeImage.setPreserveRatio(true);
    welcomeImage.setSmooth(true);
    welcomeImage.setCache(true);
    centerPane = new StackPane(welcomeImage); // chỉ gán, không khai báo lại
    centerPane.setStyle("-fx-background-color: #f7fafd; -fx-background-radius: 0 24 24 0;");
    welcomeImage.fitWidthProperty().bind(centerPane.widthProperty());
    welcomeImage.fitHeightProperty().bind(centerPane.heightProperty());
}
bp.setCenter(centerPane);




// Xử lý sự kiện cho các nút sidebar


btnHome.setOnAction(e -> {
    setSidebarActive(sidebar, btnHome);
    bp.setCenter(centerPane); 
});
btnAccount.setOnAction(e -> {
    setSidebarActive(sidebar, btnAccount);
    bp.setCenter(getAccountPane(adminUsername));
});
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
    TabPane billTabPane = new TabPane();
    Tab tabCreateBill = new Tab("Tạo hóa đơn", getBillManagementPaneStyled());
    Tab tabArchive = new Tab("Lưu trữ hóa đơn", getBillArchivePane());
    billTabPane.getTabs().addAll(tabCreateBill, tabArchive);
    bp.setCenter(billTabPane);
});
btnNotify.setOnAction(e -> {
    setSidebarActive(sidebar, btnNotify);
    bp.setCenter(getNotifyPaneStyled());
});
btnLogout.setOnAction(e -> showAuthPane());

Scene scene = new Scene(bp, 1100, 700);
primaryStage.setScene(scene);
primaryStage.show();
}

// Tạo giao diện quản lý tài khoản admin
/*public Node getAccountPane(String username) {
    VBox root = new VBox(28);
    root.setPadding(new Insets(40, 0, 0, 0));
    root.setAlignment(Pos.TOP_CENTER);

    // Avatar tròn lớn, viền sáng
    ImageView avatar = new ImageView(service.getAvatarForUser(username));
    avatar.setFitWidth(140);
    avatar.setFitHeight(140);
    avatar.setPreserveRatio(true);

    Circle clip = new Circle(70, 70, 70);
    avatar.setClip(clip);

    // Viền sáng cho avatar
    Circle border = new Circle(70, 70, 72);
    border.setStroke(Color.web("#43cea2"));
    border.setStrokeWidth(4);
    border.setFill(Color.TRANSPARENT);

    StackPane avatarPane = new StackPane(border, avatar);
    avatarPane.setPrefSize(144, 144);

    
// Nút đổi ảnh
Button btnChangeAvatar = new Button("🖼 Đổi ảnh");
btnChangeAvatar.setStyle(
    "-fx-background-radius: 20; " +
    "-fx-background-color: linear-gradient(to right, #43cea2, #185a9d);" +
    "-fx-text-fill: white; -fx-font-size: 15px; -fx-padding: 6 18;"
);
btnChangeAvatar.setOnAction(e -> {
    FileChooser fc = new FileChooser();
    fc.setTitle("Chọn ảnh đại diện");
    File file = fc.showOpenDialog(root.getScene().getWindow());
    if (file != null) {
        avatar.setImage(new Image(file.toURI().toString()));
        service.saveAvatarForUser(username, file);
    }
});

// Nút xóa ảnh
Button btnDeleteAvatar = new Button("🗑 Xóa ảnh");
btnDeleteAvatar.setStyle(
    "-fx-background-radius: 20; " +
    "-fx-background-color: #e57373;" +
    "-fx-text-fill: white; -fx-font-size: 15px; -fx-padding: 6 18;"
);
btnDeleteAvatar.setOnAction(e -> {
    service.deleteAvatarForUser(username); // Xóa file ảnh
    avatar.setImage(service.getAvatarForUser(username)); // Hiển thị lại ảnh mặc định ngay lập tức
});
// Đặt hai nút cạnh nhau
HBox avatarBtnBox = new HBox(12, btnChangeAvatar, btnDeleteAvatar);
avatarBtnBox.setAlignment(Pos.CENTER);


    // Thông tin cá nhân
    Label lblName = new Label(service.getDisplayName(username));
    lblName.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-padding: 10 0 0 0;");
    Label lblPhone = new Label("📞 " + service.getPhone(username));
    lblPhone.setStyle("-fx-font-size: 18px;");
    Label lblEmail = new Label("✉ " + service.getEmail(username));
    lblEmail.setStyle("-fx-font-size: 18px;");
    VBox infoBox = new VBox(8, lblName, lblPhone, lblEmail);
    infoBox.setAlignment(Pos.CENTER);

    // Danh sách tài khoản đã đăng ký
    Label lblList = new Label("Tài khoản đã đăng ký");
    lblList.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 24 0 0 0;");
    VBox accountListBox = new VBox(12);
    accountListBox.setAlignment(Pos.TOP_CENTER);

    for (String user : service.getAllUsernames()) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        Label userLabel = new Label(user);
        userLabel.setStyle("-fx-font-size: 17px;");
        Button btnMore = new Button("⋮");
        btnMore.setStyle(
            "-fx-background-radius: 50%; -fx-background-color: #e0e0e0; -fx-font-size: 18px; -fx-min-width: 36px; -fx-min-height: 36px;"
        );
        btnMore.setOnAction(ev -> {
            ContextMenu menu = new ContextMenu();
            MenuItem delete = new MenuItem("Xóa tài khoản");
            delete.setOnAction(delEv -> {
                if (!user.equals(username)) {
                    service.deleteAccount(user);
                    accountListBox.getChildren().remove(row);
                }
            });
            menu.getItems().add(delete);
            menu.show(btnMore, Side.BOTTOM, 0, 0);
        });
        row.getChildren().addAll(userLabel, btnMore);
        accountListBox.getChildren().add(row);
    }

    root.getChildren().addAll(avatarPane, avatarBtnBox, infoBox, lblList, accountListBox);
    return root;
}*/
public Node getAccountPane(String username) {
    VBox root = new VBox(28);
    root.setPadding(new Insets(40, 0, 0, 0));
    root.setAlignment(Pos.TOP_CENTER);

    // Avatar tròn lớn, viền sáng
    ImageView avatar = new ImageView(service.getAvatarForUser(username));
    avatar.setFitWidth(140);
    avatar.setFitHeight(140);
    avatar.setPreserveRatio(true);

    Circle clip = new Circle(70, 70, 70);
    avatar.setClip(clip);

    // Viền sáng cho avatar
    Circle border = new Circle(70, 70, 72);
    border.setStroke(Color.web("#43cea2"));
    border.setStrokeWidth(4);
    border.setFill(Color.TRANSPARENT);

    StackPane avatarPane = new StackPane(border, avatar);
    avatarPane.setPrefSize(144, 144);

    // Nút đổi ảnh
    Button btnChangeAvatar = new Button("🖼 Đổi ảnh");
    btnChangeAvatar.setStyle(
        "-fx-background-radius: 20; " +
        "-fx-background-color: linear-gradient(to right, #43cea2, #185a9d);" +
        "-fx-text-fill: white; -fx-font-size: 15px; -fx-padding: 6 18;"
    );
    btnChangeAvatar.setOnAction(e -> {
        FileChooser fc = new FileChooser();
        fc.setTitle("Chọn ảnh đại diện");
        File file = fc.showOpenDialog(root.getScene().getWindow());
        if (file != null) {
            avatar.setImage(new Image(file.toURI().toString()));
            service.saveAvatarForUser(username, file);
        }
    });

    // Nút xóa ảnh
    Button btnDeleteAvatar = new Button("🗑 Xóa ảnh");
    btnDeleteAvatar.setStyle(
        "-fx-background-radius: 20; " +
        "-fx-background-color: #e57373;" +
        "-fx-text-fill: white; -fx-font-size: 15px; -fx-padding: 6 18;"
    );
    btnDeleteAvatar.setOnAction(e -> {
        service.deleteAvatarForUser(username); // Xóa file ảnh
        avatar.setImage(service.getAvatarForUser(username)); // Hiển thị lại ảnh mặc định ngay lập tức
    });
    // Đặt hai nút cạnh nhau
    HBox avatarBtnBox = new HBox(12, btnChangeAvatar, btnDeleteAvatar);
    avatarBtnBox.setAlignment(Pos.CENTER);

    // Thông tin cá nhân
    Label lblName = new Label(service.getDisplayName(username));
    lblName.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-padding: 10 0 0 0;");
    Label lblPhone = new Label("📞 " + service.getPhone(username));
    lblPhone.setStyle("-fx-font-size: 18px;");
    Label lblEmail = new Label("✉ " + service.getEmail(username));
    lblEmail.setStyle("-fx-font-size: 18px;");
    VBox infoBox = new VBox(8, lblName, lblPhone, lblEmail);
    infoBox.setAlignment(Pos.CENTER);

    // Danh sách tài khoản đã đăng ký
    Label lblList = new Label("Tài khoản đã đăng ký");
    lblList.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 24 0 0 0;");
    VBox accountListBox = new VBox(12);
    accountListBox.setAlignment(Pos.TOP_CENTER);
    
Runnable[] refreshAccountList = new Runnable[1];
refreshAccountList[0] = () -> {
    accountListBox.getChildren().clear();
    for (String user : service.getAllUsernames()) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        Label userLabel = new Label(user);
        userLabel.setStyle("-fx-font-size: 17px;");
        Button btnMore = new Button("⋮");
        btnMore.setStyle(
            "-fx-background-radius: 50%; -fx-background-color: #e0e0e0; -fx-font-size: 18px; -fx-min-width: 36px; -fx-min-height: 36px;"
        );
        btnMore.setOnAction(ev -> {
            ContextMenu menu = new ContextMenu();
            MenuItem delete = new MenuItem("Xóa tài khoản");
            delete.setOnAction(delEv -> {
                if (!user.equals(username)) {
                    service.deleteAccount(user);
                    refreshAccountList[0].run(); // Cập nhật lại danh sách ngay sau khi xóa
                }
            });
            menu.getItems().add(delete);
            menu.show(btnMore, Side.BOTTOM, 0, 0);
        });
        row.getChildren().addAll(userLabel, btnMore);
        accountListBox.getChildren().add(row);
    }
};

// Gọi lần đầu để hiển thị danh sách
refreshAccountList[0].run();
    root.getChildren().addAll(avatarPane, avatarBtnBox, infoBox, lblList, accountListBox);
    return root;
}


    // Tạo giao diện thông báo
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
     
// Hiển thị danh sách hóa đơn của từng người thuê
    private void showTenantInvoiceStage(String tenantName) {
    Stage stage = new Stage();
    VBox root = new VBox(10);
    root.setPadding(new Insets(20));

    // Lấy thông tin người thuê (giả sử có hàm getTenantInfo)

// Lấy thông tin người thuê
TenantInfo info = service.getTenantInfo(tenantName);

Label infoLabel = new Label(
    "Tên: " + tenantName + "\n" +
    "Phòng: " + info.getRoomName() + "\n" +
    "SĐT: " + info.getPhone() + "\n" +
    "Địa chỉ: " + info.getAddress()
);

    // Lấy danh sách file hóa đơn của khách này (theo định dạng mới)
    File invoiceDir = new File("invoices");
    File[] files = invoiceDir.listFiles((dir, name) ->
        name.startsWith(tenantName + "_") && name.endsWith(".pdf")
    );

    ListView<File> fileList = new ListView<>();
    if (files != null) fileList.getItems().addAll(files);

    fileList.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
        @Override
        protected void updateItem(File item, boolean empty) {
            super.updateItem(item, empty);
            setText(empty || item == null ? null : item.getName());
        }
    });

    // Khi click vào file, mở file hóa đơn
    fileList.setOnMouseClicked(e -> {
        File selectedFile = fileList.getSelectionModel().getSelectedItem();
        if (selectedFile != null) {
            try {
                java.awt.Desktop.getDesktop().open(selectedFile);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    });
    

    // Nút copy đường dẫn
   /* Button btnCopyPath = new Button("Copy đường dẫn");
    btnCopyPath.setOnAction(e -> {
        File selectedFile = fileList.getSelectionModel().getSelectedItem();
        if (selectedFile != null) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(selectedFile.getAbsolutePath());
            clipboard.setContent(content);
        }
    });

    root.getChildren().addAll(infoLabel, new Label("Hóa đơn đã tạo:"), fileList, btnCopyPath);

    stage.setScene(new Scene(root, 400, 400));
    stage.setTitle("Thông tin & hóa đơn: " + tenantName);
    stage.show();
}*/
// ...existing code...

Button btnCopyPath = new Button("Copy đường dẫn");
btnCopyPath.setOnAction(e -> {
    File selectedFile = fileList.getSelectionModel().getSelectedItem();
    if (selectedFile != null) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(selectedFile.getAbsolutePath());
        clipboard.setContent(content);
    }
});

// Nút xóa hóa đơn
Button btnDelete = new Button("Xóa hóa đơn");
btnDelete.setOnAction(e -> {
    File selectedFile = fileList.getSelectionModel().getSelectedItem();
    if (selectedFile != null) {
        boolean deleted = selectedFile.delete();
        if (deleted) {
            fileList.getItems().remove(selectedFile);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Không thể xóa file hóa đơn!", ButtonType.OK);
            alert.showAndWait();
        }
    }
});

HBox buttonBox = new HBox(10, btnCopyPath, btnDelete);

root.getChildren().addAll(infoLabel, new Label("Hóa đơn đã tạo:"), fileList, buttonBox);

stage.setScene(new Scene(root, 400, 400));
stage.setTitle("Thông tin & hóa đơn: " + tenantName);
stage.show();
    }

public Node getBillArchivePane() {
    VBox root = new VBox(10);
    root.setPadding(new Insets(20));

    Label title = new Label("Tra cứu hóa đơn");
    title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

    ListView<String> tenantList = new ListView<>();
    tenantList.setItems(FXCollections.observableArrayList(service.getTenantNames()));

    tenantList.setOnMouseClicked(e -> {
        String selectedTenant = tenantList.getSelectionModel().getSelectedItem();
        if (selectedTenant != null) {
            showTenantInvoiceStage(selectedTenant);
        }
    });

    root.getChildren().addAll(title, new Label("Chọn người thuê:"), tenantList);
    return root;
}
    

    
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
