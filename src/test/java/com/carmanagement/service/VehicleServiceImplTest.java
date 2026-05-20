package com.carmanagement.service;

import com.carmanagement.dto.request.VehicleCreateRequest;
import com.carmanagement.dto.request.VehicleSearchRequest;
import com.carmanagement.entity.CarModel;
import com.carmanagement.entity.Showroom;
import com.carmanagement.entity.Vehicle;
import com.carmanagement.enums.VehicleStatus;
import com.carmanagement.exception.BusinessException;
import com.carmanagement.exception.ResourceNotFoundException;
import com.carmanagement.repository.CarModelRepository;
import com.carmanagement.repository.ShowroomRepository;
import com.carmanagement.repository.VehicleRepository;
import com.carmanagement.service.impl.VehicleServiceImpl;
import com.carmanagement.util.FileUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceImplTest {

    @Mock VehicleRepository vehicleRepository;
    @Mock CarModelRepository carModelRepository;
    @Mock ShowroomRepository showroomRepository;
    @Mock FileUtil fileUtil;

    @InjectMocks VehicleServiceImpl vehicleService;

    // ── findById ──────────────────────────────────────────────────────────────

    @Test
    void findById_found_returnsVehicle() {
        Vehicle vehicle = Vehicle.builder().id(1L).vin("VN12345678901234567").build();
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        Vehicle result = vehicleService.findById(1L);

        assertThat(result.getVin()).isEqualTo("VN12345678901234567");
    }

    @Test
    void findById_notFound_throwsResourceNotFoundException() {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.findById(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vehicle");
    }

    // ── search ────────────────────────────────────────────────────────────────

    @Test
    void search_delegatesToRepository() {
        Page<Vehicle> page = new PageImpl<>(List.of());
        when(vehicleRepository.search(any(), any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(page);

        VehicleSearchRequest req = new VehicleSearchRequest();
        req.setPage(0);
        req.setSize(15);

        Page<Vehicle> result = vehicleService.search(req);

        assertThat(result).isNotNull();
        verify(vehicleRepository).search(
            isNull(), isNull(), eq(VehicleStatus.AVAILABLE), isNull(), isNull(), isNull(), isNull(), any()
        );
    }

    // ── create ────────────────────────────────────────────────────────────────

    @Test
    void create_duplicateVin_throwsBusinessException() {
        when(vehicleRepository.existsByVin("DUPLICATE12345678")).thenReturn(true);

        VehicleCreateRequest req = new VehicleCreateRequest();
        req.setVin("DUPLICATE12345678");
        req.setCarModelId(1L);

        assertThatThrownBy(() -> vehicleService.create(req))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("VIN đã tồn tại");
    }

    @Test
    void create_carModelNotFound_throwsResourceNotFoundException() {
        when(vehicleRepository.existsByVin(any())).thenReturn(false);
        when(carModelRepository.findById(99L)).thenReturn(Optional.empty());

        VehicleCreateRequest req = new VehicleCreateRequest();
        req.setVin("VN12345678901234567");
        req.setCarModelId(99L);

        assertThatThrownBy(() -> vehicleService.create(req))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("CarModel");
    }

    @Test
    void create_success_setsStatusAvailable() {
        CarModel model = CarModel.builder().id(1L).name("Camry").build();
        Vehicle saved = Vehicle.builder().id(10L).status(VehicleStatus.AVAILABLE).build();

        when(vehicleRepository.existsByVin("VN12345678901234567")).thenReturn(false);
        when(carModelRepository.findById(1L)).thenReturn(Optional.of(model));
        when(vehicleRepository.save(any())).thenReturn(saved);

        VehicleCreateRequest req = new VehicleCreateRequest();
        req.setVin("VN12345678901234567");
        req.setCarModelId(1L);
        req.setColor("White");
        req.setImportPrice(BigDecimal.valueOf(900_000_000));
        req.setSellingPrice(BigDecimal.valueOf(1_200_000_000));

        Vehicle result = vehicleService.create(req);

        assertThat(result.getStatus()).isEqualTo(VehicleStatus.AVAILABLE);
        verify(vehicleRepository).save(argThat(v ->
            v.getVin().equals("VN12345678901234567") && v.getStatus() == VehicleStatus.AVAILABLE
        ));
    }

    @Test
    void create_withShowroom_attachesShowroom() {
        CarModel model = CarModel.builder().id(1L).build();
        Showroom showroom = Showroom.builder().id(5L).name("HCM Showroom").build();
        Vehicle saved = Vehicle.builder().id(10L).showroom(showroom).build();

        when(vehicleRepository.existsByVin(any())).thenReturn(false);
        when(carModelRepository.findById(1L)).thenReturn(Optional.of(model));
        when(showroomRepository.findById(5L)).thenReturn(Optional.of(showroom));
        when(vehicleRepository.save(any())).thenReturn(saved);

        VehicleCreateRequest req = new VehicleCreateRequest();
        req.setVin("VN12345678901234567");
        req.setCarModelId(1L);
        req.setShowroomId(5L);
        req.setColor("Black");
        req.setImportPrice(BigDecimal.valueOf(900_000_000));
        req.setSellingPrice(BigDecimal.valueOf(1_200_000_000));

        vehicleService.create(req);

        verify(showroomRepository).findById(5L);
        verify(vehicleRepository).save(argThat(v -> v.getShowroom() != null));
    }

    // ── update ────────────────────────────────────────────────────────────────

    @Test
    void update_notFound_throwsResourceNotFoundException() {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.update(99L, new VehicleCreateRequest()))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_success_updatesFields() {
        Vehicle existing = Vehicle.builder()
            .id(1L).color("White").sellingPrice(BigDecimal.valueOf(1_000_000_000)).build();
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(vehicleRepository.save(any())).thenReturn(existing);

        VehicleCreateRequest req = new VehicleCreateRequest();
        req.setColor("Red");
        req.setImportPrice(BigDecimal.valueOf(900_000_000));
        req.setSellingPrice(BigDecimal.valueOf(1_100_000_000));

        vehicleService.update(1L, req);

        assertThat(existing.getColor()).isEqualTo("Red");
        assertThat(existing.getSellingPrice()).isEqualByComparingTo(BigDecimal.valueOf(1_100_000_000));
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Test
    void delete_soldVehicle_throwsBusinessException() {
        Vehicle vehicle = Vehicle.builder().id(1L).status(VehicleStatus.SOLD).build();
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> vehicleService.delete(1L))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("đã được bán");
    }

    @Test
    void delete_availableVehicle_callsRepositoryDelete() {
        Vehicle vehicle = Vehicle.builder().id(1L).status(VehicleStatus.AVAILABLE).build();
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        vehicleService.delete(1L);

        verify(vehicleRepository).delete(vehicle);
    }

    @Test
    void delete_reservedVehicle_callsRepositoryDelete() {
        Vehicle vehicle = Vehicle.builder().id(1L).status(VehicleStatus.RESERVED).build();
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        vehicleService.delete(1L);

        verify(vehicleRepository).delete(vehicle);
    }
}
