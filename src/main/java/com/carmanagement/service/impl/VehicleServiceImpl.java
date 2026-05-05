package com.carmanagement.service.impl;

import com.carmanagement.dto.request.VehicleCreateRequest;
import com.carmanagement.dto.request.VehicleSearchRequest;
import com.carmanagement.entity.Vehicle;
import com.carmanagement.entity.VehicleImage;
import com.carmanagement.enums.VehicleStatus;
import com.carmanagement.exception.BusinessException;
import com.carmanagement.exception.ResourceNotFoundException;
import com.carmanagement.repository.CarModelRepository;
import com.carmanagement.repository.ShowroomRepository;
import com.carmanagement.repository.VehicleRepository;
import com.carmanagement.service.VehicleService;
import com.carmanagement.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CarModelRepository carModelRepository;
    private final ShowroomRepository showroomRepository;
    private final FileUtil fileUtil;

    @Override
    public Page<Vehicle> search(VehicleSearchRequest req) {
        return vehicleRepository.search(
            req.getBrandId(), req.getModelId(), req.getStatus(),
            req.getShowroomId(), req.getColor(), req.getMinPrice(), req.getMaxPrice(),
            PageRequest.of(req.getPage(), req.getSize())
        );
    }

    @Override
    public Vehicle findById(Long id) {
        return vehicleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id));
    }

    @Override
    public Vehicle findWithDetailsById(Long id) {
        return vehicleRepository.findWithDetailsById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id));
    }

    @Override
    @Transactional
    public Vehicle create(VehicleCreateRequest req) {
        if (vehicleRepository.existsByVin(req.getVin())) {
            throw new BusinessException("VIN đã tồn tại trong hệ thống: " + req.getVin());
        }
        var model = carModelRepository.findById(req.getCarModelId())
            .orElseThrow(() -> new ResourceNotFoundException("CarModel", req.getCarModelId()));

        Vehicle vehicle = Vehicle.builder()
            .vin(req.getVin())
            .carModel(model)
            .color(req.getColor())
            .colorCode(req.getColorCode())
            .importPrice(req.getImportPrice())
            .sellingPrice(req.getSellingPrice())
            .importDate(req.getImportDate())
            .notes(req.getNotes())
            .status(VehicleStatus.AVAILABLE)
            .build();

        if (req.getShowroomId() != null) {
            vehicle.setShowroom(showroomRepository.findById(req.getShowroomId())
                .orElseThrow(() -> new ResourceNotFoundException("Showroom", req.getShowroomId())));
        }

        return vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional
    public Vehicle update(Long id, VehicleCreateRequest req) {
        Vehicle vehicle = findById(id);
        vehicle.setColor(req.getColor());
        vehicle.setColorCode(req.getColorCode());
        vehicle.setImportPrice(req.getImportPrice());
        vehicle.setSellingPrice(req.getSellingPrice());
        vehicle.setImportDate(req.getImportDate());
        vehicle.setNotes(req.getNotes());

        if (req.getShowroomId() != null) {
            vehicle.setShowroom(showroomRepository.findById(req.getShowroomId())
                .orElseThrow(() -> new ResourceNotFoundException("Showroom", req.getShowroomId())));
        }

        return vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Vehicle vehicle = findById(id);
        if (vehicle.getStatus() == VehicleStatus.SOLD) {
            throw new BusinessException("Không thể xóa xe đã được bán");
        }
        vehicleRepository.delete(vehicle);
    }

    @Override
    @Transactional
    public void uploadImages(Long vehicleId, MultipartFile[] files) {
        Vehicle vehicle = findById(vehicleId);
        boolean hasImages = !vehicle.getImages().isEmpty();

        for (int i = 0; i < files.length; i++) {
            String url = fileUtil.saveVehicleImage(files[i]);
            VehicleImage image = VehicleImage.builder()
                .vehicle(vehicle)
                .url(url)
                .isPrimary(!hasImages && i == 0)
                .sortOrder(vehicle.getImages().size() + i)
                .build();
            vehicle.getImages().add(image);
        }

        vehicleRepository.save(vehicle);
    }
}
