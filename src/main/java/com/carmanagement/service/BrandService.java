package com.carmanagement.service;

import com.carmanagement.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BrandService {
    List<Brand> findAllActive();
    Page<Brand> findAll(String keyword, Pageable pageable);
    Brand findById(Long id);
    Brand save(Brand brand);
    void deleteById(Long id);
}
