package com.roommanagement.notification;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;

public class InvoiceService {
    public void exportInvoiceToPDF(
            String filePath,
            String tenant,
            String room,
            String phone,
            String address,
            String rent,
            String electricity,
            String service,
            String internet,
            String total
    ) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, new BaseColor(60, 60, 120));
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);

        Paragraph title = new Paragraph("HÓA ĐƠN THANH TOÁN", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(18f);
        document.add(title);

        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidths(new float[]{1.2f, 3.5f});
        infoTable.setWidthPercentage(60);
        infoTable.setHorizontalAlignment(Element.ALIGN_LEFT);
        infoTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        infoTable.addCell(getCell("Người thuê:", labelFont));
        infoTable.addCell(getCell(tenant, normalFont));
        infoTable.addCell(getCell("Phòng:", labelFont));
        infoTable.addCell(getCell(room, normalFont));
        infoTable.addCell(getCell("SĐT:", labelFont));
        infoTable.addCell(getCell(phone, normalFont));
        infoTable.addCell(getCell("Địa chỉ thuê:", labelFont));
        infoTable.addCell(getCell(address, normalFont));
        infoTable.setSpacingAfter(18f);
        document.add(infoTable);

        PdfPTable table = new PdfPTable(2);
        table.setWidths(new float[]{3.5f, 2f});
        table.setWidthPercentage(60);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        addHeaderCell(table, "Khoản mục", labelFont);
        addHeaderCell(table, "Số tiền (VNĐ)", labelFont);

        addNormalCell(table, "Tiền phòng hàng tháng", normalFont, Element.ALIGN_LEFT);
        addNormalCell(table, rent, normalFont, Element.ALIGN_RIGHT);

        addNormalCell(table, "Tiền điện nước", normalFont, Element.ALIGN_LEFT);
        addNormalCell(table, electricity, normalFont, Element.ALIGN_RIGHT);

        addNormalCell(table, "Tiền dịch vụ", normalFont, Element.ALIGN_LEFT);
        addNormalCell(table, service, normalFont, Element.ALIGN_RIGHT);

        addNormalCell(table, "Tiền mạng", normalFont, Element.ALIGN_LEFT);
        addNormalCell(table, internet, normalFont, Element.ALIGN_RIGHT);

        document.add(table);

        Paragraph totalP = new Paragraph("TỔNG CỘNG: " + total + " VNĐ", labelFont);
        totalP.setAlignment(Element.ALIGN_RIGHT);
        totalP.setSpacingBefore(10f);
        document.add(totalP);

        document.close();
    }

    private PdfPCell getCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(new BaseColor(230, 230, 250));
        table.addCell(cell);
    }

    private void addNormalCell(PdfPTable table, String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(align);
        table.addCell(cell);
    }
}