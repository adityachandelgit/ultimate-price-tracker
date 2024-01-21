package com.adityachandel.ultimatepricetracker.model.api.request;

import com.adityachandel.ultimatepricetracker.model.Metadata;
import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import lombok.Data;

@Data
public class ApiItemRequest {
    private String externalId;
    private int desiredPrice;
    private StoreType store;
    private boolean trackingEnabled = true;
    private Metadata metadata;
}