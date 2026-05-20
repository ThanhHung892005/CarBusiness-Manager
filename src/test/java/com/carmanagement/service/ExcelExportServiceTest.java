package com.carmanagement.service;

import com.carmanagement.entity.Brand;
import com.carmanagement.entity.CarModel;
import com.carmanagement.entity.Showroom;
import com.carmanagement.entity.Vehicle;
import com.carmanagement.enums.CarType;
import com.carmanagement.enums.VehicleStatus;
import com.carmanagement.repository.OrderRepository;
import com.carmanagement.repository.VehicleRepository;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExcelExportServiceTest {

    @Mock VehicleRepository vehicleRepository;
    @Mock OrderRepository orderRepository;

    @InjectMocks ExcelExportService excelExportService;

    // ── exportVehicleInventory ────────────────────────────────────────────────

    @Test
    void exportVehicleInventory_emptyList_returnsValidExcel() throws Exception {
        when(vehicleRepository.findAllWithDetails()).thenReturn(List.of());

        byte[] result = excelExportService.exportVehicleInventory();

        assertThat(result).isNotNull().isNotEmpty();
        try (XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(result))) {
            assertThat(wb.getNumberOfSheets()).isEqualTo(1);
            assertThat(wb.getSheetAt(0).getSheetName()).isEqualTo("Vehicle Inventory");
        }
    }

    @Test
    void exportVehicleInventory_withVehicles_containsDataRows() throws Exception {
        Brand brand = Brand.builder().id(1L).name("Toyota").build();
        CarModel model = CarModel.builder()
            .id(1L).name("Camry").year(2024).carType(CarType.SEDAN).brand(brand).build();
        Showroom showroom = Showroom.builder().id(1L).name("HCM Showroom").build();
        Vehicle vehicle = Vehicle.builder()
            .id(1L).vin("VN1234567890123456")
            .carModel(model).color("White").showroom(showroom)
            .importPrice(BigDecimal.valueOf(900_000_000))
            .sellingPrice(BigDecimal.valueOf(1_200_000_000))
            .status(VehicleStatus.AVAILABLE).build();

        when(vehicleRepository.findAllWithDetails()).thenReturn(List.of(vehicle));

        byte[] result = excelExportService.exportVehicleInventory();

        assertThat(result).isNotEmpty();
        try (XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(result))) {
            var sheet = wb.getSheetAt(0);
            // row 0 = title, row 1 = header, row 2 = first data row
            assertThat(sheet.getLastRowNum()).isGreaterThanOrEqualTo(2);
            assertThat(sheet.getRow(2).getCell(1).getStringCellValue())
                .isEqualTo("VN1234567890123456");
            assertThat(sheet.getRow(2).getCell(2).getStringCellValue())
                .isEqualTo("Toyota");
        }
    }

    @Test
    void exportVehicleInventory_vehicleWithoutShowroom_usesPlaceholder() throws Exception {
        Brand brand = Brand.builder().id(1L).name("Honda").build();
        CarModel model = CarModel.builder()
            .id(1L).name("Civic").year(2024).carType(CarType.SEDAN).brand(brand).build();
        Vehicle vehicle = Vehicle.builder()
            .id(1L).vin("VN1234567890123456")
            .carModel(model).color("Black")
            .importPrice(BigDecimal.valueOf(700_000_000))
            .sellingPrice(BigDecimal.valueOf(900_000_000))
            .status(VehicleStatus.AVAILABLE).build();

        when(vehicleRepository.findAllWithDetails()).thenReturn(List.of(vehicle));

        byte[] result = excelExportService.exportVehicleInventory();

        assertThat(result).isNotEmpty();
        try (XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(result))) {
            String showroomCell = wb.getSheetAt(0).getRow(2).getCell(10).getStringCellValue();
            assertThat(showroomCell).isEqualTo("-");
        }
    }

    @Test
    void exportVehicleInventory_multipleVehicles_correctRowCount() throws Exception {
        Brand brand = Brand.builder().id(1L).name("BMW").build();
        CarModel model = CarModel.builder()
            .id(1L).name("320i").year(2024).carType(CarType.SEDAN).brand(brand).build();

        List<Vehicle> vehicles = List.of(
            Vehicle.builder().id(1L).vin("VN0000000000000001").carModel(model).color("White")
                .importPrice(BigDecimal.valueOf(1_500_000_000))
                .sellingPrice(BigDecimal.valueOf(1_800_000_000))
                .status(VehicleStatus.AVAILABLE).build(),
            Vehicle.builder().id(2L).vin("VN0000000000000002").carModel(model).color("Black")
                .importPrice(BigDecimal.valueOf(1_500_000_000))
                .sellingPrice(BigDecimal.valueOf(1_800_000_000))
                .status(VehicleStatus.SOLD).build()
        );

        when(vehicleRepository.findAllWithDetails()).thenReturn(vehicles);

        byte[] result = excelExportService.exportVehicleInventory();

        try (XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(result))) {
            // row 0 = title, row 1 = header, row 2-3 = data, row 4 = blank, row 5 = summary
            assertThat(wb.getSheetAt(0).getLastRowNum()).isGreaterThanOrEqualTo(3);
        }
    }

    // ── exportMonthlyRevenue ──────────────────────────────────────────────────

    @Test
    void exportMonthlyRevenue_emptyData_returnsValidExcel() throws Exception {
        when(orderRepository.findMonthlyRevenue(2024)).thenReturn(List.of());

        byte[] result = excelExportService.exportMonthlyRevenue(2024);

        assertThat(result).isNotNull().isNotEmpty();
        try (XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(result))) {
            assertThat(wb.getSheetAt(0).getSheetName()).isEqualTo("Monthly Revenue 2024");
        }
    }

    @Test
    void exportMonthlyRevenue_withData_showsCorrectRevenue() throws Exception {
        // Month 3 (March) has 500M revenue
        List<Object[]> data = new java.util.ArrayList<>();
        data.add(new Object[]{3, new BigDecimal("500000000")});
        when(orderRepository.findMonthlyRevenue(2024)).thenReturn(data);

        byte[] result = excelExportService.exportMonthlyRevenue(2024);

        assertThat(result).isNotEmpty();
        try (XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(result))) {
            var sheet = wb.getSheetAt(0);
            // row 0 = title, row 1 = header, row 2 = January, row 4 = March (index 3)
            double marchRevenue = sheet.getRow(4).getCell(1).getNumericCellValue();
            assertThat(marchRevenue).isEqualTo(500_000_000.0);
        }
    }

    @Test
    void exportMonthlyRevenue_12months_has12DataRows() throws Exception {
        when(orderRepository.findMonthlyRevenue(2024)).thenReturn(List.of());

        byte[] result = excelExportService.exportMonthlyRevenue(2024);

        try (XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(result))) {
            var sheet = wb.getSheetAt(0);
            // row 0 = title, row 1 = header, row 2-13 = 12 months, row 14 = total
            assertThat(sheet.getLastRowNum()).isEqualTo(14);
        }
    }
}
