package com.carmanagement.dto.request;

import com.carmanagement.enums.VehicleStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class VehicleSearchRequest {
    private Long brandId;
    private Long modelId;
    private VehicleStatus status = VehicleStatus.AVAILABLE;
    private Long showroomId;
    private String color;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private int page = 0;
    private int size = 15;
}
