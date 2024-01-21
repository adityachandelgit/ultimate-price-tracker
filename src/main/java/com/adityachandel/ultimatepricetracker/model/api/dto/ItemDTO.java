package com.adityachandel.ultimatepricetracker.model.api.dto;

import com.adityachandel.ultimatepricetracker.model.Metadata;
import com.adityachandel.ultimatepricetracker.model.enums.PriceTrend;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Builder
@Getter
public class ItemDTO {
    private Long id;
    private String externalId;
    private String name;
    private String store;
    private String url;
    private String imageUrl;
    private double desiredPrice;
    private double latestPrice;
    private Instant latestPriceTimestamp;
    private boolean trackingEnabled;
    private PriceTrend priceTrend;
    private Metadata metadata;
}
