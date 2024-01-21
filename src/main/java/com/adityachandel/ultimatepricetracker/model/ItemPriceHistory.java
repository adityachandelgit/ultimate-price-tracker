package com.adityachandel.ultimatepricetracker.model;

import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ItemPriceHistory {
    private Long id;
    private Long itemId;
    @Enumerated(value = EnumType.STRING)
    private StoreType store;
    private Double price;
    private Instant timestamp;
}
