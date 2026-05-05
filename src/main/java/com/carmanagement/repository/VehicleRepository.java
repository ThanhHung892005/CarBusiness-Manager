package com.carmanagement.repository;

import com.carmanagement.entity.Vehicle;
import com.carmanagement.enums.VehicleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByVin(String vin);

    boolean existsByVin(String vin);

    long countByStatus(VehicleStatus status);

    long countByShowroomIdAndStatus(Long showroomId, VehicleStatus status);

    @Query(value = """
        SELECT v FROM Vehicle v
        JOIN FETCH v.carModel m
        JOIN FETCH m.brand b
        LEFT JOIN FETCH v.showroom
        WHERE (:brandId IS NULL OR b.id = :brandId)
          AND (:modelId IS NULL OR m.id = :modelId)
          AND (:status IS NULL OR v.status = :status)
          AND (:showroomId IS NULL OR v.showroom.id = :showroomId)
          AND (:color IS NULL OR LOWER(v.color) LIKE LOWER(CONCAT('%', CAST(:color AS string), '%')))
          AND (:minPrice IS NULL OR v.sellingPrice >= :minPrice)
          AND (:maxPrice IS NULL OR v.sellingPrice <= :maxPrice)
        """,
        countQuery = """
        SELECT COUNT(v) FROM Vehicle v
        JOIN v.carModel m
        JOIN m.brand b
        WHERE (:brandId IS NULL OR b.id = :brandId)
          AND (:modelId IS NULL OR m.id = :modelId)
          AND (:status IS NULL OR v.status = :status)
          AND (:showroomId IS NULL OR v.showroom.id = :showroomId)
          AND (:color IS NULL OR LOWER(v.color) LIKE LOWER(CONCAT('%', CAST(:color AS string), '%')))
          AND (:minPrice IS NULL OR v.sellingPrice >= :minPrice)
          AND (:maxPrice IS NULL OR v.sellingPrice <= :maxPrice)
        """)
    Page<Vehicle> search(@Param("brandId") Long brandId,
                         @Param("modelId") Long modelId,
                         @Param("status") VehicleStatus status,
                         @Param("showroomId") Long showroomId,
                         @Param("color") String color,
                         @Param("minPrice") BigDecimal minPrice,
                         @Param("maxPrice") BigDecimal maxPrice,
                         Pageable pageable);

    @Query("""
        SELECT v FROM Vehicle v
        JOIN FETCH v.carModel m
        JOIN FETCH m.brand
        LEFT JOIN FETCH v.showroom
        LEFT JOIN FETCH v.images
        WHERE v.id = :id
        """)
    Optional<Vehicle> findWithDetailsById(@Param("id") Long id);

    @Query("""
        SELECT v FROM Vehicle v
        JOIN FETCH v.carModel m
        JOIN FETCH m.brand
        LEFT JOIN FETCH v.showroom
        ORDER BY v.status, m.brand.name, m.name
        """)
    List<Vehicle> findAllWithDetails();

    long countByStatusIn(List<VehicleStatus> statuses);
}
