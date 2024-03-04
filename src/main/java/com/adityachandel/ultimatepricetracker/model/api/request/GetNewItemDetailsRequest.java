package com.adityachandel.ultimatepricetracker.model.api.request;

import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import lombok.Data;

@Data
public class GetNewItemDetailsRequest {
    private StoreType store;
    private String itemId;
}
