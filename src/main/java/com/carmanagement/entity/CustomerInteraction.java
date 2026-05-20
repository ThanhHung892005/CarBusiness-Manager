package com.carmanagement.entity;

import com.carmanagement.enums.InteractionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer_interactions")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerInteraction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id")
    private Lead lead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private InteractionType type = InteractionType.CALL;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "interaction_date", nullable = false)
    private LocalDateTime interactionDate;
}
