package com.adityachandel.ultimatepricetracker.stores;


import com.adityachandel.ultimatepricetracker.model.NewItemDetails;
import com.adityachandel.ultimatepricetracker.model.Item;

public interface Store {
    Item fetchItem(Item item);
    NewItemDetails getNewItemDetails(String itemId);
}
