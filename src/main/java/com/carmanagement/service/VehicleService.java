package com.carmanagement.service;

import com.carmanagement.dto.request.VehicleCreateRequest;
import com.carmanagement.dto.request.VehicleSearchRequest;
import com.carmanagement.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface VehicleService {
    Page<Vehicle> search(VehicleSearchRequest request);
    Vehicle findById(Long id);
    Vehicle findWithDetailsById(Long id);
    Vehicle create(VehicleCreateRequest request);
    Vehicle update(Long id, VehicleCreateRequest request);
    void delete(Long id);
    void uploadImages(Long vehicleId, MultipartFile[] files);
}
