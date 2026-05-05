package com.carmanagement.service;

import com.carmanagement.entity.Invoice;
import com.carmanagement.entity.Order;
import com.carmanagement.entity.OrderItem;
import com.carmanagement.entity.Payment;
import com.carmanagement.exception.ResourceNotFoundException;
import com.carmanagement.repository.InvoiceRepository;
import com.carmanagement.repository.OrderRepository;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PdfExportService {

    private final OrderRepository orderRepository;
    private final InvoiceRepository invoiceRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DeviceRgb HEADER_BG = new DeviceRgb(41, 128, 185);
    private static final DeviceRgb ALT_ROW_BG = new DeviceRgb(235, 245, 255);

    @Transactional(readOnly = true)
    public byte[] exportInvoice(Long orderId) {
        Order order = orderRepository.findWithDetailsById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        Invoice invoice = invoiceRepository.findByOrderId(orderId).orElse(null);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);
            doc.setMargins(40, 40, 40, 40);

            PdfFont bold = loadFont("/fonts/NotoSans-Bold.ttf");
            PdfFont regular = loadFont("/fonts/NotoSans-Regular.ttf");

            addHeader(doc, bold, regular);
            addOrderInfo(doc, bold, regular, order, invoice);
            addCustomerInfo(doc, bold, regular, order);
            addItemsTable(doc, bold, regular, order.getOrderItems());
            addTotalsSection(doc, bold, regular, order, invoice);
            if (invoice != null && !invoice.getPayments().isEmpty()) {
                addPaymentsTable(doc, bold, regular, invoice.getPayments());
            }
            addFooter(doc, regular);

            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate invoice PDF", e);
        }
    }

    private void addHeader(Document doc, PdfFont bold, PdfFont regular) throws Exception {
        Paragraph title = new Paragraph("CAR MANAGEMENT SYSTEM")
            .setFont(bold).setFontSize(20)
            .setFontColor(HEADER_BG)
            .setTextAlignment(TextAlignment.CENTER);
        doc.add(title);

        doc.add(new Paragraph("SALES INVOICE / HOA DON BAN HANG")
            .setFont(bold).setFontSize(14)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(4));

        doc.add(new Paragraph("________________________________________")
            .setFontColor(ColorConstants.GRAY)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(16));
    }

    private void addOrderInfo(Document doc, PdfFont bold, PdfFont regular, Order order, Invoice invoice) throws Exception {
        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();

        String invoiceCode = invoice != null ? invoice.getInvoiceCode() : "N/A";
        String issuedDate = invoice != null ? invoice.getIssuedDate().format(DATE_FMT) : "-";
        String invoiceStatus = invoice != null ? invoice.getStatus().name() : "-";

        addInfoRow(infoTable, bold, regular, "Invoice No:", invoiceCode);
        addInfoRow(infoTable, bold, regular, "Order Code:", order.getOrderCode());
        addInfoRow(infoTable, bold, regular, "Order Date:", order.getOrderDate().format(DATETIME_FMT));
        addInfoRow(infoTable, bold, regular, "Issued Date:", issuedDate);
        addInfoRow(infoTable, bold, regular, "Status:", invoiceStatus);
        if (order.getShowroom() != null) {
            addInfoRow(infoTable, bold, regular, "Showroom:", order.getShowroom().getName());
        }
        doc.add(infoTable);
        doc.add(new Paragraph(" ").setMarginBottom(8));
    }

    private void addCustomerInfo(Document doc, PdfFont bold, PdfFont regular, Order order) throws Exception {
        doc.add(new Paragraph("CUSTOMER INFORMATION")
            .setFont(bold).setFontSize(11)
            .setFontColor(HEADER_BG)
            .setMarginBottom(4));

        Table t = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
        var customer = order.getCustomer();
        addInfoRow(t, bold, regular, "Customer Code:", customer.getCustomerCode());
        addInfoRow(t, bold, regular, "Full Name:", customer.getFullName());
        addInfoRow(t, bold, regular, "Phone:", customer.getPhone());
        if (customer.getEmail() != null) addInfoRow(t, bold, regular, "Email:", customer.getEmail());
        if (customer.getAddress() != null) addInfoRow(t, bold, regular, "Address:", customer.getAddress());
        doc.add(t);
        doc.add(new Paragraph(" ").setMarginBottom(8));
    }

    private void addItemsTable(Document doc, PdfFont bold, PdfFont regular, List<OrderItem> items) throws Exception {
        doc.add(new Paragraph("VEHICLE DETAILS")
            .setFont(bold).setFontSize(11)
            .setFontColor(HEADER_BG)
            .setMarginBottom(4));

        Table t = new Table(UnitValue.createPercentArray(new float[]{5, 20, 20, 15, 15, 15, 10})).useAllAvailableWidth();

        String[] headers = {"#", "Brand", "Model", "VIN", "Unit Price (VND)", "Discount (VND)", "Subtotal (VND)"};
        for (String h : headers) {
            t.addHeaderCell(new Cell().add(new Paragraph(h).setFont(bold).setFontSize(9))
                .setBackgroundColor(HEADER_BG)
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER));
        }

        int i = 1;
        for (OrderItem item : items) {
            boolean alt = (i % 2 == 0);
            var v = item.getVehicle();
            var m = v.getCarModel();
            addTableCell(t, bold, regular, String.valueOf(i++), TextAlignment.CENTER, alt);
            addTableCell(t, bold, regular, m.getBrand().getName(), TextAlignment.LEFT, alt);
            addTableCell(t, bold, regular, m.getName() + " " + m.getYear(), TextAlignment.LEFT, alt);
            addTableCell(t, bold, regular, v.getVin(), TextAlignment.LEFT, alt);
            addTableCell(t, bold, regular, formatMoney(item.getUnitPrice()), TextAlignment.RIGHT, alt);
            addTableCell(t, bold, regular, formatMoney(item.getDiscount()), TextAlignment.RIGHT, alt);
            addTableCell(t, bold, regular, formatMoney(item.getLineTotal()), TextAlignment.RIGHT, alt);
        }
        doc.add(t);
        doc.add(new Paragraph(" ").setMarginBottom(8));
    }

    private void addTotalsSection(Document doc, PdfFont bold, PdfFont regular, Order order, Invoice invoice) throws Exception {
        Table t = new Table(UnitValue.createPercentArray(new float[]{70, 30})).useAllAvailableWidth();

        addTotalRow(t, bold, regular, "Subtotal:", formatMoney(order.getSubtotal()), false);
        addTotalRow(t, bold, regular, "Discount:", "- " + formatMoney(order.getDiscountAmount()), false);
        addTotalRow(t, bold, regular, "TOTAL AMOUNT (VND):", formatMoney(order.getTotalAmount()), true);
        if (invoice != null) {
            addTotalRow(t, bold, regular, "Paid Amount:", formatMoney(invoice.getPaidAmount()), false);
            addTotalRow(t, bold, regular, "Remaining:", formatMoney(invoice.getRemaining()), false);
        }
        doc.add(t);
        doc.add(new Paragraph(" ").setMarginBottom(8));
    }

    private void addPaymentsTable(Document doc, PdfFont bold, PdfFont regular, List<Payment> payments) throws Exception {
        doc.add(new Paragraph("PAYMENT HISTORY")
            .setFont(bold).setFontSize(11)
            .setFontColor(HEADER_BG)
            .setMarginBottom(4));

        Table t = new Table(UnitValue.createPercentArray(new float[]{10, 25, 25, 20, 20})).useAllAvailableWidth();
        String[] headers = {"#", "Date", "Method", "Ref No", "Amount (VND)"};
        for (String h : headers) {
            t.addHeaderCell(new Cell().add(new Paragraph(h).setFont(bold).setFontSize(9))
                .setBackgroundColor(HEADER_BG)
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER));
        }

        int i = 1;
        for (Payment p : payments) {
            boolean alt = (i % 2 == 0);
            addTableCell(t, bold, regular, String.valueOf(i++), TextAlignment.CENTER, alt);
            addTableCell(t, bold, regular,
                p.getCreatedAt() != null ? p.getCreatedAt().format(DATETIME_FMT) : "-", TextAlignment.CENTER, alt);
            addTableCell(t, bold, regular, p.getPaymentMethod().name(), TextAlignment.CENTER, alt);
            addTableCell(t, bold, regular,
                p.getReferenceNo() != null ? p.getReferenceNo() : "-", TextAlignment.CENTER, alt);
            addTableCell(t, bold, regular, formatMoney(p.getAmount()), TextAlignment.RIGHT, alt);
        }
        doc.add(t);
    }

    private void addFooter(Document doc, PdfFont regular) throws Exception {
        doc.add(new Paragraph(" ").setMarginTop(20));
        doc.add(new Paragraph("Generated by Car Management System  |  " + java.time.LocalDateTime.now().format(DATETIME_FMT))
            .setFont(regular).setFontSize(8)
            .setFontColor(ColorConstants.GRAY)
            .setTextAlignment(TextAlignment.CENTER));
    }

    private void addInfoRow(Table t, PdfFont bold, PdfFont regular, String label, String value) {
        t.addCell(new Cell().add(new Paragraph(label).setFont(bold).setFontSize(9))
            .setBorder(null).setPaddingBottom(3));
        t.addCell(new Cell().add(new Paragraph(value != null ? value : "-").setFont(regular).setFontSize(9))
            .setBorder(null).setPaddingBottom(3));
    }

    private void addTableCell(Table t, PdfFont bold, PdfFont regular, String text, TextAlignment align, boolean alt) {
        Cell cell = new Cell()
            .add(new Paragraph(text != null ? text : "-").setFont(regular).setFontSize(9))
            .setTextAlignment(align);
        if (alt) cell.setBackgroundColor(ALT_ROW_BG);
        t.addCell(cell);
    }

    private void addTotalRow(Table t, PdfFont bold, PdfFont regular, String label, String value, boolean highlight) {
        Cell labelCell = new Cell().add(new Paragraph(label).setFont(bold).setFontSize(10))
            .setBorder(null).setTextAlignment(TextAlignment.RIGHT).setPaddingRight(8);
        Cell valueCell = new Cell().add(new Paragraph(value).setFont(bold).setFontSize(10))
            .setBorder(null).setTextAlignment(TextAlignment.RIGHT);
        if (highlight) {
            labelCell.setFontColor(HEADER_BG);
            valueCell.setFontColor(HEADER_BG);
        }
        t.addCell(labelCell);
        t.addCell(valueCell);
    }

    private PdfFont loadFont(String classpathPath) throws Exception {
        byte[] bytes = getClass().getResourceAsStream(classpathPath).readAllBytes();
        return PdfFontFactory.createFont(bytes, PdfEncodings.IDENTITY_H,
            PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) return "0";
        return NumberFormat.getNumberInstance(Locale.US).format(amount.longValue());
    }
}
