package com.roommanagement.billing;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.util.List;

import com.mysql.cj.x.protobuf.MysqlxCrud.DataModel;
import com.roommanagement.billing.InvoiceFormController.InvoiceItem;

public class InvoiceFormView {



    public ComboBox<String> cbTenant = new ComboBox<>();
    public TextField txtRoom = new TextField();
    public TextField txtPhone = new TextField();
    public TextField txtAddress = new TextField();
    public TextField txtChuHo = new TextField();
    public TextField txtMessage = new TextField();
    public TextField txtTotal = new TextField();
    public Button btnSendInvoice = new Button("📧 Tạo hóa đơn");
    public Label lblNotifyStatus = new Label();
public int khoiNuoc;
ObservableList<DataModel> data = FXCollections.observableArrayList();
    public TableView<InvoiceFormController.InvoiceItem> tblServices = new TableView<>();
    public ObservableList<InvoiceFormController.InvoiceItem> items = FXCollections.observableArrayList();
@SuppressWarnings("unchecked")
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

//Tạo ComboBox chọn loại dịch vụ
        ComboBox<String> serviceTypeBox = new ComboBox<>();
        serviceTypeBox.getItems().addAll("Tiền phòng", "Tiền điện", "Tiền nước", "Tiền dịch vụ");
        serviceTypeBox.setValue("Tiền phòng");

        // Các trường nhập liệu
        TextField txtSoLuong = new TextField();
        txtSoLuong.setPromptText("Số lượng");

        TextField txtSoDien = new TextField();
        txtSoDien.setPromptText("Số điện");

        TextField txtKhoiNuoc = new TextField();
        txtKhoiNuoc.setPromptText("Khối nước");

        TextField txtDonGia = new TextField();
        txtDonGia.setPromptText("Đơn giá");

        // Ẩn/hiện các trường nhập liệu theo dịch vụ
        serviceTypeBox.setOnAction(e -> {
            String selected = serviceTypeBox.getValue();
            txtSoLuong.setVisible(false);
            txtSoDien.setVisible(false);
            txtKhoiNuoc.setVisible(false);
            txtDonGia.setVisible(true); // Đơn giá luôn hiện

            if ("Tiền phòng".equals(selected) || "Tiền dịch vụ".equals(selected)) {
                txtSoLuong.setVisible(true);
            }
            if ("Tiền điện".equals(selected)) {
                txtSoDien.setVisible(true);
            }
            if ("Tiền nước".equals(selected)) {
                txtKhoiNuoc.setVisible(true);
            }
        });
        // Gọi 1 lần để thiết lập ban đầu
        serviceTypeBox.getOnAction().handle(null);

        Button btnAddService = new Button("Thêm dịch vụ");

        btnAddService.setOnAction(e -> {
            String selectedService = serviceTypeBox.getValue();
            int soLuong = 1;
            int soDien = 0;
            int khoiNuoc = 0;
            double donGia = 0.0;

            try { soLuong = Integer.parseInt(txtSoLuong.getText()); } catch (Exception ex) {}
            try { soDien = Integer.parseInt(txtSoDien.getText()); } catch (Exception ex) {}
            try { khoiNuoc = Integer.parseInt(txtKhoiNuoc.getText()); } catch (Exception ex) {}
            try { donGia = Double.parseDouble(txtDonGia.getText()); } catch (Exception ex) {}

            InvoiceFormController.InvoiceItem item = new InvoiceFormController.InvoiceItem(
                selectedService, soLuong, "tháng", soDien, khoiNuoc, donGia
            );
            updateThanhTien(item); // phải gọi trước khi add vào items
            items.add(item);

            txtSoLuong.clear();
            txtSoDien.clear();
            txtKhoiNuoc.clear();
            txtDonGia.clear();

            // Cập nhật tổng tiền
            txtTotal.setText(String.format("%,.0f", tinhTongCongTatCaDichVu(items)));
        });

        Button btnRemoveService = new Button("Xóa dịch vụ");
        btnRemoveService.setOnAction(e -> {
            InvoiceFormController.InvoiceItem selected = tblServices.getSelectionModel().getSelectedItem();
            if (selected != null) {
                items.remove(selected);
                txtTotal.setText(String.format("%,.0f", tinhTongCongTatCaDichVu(items)));
            }
        });

        // HBox chứa các trường nhập liệu (luôn giữ thứ tự)
        HBox inputBox = new HBox(10, txtSoLuong, txtSoDien, txtKhoiNuoc, txtDonGia);

        // HBox chứa combobox và nút
        HBox serviceButtons = new HBox(10, serviceTypeBox, btnAddService, btnRemoveService);

        // Khai báo các cột TableView
        TableColumn<InvoiceFormController.InvoiceItem, String> colDichVu = new TableColumn<>("Dịch vụ");
        colDichVu.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().tenDichVu));
        colDichVu.setCellFactory(ComboBoxTableCell.forTableColumn("Tiền phòng", "Tiền điện", "Tiền dịch vụ", "Tiền nước"));
        colDichVu.setOnEditCommit(e -> e.getRowValue().tenDichVu = e.getNewValue());

        TableColumn<InvoiceFormController.InvoiceItem, Integer> colSoLuong = new TableColumn<>("Số lượng");
        colSoLuong.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().soLuong).asObject());
        colSoLuong.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colSoLuong.setOnEditCommit(e -> {
            e.getRowValue().soLuong = e.getNewValue();
            updateThanhTien(e.getRowValue());
            txtTotal.setText(String.format("%,.0f", tinhTongCongTatCaDichVu(items)));
        });

        TableColumn<InvoiceFormController.InvoiceItem, Integer> colSoDien = new TableColumn<>("Số điện");
        colSoDien.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().soDien).asObject());
        colSoDien.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colSoDien.setOnEditCommit(e -> {
            e.getRowValue().soDien = e.getNewValue();
            updateThanhTien(e.getRowValue());
            txtTotal.setText(String.format("%,.0f", tinhTongCongTatCaDichVu(items)));
        });

        TableColumn<InvoiceFormController.InvoiceItem, Integer> colKhoiNuoc = new TableColumn<>("Khối nước");
        colKhoiNuoc.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().khoiNuoc).asObject());
        colKhoiNuoc.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colKhoiNuoc.setOnEditCommit(e -> {
            e.getRowValue().khoiNuoc = e.getNewValue();
            updateThanhTien(e.getRowValue());
            txtTotal.setText(String.format("%,.0f", tinhTongCongTatCaDichVu(items)));
        });

        TableColumn<InvoiceFormController.InvoiceItem, String> colDonVi = new TableColumn<>("Đơn vị");
        colDonVi.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().donVi));
        colDonVi.setCellFactory(TextFieldTableCell.forTableColumn());
        colDonVi.setOnEditCommit(e -> e.getRowValue().donVi = e.getNewValue());

        TableColumn<InvoiceFormController.InvoiceItem, Double> colDonGia = new TableColumn<>("Đơn giá");
        colDonGia.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().donGia).asObject());
        colDonGia.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colDonGia.setOnEditCommit(e -> {
            e.getRowValue().donGia = e.getNewValue();
            updateThanhTien(e.getRowValue());
            txtTotal.setText(String.format("%,.0f", tinhTongCongTatCaDichVu(items)));
        });

        TableColumn<InvoiceFormController.InvoiceItem, Double> colThanhTien = new TableColumn<>("Thành tiền");
colThanhTien.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().thanhTien).asObject());
colThanhTien.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
colThanhTien.setEditable(false);
        tblServices.getColumns().setAll(colDichVu, colSoLuong, colSoDien, colKhoiNuoc, colDonVi, colDonGia, colThanhTien);
        tblServices.setItems(items);
        tblServices.setEditable(true);
        tblServices.setPrefHeight(180);

        VBox serviceBox = new VBox(8, tblServices, inputBox, serviceButtons);

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
        form.add(new Label("Dịch vụ:"), 0, 5); form.add(serviceBox, 1, 5);
        form.add(new Label("Tổng tiền:"), 0, 6); form.add(txtTotal, 1, 6);
        form.add(new Label("Nội dung thông báo:"), 0, 7); form.add(txtMessage, 1, 7);

        VBox invoicePane = new VBox(18, form, btnSendInvoice, lblNotifyStatus);
        invoicePane.setPadding(new Insets(24));
        invoicePane.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 18;");

        return invoicePane;
    }

    // Hàm tính thành tiền cho từng dịch vụ
    public void updateThanhTien(InvoiceFormController.InvoiceItem item) {
    if ("Tiền điện".equals(item.tenDichVu)) {
        item.thanhTien = item.soDien * item.donGia;
    } else if ("Tiền nước".equals(item.tenDichVu)) {
        item.thanhTien = item.khoiNuoc * item.donGia;
    } else {
        item.thanhTien = item.soLuong * item.donGia;
    }
}

    // Hàm tính tổng cộng tất cả dịch vụ
    public double tinhTongCongTatCaDichVu(ObservableList<InvoiceFormController.InvoiceItem> items) {
        double tong = 0;
        for (InvoiceFormController.InvoiceItem item : items) {
            tong += item.thanhTien;
        }
        return tong;
    }
}