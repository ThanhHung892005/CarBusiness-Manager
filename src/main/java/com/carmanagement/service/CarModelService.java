package com.carmanagement.service;

import com.carmanagement.entity.CarModel;
import com.carmanagement.enums.CarType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CarModelService {
    List<CarModel> findByBrand(Long brandId);
    Page<CarModel> search(Long brandId, CarType carType, Integer year, Pageable pageable);
    CarModel findById(Long id);
    CarModel save(CarModel model);
    void deleteById(Long id);
}
