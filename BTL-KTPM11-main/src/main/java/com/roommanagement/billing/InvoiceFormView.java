package com.roommanagement.billing;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.util.List;

public class InvoiceFormView {
    public ComboBox<String> cbTenant = new ComboBox<>();
    public TextField txtRoom = new TextField();
    public TextField txtPhone = new TextField();
    public TextField txtAddress = new TextField();
    public TextField txtChuHo = new TextField();
    public TextField txtMessage = new TextField();
    public TextField txtTotal = new TextField();
    public Button btnSendInvoice = new Button("📧 Gửi hóa đơn & thông báo");
    public Label lblNotifyStatus = new Label();

    public TableView<InvoiceFormController.InvoiceItem> tblServices = new TableView<>();
    public ObservableList<InvoiceFormController.InvoiceItem> items = FXCollections.observableArrayList();

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
        TableColumn<InvoiceFormController.InvoiceItem, String> colDichVu = new TableColumn<>("Nội dung");
        colDichVu.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().tenDichVu));
        colDichVu.setCellFactory(TextFieldTableCell.forTableColumn());
        colDichVu.setOnEditCommit(e -> e.getRowValue().tenDichVu = e.getNewValue());

        TableColumn<InvoiceFormController.InvoiceItem, Integer> colSoLuong = new TableColumn<>("Số lượng");
        colSoLuong.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().soLuong).asObject());
        colSoLuong.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colSoLuong.setOnEditCommit(e -> e.getRowValue().soLuong = e.getNewValue());

        TableColumn<InvoiceFormController.InvoiceItem, String> colDonVi = new TableColumn<>("Đơn vị");
        colDonVi.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().donVi));
        colDonVi.setCellFactory(TextFieldTableCell.forTableColumn());
        colDonVi.setOnEditCommit(e -> e.getRowValue().donVi = e.getNewValue());

        TableColumn<InvoiceFormController.InvoiceItem, Double> colDonGia = new TableColumn<>("Đơn giá");
        colDonGia.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().donGia).asObject());
        colDonGia.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colDonGia.setOnEditCommit(e -> e.getRowValue().donGia = e.getNewValue());

        TableColumn<InvoiceFormController.InvoiceItem, Double> colThanhTien = new TableColumn<>("Thành tiền");
        colThanhTien.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().thanhTien).asObject());
        colThanhTien.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colThanhTien.setEditable(false);

        tblServices.getColumns().setAll(colDichVu, colSoLuong, colDonVi, colDonGia, colThanhTien);
        tblServices.setItems(items);
        tblServices.setEditable(true);
        tblServices.setPrefHeight(180);

        // Nút thêm dịch vụ
        Button btnAddService = new Button("Thêm dịch vụ");
        btnAddService.setOnAction(e -> items.add(new InvoiceFormController.InvoiceItem("Tiền nhà", 1, "tháng", 0)));

        // Nút xóa dịch vụ
        Button btnRemoveService = new Button("Xóa dịch vụ");
        btnRemoveService.setOnAction(e -> {
            InvoiceFormController.InvoiceItem selected = tblServices.getSelectionModel().getSelectedItem();
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