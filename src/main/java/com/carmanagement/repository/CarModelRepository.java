package com.carmanagement.repository;

import com.carmanagement.entity.CarModel;
import com.carmanagement.enums.CarType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarModelRepository extends JpaRepository<CarModel, Long> {

    List<CarModel> findByBrandIdAndActiveTrue(Long brandId);

    @Query("""
        SELECT m FROM CarModel m JOIN FETCH m.brand
        WHERE (:brandId IS NULL OR m.brand.id = :brandId)
          AND (:carType IS NULL OR m.carType = :carType)
          AND (:year IS NULL OR m.year = :year)
          AND m.active = true
        """)
    Page<CarModel> search(@Param("brandId") Long brandId,
                          @Param("carType") CarType carType,
                          @Param("year") Integer year,
                          Pageable pageable);
}
