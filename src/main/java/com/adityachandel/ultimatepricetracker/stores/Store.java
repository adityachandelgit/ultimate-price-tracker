package com.adityachandel.ultimatepricetracker.stores;


import com.adityachandel.ultimatepricetracker.model.NewItemInfo;
import com.adityachandel.ultimatepricetracker.model.Item;
import com.adityachandel.ultimatepricetracker.model.enums.StoreType;

public interface Store {
    Item fetchItem(Item item);
    NewItemInfo getNewItemInfo(String itemId);
}
