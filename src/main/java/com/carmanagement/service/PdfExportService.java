package com.carmanagement.service;

import com.carmanagement.dto.request.QuoteRequest;
import com.carmanagement.entity.Invoice;
import com.carmanagement.entity.Order;
import com.carmanagement.entity.OrderItem;
import com.carmanagement.entity.Payment;
import com.carmanagement.entity.Vehicle;
import com.carmanagement.exception.ResourceNotFoundException;
import com.carmanagement.repository.InvoiceRepository;
import com.carmanagement.repository.OrderRepository;
import com.carmanagement.repository.VehicleRepository;
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
    private final VehicleRepository vehicleRepository;

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

    @Transactional(readOnly = true)
    public byte[] exportQuotePdf(QuoteRequest req) {
        Vehicle vehicle = vehicleRepository.findWithDetailsById(req.getVehicleId())
            .orElseThrow(() -> new ResourceNotFoundException("Vehicle", req.getVehicleId()));

        BigDecimal unitPrice = vehicle.getSellingPrice();
        BigDecimal discount  = req.getDiscountAmount() != null ? req.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal total     = unitPrice.subtract(discount);
        java.time.LocalDate validUntil = java.time.LocalDate.now().plusDays(req.getValidDays() != null ? req.getValidDays() : 7);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf  = new PdfDocument(writer);
            Document doc     = new Document(pdf);
            doc.setMargins(40, 40, 40, 40);

            PdfFont bold    = loadFont("/fonts/NotoSans-Bold.ttf");
            PdfFont regular = loadFont("/fonts/NotoSans-Regular.ttf");

            // Header
            doc.add(new Paragraph("CAR MANAGEMENT SYSTEM")
                .setFont(bold).setFontSize(20).setFontColor(HEADER_BG).setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("BAO GIA XE / VEHICLE QUOTATION")
                .setFont(bold).setFontSize(14).setTextAlignment(TextAlignment.CENTER).setMarginBottom(4));
            doc.add(new Paragraph("________________________________________")
                .setFontColor(ColorConstants.GRAY).setTextAlignment(TextAlignment.CENTER).setMarginBottom(16));

            // Quote meta
            Table meta = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
            String quoteNo = "BG" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"))
                + String.format("%04d", (long)(Math.random() * 9999 + 1));
            addInfoRow(meta, bold, regular, "So Bao Gia:", quoteNo);
            addInfoRow(meta, bold, regular, "Ngay Lap:", java.time.LocalDate.now().format(DATE_FMT));
            addInfoRow(meta, bold, regular, "Hieu Luc Den:", validUntil.format(DATE_FMT));
            doc.add(meta);
            doc.add(new Paragraph(" ").setMarginBottom(8));

            // Customer info
            doc.add(new Paragraph("THONG TIN KHACH HANG")
                .setFont(bold).setFontSize(11).setFontColor(HEADER_BG).setMarginBottom(4));
            Table cust = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
            addInfoRow(cust, bold, regular, "Ho Ten:", req.getCustomerName());
            addInfoRow(cust, bold, regular, "Dien Thoai:", req.getCustomerPhone());
            if (req.getCustomerEmail() != null && !req.getCustomerEmail().isBlank()) {
                addInfoRow(cust, bold, regular, "Email:", req.getCustomerEmail());
            }
            doc.add(cust);
            doc.add(new Paragraph(" ").setMarginBottom(8));

            // Vehicle details
            doc.add(new Paragraph("THONG TIN XE")
                .setFont(bold).setFontSize(11).setFontColor(HEADER_BG).setMarginBottom(4));
            var m = vehicle.getCarModel();
            Table veh = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
            addInfoRow(veh, bold, regular, "Hang Xe:", m.getBrand().getName());
            addInfoRow(veh, bold, regular, "Dong Xe:", m.getName() + " " + m.getYear());
            addInfoRow(veh, bold, regular, "Mau Sac:", vehicle.getColor());
            addInfoRow(veh, bold, regular, "VIN:", vehicle.getVin());
            doc.add(veh);
            doc.add(new Paragraph(" ").setMarginBottom(8));

            // Pricing table
            doc.add(new Paragraph("BANG GIA")
                .setFont(bold).setFontSize(11).setFontColor(HEADER_BG).setMarginBottom(4));
            Table pricing = new Table(UnitValue.createPercentArray(new float[]{60, 40})).useAllAvailableWidth();
            addTotalRow(pricing, bold, regular, "Gia Niem Yet (VND):", formatMoney(unitPrice), false);
            addTotalRow(pricing, bold, regular, "Chiet Khau (VND):", "- " + formatMoney(discount), false);
            addTotalRow(pricing, bold, regular, "GIA BAO (VND):", formatMoney(total), true);
            doc.add(pricing);

            if (req.getNotes() != null && !req.getNotes().isBlank()) {
                doc.add(new Paragraph(" ").setMarginBottom(8));
                doc.add(new Paragraph("Ghi chu: " + req.getNotes())
                    .setFont(regular).setFontSize(9).setFontColor(ColorConstants.GRAY));
            }

            doc.add(new Paragraph(" ").setMarginTop(16));
            doc.add(new Paragraph("Bao gia co hieu luc den het ngay " + validUntil.format(DATE_FMT)
                + ". Lien he chung toi de duoc ho tro tot nhat.")
                .setFont(regular).setFontSize(9).setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER));
            addFooter(doc, regular);

            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate quote PDF", e);
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
