package com.roommanagement.billing;

import com.roommanagement.auth.AdminService;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import com.roommanagement.tenant.TenantInfo;
import java.util.ArrayList;
import java.util.List;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;

import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
public class InvoiceFormController {
    private final AdminService service;
    private final InvoiceFormView view;
    public InvoiceFormController(AdminService service, InvoiceFormView view) {
        this.service = service;
        this.view = view;
        initEvents();
        
    }
public void addCell(PdfPTable table, String text, Font font) {
    PdfPCell cell = new PdfPCell(new Phrase(text, font));
    table.addCell(cell);
}

    private void initEvents() {
        // Tự động điền thông tin khi chọn người thuê
        view.cbTenant.setOnAction(ev -> {
            String tenant = view.cbTenant.getValue();
            if (tenant != null && !tenant.isEmpty()) {
                TenantInfo info = service.getTenantInfo(tenant);
                view.txtRoom.setText(info.getRoomName());
                view.txtPhone.setText(info.getPhone());
                view.txtAddress.setText(info.getAddress());
            }
        });

        view.items.addListener((ListChangeListener<InvoiceItem>) c -> updateTotal());
        view.tblServices.setItems(view.items);
        view.tblServices.setEditable(true);
        view.btnSendInvoice.setOnAction(ev -> sendInvoice());
    }

    private void updateTotal() {
    double total = 0;
    for (InvoiceItem item : view.items) {
        // Tính thành tiền đúng theo loại dịch vụ
        if ("Tiền điện".equals(item.tenDichVu)) {
            item.thanhTien = item.soDien * item.donGia;
        } else if ("Tiền nước".equals(item.tenDichVu)) {
            item.thanhTien = item.khoiNuoc * item.donGia;
        } else {
            item.thanhTien = item.soLuong * item.donGia;
        }
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


            String monthYear = java.time.LocalDate.now().getMonthValue() + "_" + java.time.LocalDate.now().getYear();
String dirPath = "invoices";
new java.io.File(dirPath).mkdirs();
String filePath = dirPath + "/" + tenant + "_" + monthYear + ".pdf";

exportInvoiceToPDF(filePath, tenant, room, phone, address, chuHo, new ArrayList<>(view.items), tongTien);

view.lblNotifyStatus.setText("Đã lưu hóa đơn cho " + tenant + ". File: " + filePath);

Platform.runLater(() -> {
    try {
        java.awt.Desktop.getDesktop().open(new java.io.File(filePath));
    } catch (Exception e) {
    }
});

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

    String fontPath = "c:/windows/fonts/arial.ttf"; 
    com.itextpdf.text.pdf.BaseFont bf = com.itextpdf.text.pdf.BaseFont.createFont(fontPath, com.itextpdf.text.pdf.BaseFont.IDENTITY_H, com.itextpdf.text.pdf.BaseFont.EMBEDDED);
    com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(bf, 22, com.itextpdf.text.Font.BOLD, new com.itextpdf.text.BaseColor(0, 102, 204));
    com.itextpdf.text.Font labelFont = new com.itextpdf.text.Font(bf, 12, com.itextpdf.text.Font.BOLD);
    com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(bf, 12);

        com.itextpdf.text.Document document = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A4, 40, 40, 40, 40);
        com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(filePath));
        document.open();

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

        com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(7); // Sửa thành 7 cột
table.setWidths(new float[]{1.2f, 3.5f, 1.5f, 2f, 2f, 2f, 2f}); // Sửa lại số phần tử cho đúng 7 cột
table.setWidthPercentage(100);


        addHeaderCell(table, "Số lượng", labelFont);
        addHeaderCell(table, "Nội dung", labelFont);
        addHeaderCell(table, "Đơn vị", labelFont);
        addHeaderCell(table, "Số điện", labelFont);    
        addHeaderCell(table, "Khối nước", labelFont);  
        addHeaderCell(table, "Đơn giá", labelFont);
        addHeaderCell(table, "Thành tiền", labelFont);



Font cellFont = new Font(bf, 12, Font.NORMAL);

        for (InvoiceItem item : items) {
    addCell(table, String.valueOf(item.soLuong), cellFont); 
    addCell(table, item.tenDichVu, cellFont);               
    addCell(table, item.donVi, cellFont);                   
    addCell(table, "Tiền điện".equals(item.tenDichVu) ? String.valueOf(item.soDien) : "", cellFont);      
    addCell(table, "Tiền nước".equals(item.tenDichVu) ? String.valueOf(item.khoiNuoc) : "", cellFont);  
    addCell(table, String.format("%,.0f", item.donGia), cellFont);    
    addCell(table, String.format("%,.0f", item.thanhTien), cellFont); 
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
    public int soLuong = 1;
    public int soDien = 0; 
    public String donVi;
    public double donGia;
    public double thanhTien;
public int khoiNuoc;
    public InvoiceItem(String tenDichVu, int soLuong, String donVi, int soDien, int khoiNuoc, double donGia) {
        this.tenDichVu = tenDichVu;
        this.soLuong = soLuong;
        this.donVi = donVi;
        this.soDien = soDien;
        this.khoiNuoc = khoiNuoc;
        this.donGia = donGia;
        this.thanhTien = 0.0;
    }
}
}