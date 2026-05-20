package com.carmanagement.dto.request;

import com.carmanagement.enums.InteractionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class InteractionCreateRequest {

    @NotNull
    private InteractionType type;

    @NotBlank
    private String content;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime interactionDate;
}
