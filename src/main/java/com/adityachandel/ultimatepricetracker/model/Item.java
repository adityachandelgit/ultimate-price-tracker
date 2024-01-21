package com.adityachandel.ultimatepricetracker.model;

import com.adityachandel.ultimatepricetracker.model.Metadata;
import com.adityachandel.ultimatepricetracker.model.enums.PriceTrend;
import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Item {
    private Long id;
    private String externalId;
    private String name;
    private StoreType store;
    private String url;
    private String imageUrl;
    private double desiredPrice;
    private double latestPrice;
    private Instant latestPriceTimestamp;
    private boolean trackingEnabled;
    private PriceTrend priceTrend;
    private Metadata metadata;
}