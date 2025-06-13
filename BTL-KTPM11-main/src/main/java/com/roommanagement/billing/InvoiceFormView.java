package com.roommanagement.billing;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.util.List;

import com.roommanagement.billing.InvoiceFormController.InvoiceItem;

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

        // Table dịch vụ
        
        // Tạo ComboBox chọn loại dịch vụ
ComboBox<String> serviceTypeBox = new ComboBox<>();
serviceTypeBox.getItems().addAll("Tiền phòng", "Tiền điện nước", "Tiền dịch vụ");
serviceTypeBox.setValue("Tiền phòng"); // Giá trị mặc định

Button btnAddService = new Button("Thêm dịch vụ");
btnAddService.setOnAction(e -> {
    String selectedService = serviceTypeBox.getValue();
    items.add(new InvoiceFormController.InvoiceItem(selectedService, 1, "tháng", 0));
});

TableColumn<InvoiceItem, String> colDichVu = new TableColumn<>("Dịch vụ");
colDichVu.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().tenDichVu));
colDichVu.setCellFactory(ComboBoxTableCell.forTableColumn("Tiền phòng", "Tiền điện nước", "Tiền dịch vụ"));
colDichVu.setOnEditCommit(e -> e.getRowValue().tenDichVu = e.getNewValue());

        TableColumn<InvoiceFormController.InvoiceItem, Integer> colSoLuong = new TableColumn<>("Số lượng");
        colSoLuong.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().soLuong).asObject());
        colSoLuong.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colSoLuong.setOnEditCommit(e -> e.getRowValue().soLuong = e.getNewValue());

        TableColumn<InvoiceFormController.InvoiceItem, String> colDonVi = new TableColumn<>("Đơn vị");
        colDonVi.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().donVi));
        colDonVi.setCellFactory(TextFieldTableCell.forTableColumn());
        colDonVi.setOnEditCommit(e -> e.getRowValue().donVi = e.getNewValue());

        /*TableColumn<InvoiceFormController.InvoiceItem, Double> colDonGia = new TableColumn<>("Đơn giá");
        colDonGia.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().donGia).asObject());
        colDonGia.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colDonGia.setOnEditCommit(e -> e.getRowValue().donGia = e.getNewValue());*/

        TableColumn<InvoiceFormController.InvoiceItem, Double> colThanhTien = new TableColumn<>("Thành tiền");
        colThanhTien.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().thanhTien).asObject());
        colThanhTien.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colThanhTien.setEditable(false);

        TableColumn<InvoiceItem, Integer> colSoDien = new TableColumn<>("Số điện");
        colSoDien.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().soDien).asObject());
        colSoDien.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colSoDien.setOnEditCommit(e -> {
        e.getRowValue().soDien = e.getNewValue();
         updateThanhTien(e.getRowValue());
      
});
TableColumn<InvoiceItem, Double> colDonGia = new TableColumn<>("Đơn giá");
        colDonGia.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().donGia).asObject());
        colDonGia.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colDonGia.setOnEditCommit(e -> {
    e.getRowValue().donGia = e.getNewValue();
    updateThanhTien(e.getRowValue());
});
        
        tblServices.getColumns().setAll(colDichVu, colSoLuong, colSoDien, colDonVi, colDonGia, colThanhTien);
        tblServices.setItems(items);
        tblServices.setEditable(true);
        tblServices.setPrefHeight(180);

        
        // Nút xóa dịch vụ
        Button btnRemoveService = new Button("Xóa dịch vụ");
        btnRemoveService.setOnAction(e -> {
            InvoiceFormController.InvoiceItem selected = tblServices.getSelectionModel().getSelectedItem();
            if (selected != null) items.remove(selected);
        });

        HBox serviceButtons = new HBox(10, serviceTypeBox, btnAddService, btnRemoveService);

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

    public void updateThanhTien(InvoiceFormController.InvoiceItem item) {
        if ("Tiền điện nước".equals(item.tenDichVu)) {
            item.thanhTien = item.soDien * item.donGia;
        } else {
            item.thanhTien = item.soLuong * item.donGia;
        }
        tblServices.refresh();
    }
}
