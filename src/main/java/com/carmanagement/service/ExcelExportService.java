package com.carmanagement.service;

import com.carmanagement.entity.Vehicle;
import com.carmanagement.repository.OrderRepository;
import com.carmanagement.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelExportService {

    private final VehicleRepository vehicleRepository;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public byte[] exportVehicleInventory() {
        List<Vehicle> vehicles = vehicleRepository.findAllWithDetails();

        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            XSSFSheet sheet = wb.createSheet("Vehicle Inventory");
            sheet.setDefaultColumnWidth(18);

            CellStyle headerStyle = createHeaderStyle(wb);
            CellStyle moneyStyle = createMoneyStyle(wb);
            CellStyle altStyle = createAltRowStyle(wb);
            CellStyle altMoneyStyle = createAltMoneyStyle(wb);

            // Title row
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("VEHICLE INVENTORY REPORT — " + LocalDate.now());
            CellStyle titleStyle = wb.createCellStyle();
            XSSFFont titleFont = wb.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleFont.setColor(new XSSFColor(new byte[]{(byte) 41, (byte) 128, (byte) 185}, null));
            titleStyle.setFont(titleFont);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 9));

            // Header row
            String[] headers = {
                "No.", "VIN", "Brand", "Model", "Year", "Type",
                "Color", "Import Price (VND)", "Selling Price (VND)", "Status", "Showroom", "Import Date"
            };
            sheet.setDefaultColumnWidth(15);
            Row headerRow = sheet.createRow(1);
            for (int i = 0; i < headers.length; i++) {
                Cell c = headerRow.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }
            sheet.setColumnWidth(1, 20 * 256);
            sheet.setColumnWidth(2, 18 * 256);
            sheet.setColumnWidth(3, 20 * 256);
            sheet.setColumnWidth(7, 22 * 256);
            sheet.setColumnWidth(8, 22 * 256);
            sheet.setColumnWidth(10, 22 * 256);

            // Data rows
            int rowIdx = 2;
            for (Vehicle v : vehicles) {
                Row row = sheet.createRow(rowIdx);
                boolean alt = (rowIdx % 2 == 0);
                CellStyle base = alt ? altStyle : null;
                CellStyle money = alt ? altMoneyStyle : moneyStyle;

                setCell(row, 0, rowIdx - 1, base);
                setCell(row, 1, v.getVin(), base);
                setCell(row, 2, v.getCarModel().getBrand().getName(), base);
                setCell(row, 3, v.getCarModel().getName(), base);
                setCell(row, 4, v.getCarModel().getYear(), base);
                setCell(row, 5, v.getCarModel().getCarType() != null ? v.getCarModel().getCarType().name() : "", base);
                setCell(row, 6, v.getColor(), base);
                setCellMoney(row, 7, v.getImportPrice(), money);
                setCellMoney(row, 8, v.getSellingPrice(), money);
                setCell(row, 9, v.getStatus().name(), base);
                setCell(row, 10, v.getShowroom() != null ? v.getShowroom().getName() : "-", base);
                setCell(row, 11, v.getImportDate() != null ? v.getImportDate().toString() : "-", base);
                rowIdx++;
            }

            // Summary
            rowIdx++;
            Row sumRow = sheet.createRow(rowIdx);
            CellStyle sumStyle = wb.createCellStyle();
            XSSFFont sumFont = wb.createFont();
            sumFont.setBold(true);
            sumStyle.setFont(sumFont);
            Cell sumLabel = sumRow.createCell(0);
            sumLabel.setCellValue("Total vehicles: " + vehicles.size());
            sumLabel.setCellStyle(sumStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowIdx, rowIdx, 0, 4));

            wb.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate inventory Excel", e);
        }
    }

    @Transactional(readOnly = true)
    public byte[] exportMonthlyRevenue(int year) {
        List<Object[]> rows = orderRepository.findMonthlyRevenue(year);

        String[] monthNames = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };

        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            XSSFSheet sheet = wb.createSheet("Monthly Revenue " + year);
            sheet.setDefaultColumnWidth(20);

            CellStyle headerStyle = createHeaderStyle(wb);
            CellStyle moneyStyle = createMoneyStyle(wb);
            CellStyle altMoneyStyle = createAltMoneyStyle(wb);
            CellStyle altStyle = createAltRowStyle(wb);

            // Title
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("MONTHLY REVENUE REPORT — " + year);
            CellStyle titleStyle = wb.createCellStyle();
            XSSFFont titleFont = wb.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleFont.setColor(new XSSFColor(new byte[]{(byte) 41, (byte) 128, (byte) 185}, null));
            titleStyle.setFont(titleFont);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 2));

            // Header
            Row hdr = sheet.createRow(1);
            for (int i = 0; i < 3; i++) {
                Cell c = hdr.createCell(i);
                c.setCellStyle(headerStyle);
            }
            hdr.getCell(0).setCellValue("Month");
            hdr.getCell(1).setCellValue("Revenue (VND)");
            hdr.getCell(2).setCellValue("% of Total");

            // Build month map
            BigDecimal[] revenues = new BigDecimal[12];
            for (int m = 0; m < 12; m++) revenues[m] = BigDecimal.ZERO;
            for (Object[] r : rows) {
                int month = ((Number) r[0]).intValue();
                BigDecimal rev = new BigDecimal(r[1].toString());
                revenues[month - 1] = rev;
            }

            BigDecimal total = BigDecimal.ZERO;
            for (BigDecimal rev : revenues) total = total.add(rev);

            for (int m = 0; m < 12; m++) {
                Row row = sheet.createRow(m + 2);
                boolean alt = (m % 2 == 0);
                CellStyle base = alt ? altStyle : null;
                CellStyle money = alt ? altMoneyStyle : moneyStyle;

                setCell(row, 0, monthNames[m], base);
                setCellMoney(row, 1, revenues[m], money);

                Cell pctCell = row.createCell(2);
                double pct = total.compareTo(BigDecimal.ZERO) == 0 ? 0
                    : revenues[m].doubleValue() / total.doubleValue() * 100;
                pctCell.setCellValue(String.format("%.1f%%", pct));
                if (base != null) pctCell.setCellStyle(base);
            }

            // Total row
            Row totalRow = sheet.createRow(14);
            CellStyle boldStyle = wb.createCellStyle();
            XSSFFont boldFont = wb.createFont();
            boldFont.setBold(true);
            boldStyle.setFont(boldFont);
            Cell totalLabel = totalRow.createCell(0);
            totalLabel.setCellValue("TOTAL");
            totalLabel.setCellStyle(boldStyle);
            CellStyle totalMoneyStyle = wb.createCellStyle();
            totalMoneyStyle.cloneStyleFrom(moneyStyle);
            totalMoneyStyle.setFont(boldFont);
            setCellMoney(totalRow, 1, total, totalMoneyStyle);
            Cell pctTotal = totalRow.createCell(2);
            pctTotal.setCellValue("100.0%");
            pctTotal.setCellStyle(boldStyle);

            wb.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate revenue Excel", e);
        }
    }

    private CellStyle createHeaderStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 41, (byte) 128, (byte) 185}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    private CellStyle createMoneyStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        DataFormat fmt = wb.createDataFormat();
        style.setDataFormat(fmt.getFormat("#,##0"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private CellStyle createAltRowStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 235, (byte) 245, (byte) 255}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createAltMoneyStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        DataFormat fmt = wb.createDataFormat();
        style.setDataFormat(fmt.getFormat("#,##0"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 235, (byte) 245, (byte) 255}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private void setCell(Row row, int col, Object value, CellStyle style) {
        Cell c = row.createCell(col);
        if (value instanceof Number) c.setCellValue(((Number) value).doubleValue());
        else c.setCellValue(value != null ? value.toString() : "");
        if (style != null) c.setCellStyle(style);
    }

    private void setCellMoney(Row row, int col, BigDecimal value, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(value != null ? value.doubleValue() : 0);
        if (style != null) c.setCellStyle(style);
    }
}
