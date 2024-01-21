package com.adityachandel.ultimatepricetracker.model.entity;

import com.adityachandel.ultimatepricetracker.model.Metadata;
import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import com.adityachandel.ultimatepricetracker.service.MetadataConvertor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "item")
@Data
public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String externalId;
    private String name;
    private String url;
    @Enumerated(EnumType.STRING)
    private StoreType store;
    private String imageUrl;
    private double desiredPrice;
    private double latestPrice;
    private Instant latestPriceTimestamp;
    private boolean trackingEnabled;
    @Convert(converter = MetadataConvertor.class)
    private Metadata metadata;
}
