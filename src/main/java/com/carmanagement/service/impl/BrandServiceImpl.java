package com.carmanagement.service.impl;

import com.carmanagement.entity.Brand;
import com.carmanagement.exception.ResourceNotFoundException;
import com.carmanagement.repository.BrandRepository;
import com.carmanagement.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    @Override
    public List<Brand> findAllActive() {
        return brandRepository.findByActiveTrue();
    }

    @Override
    public Page<Brand> findAll(String keyword, Pageable pageable) {
        if (StringUtils.hasText(keyword)) {
            return brandRepository.findByNameContainingIgnoreCase(keyword, pageable);
        }
        return brandRepository.findAll(pageable);
    }

    @Override
    public Brand findById(Long id) {
        return brandRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Brand", id));
    }

    @Override
    @Transactional
    public Brand save(Brand brand) {
        return brandRepository.save(brand);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Brand brand = findById(id);
        brand.setActive(false);
        brandRepository.save(brand);
    }
}
