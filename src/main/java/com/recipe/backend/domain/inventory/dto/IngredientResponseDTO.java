package com.recipe.backend.domain.inventory.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class IngredientResponseDTO {
    private String name;
    private LocalDate expiryDate;
    private boolean isExpiringSoon; // 유통기한 임박 여부
    private boolean isExpired;      // 유통기한 지남 여부

    public IngredientResponseDTO(String name, LocalDate expiryDate) {
        this.name = name;
        this.expiryDate = expiryDate;
        this.isExpiringSoon = expiryDate.isBefore(LocalDate.now().plusDays(7)) && !expiryDate.isBefore(LocalDate.now());
        this.isExpired = expiryDate.isBefore(LocalDate.now());
    }

    // Getters and setters...
}
