package com.carmanagement.service.impl;

import com.carmanagement.entity.CarModel;
import com.carmanagement.enums.CarType;
import com.carmanagement.exception.ResourceNotFoundException;
import com.carmanagement.repository.CarModelRepository;
import com.carmanagement.service.CarModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CarModelServiceImpl implements CarModelService {

    private final CarModelRepository carModelRepository;

    @Override
    public List<CarModel> findByBrand(Long brandId) {
        return carModelRepository.findByBrandIdAndActiveTrue(brandId);
    }

    @Override
    public Page<CarModel> search(Long brandId, CarType carType, Integer year, Pageable pageable) {
        return carModelRepository.search(brandId, carType, year, pageable);
    }

    @Override
    public CarModel findById(Long id) {
        return carModelRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("CarModel", id));
    }

    @Override
    @Transactional
    public CarModel save(CarModel model) {
        return carModelRepository.save(model);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        CarModel model = findById(id);
        model.setActive(false);
        carModelRepository.save(model);
    }
}
