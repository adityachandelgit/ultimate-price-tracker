package com.adityachandel.ultimatepricetracker.service;

import com.adityachandel.ultimatepricetracker.config.StoreConfig;
import com.adityachandel.ultimatepricetracker.model.NewItemDetails;
import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NewItemDetailsService {

    private final StoreConfig storeConfig;

    public NewItemDetails get(StoreType storeType, String itemId) {
        return storeConfig.getStore(storeType).getNewItemDetails(itemId);
    }
}
