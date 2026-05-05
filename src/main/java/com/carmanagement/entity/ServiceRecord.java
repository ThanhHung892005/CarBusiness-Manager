package com.carmanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "service_records")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private ServiceAppointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;

    @Column(name = "mileage_in")
    private Integer mileageIn;

    @Column(name = "mileage_out")
    private Integer mileageOut;

    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    @Column(name = "work_done", columnDefinition = "TEXT")
    private String workDone;

    @Column(name = "total_cost", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalCost = BigDecimal.ZERO;

    @Column(name = "labor_cost", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal laborCost = BigDecimal.ZERO;

    @Column(name = "parts_cost", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal partsCost = BigDecimal.ZERO;

    @Column(name = "next_service_date")
    private LocalDate nextServiceDate;

    @OneToMany(mappedBy = "serviceRecord", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ServiceItem> serviceItems = new ArrayList<>();
}
