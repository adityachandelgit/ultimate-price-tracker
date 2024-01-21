package com.adityachandel.ultimatepricetracker.service;

import com.adityachandel.ultimatepricetracker.config.StoreConfig;
import com.adityachandel.ultimatepricetracker.model.NewItemInfo;
import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import com.adityachandel.ultimatepricetracker.stores.Store;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NewItemInfoService {

    private final StoreConfig storeConfig;

    public NewItemInfo get(StoreType storeType, String itemId) {
        Store store = storeConfig.getStore(storeType);
        return store.getNewItemInfo(storeType, itemId);
    }
}
