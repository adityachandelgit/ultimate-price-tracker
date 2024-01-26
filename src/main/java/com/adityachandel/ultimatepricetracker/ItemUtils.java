package com.adityachandel.ultimatepricetracker;

import com.adityachandel.ultimatepricetracker.model.Item;
import com.adityachandel.ultimatepricetracker.model.NewItemInfo;

import java.time.Instant;

public class ItemUtils {

    public static long getLatestPrice(NewItemInfo newItemInfo, Item item) {
        for (NewItemInfo.Options.ColorSizes colorSize : newItemInfo.getMetadata().getColorSizes()) {
            NewItemInfo.Options.ColorSizes.Color color = colorSize.getColor();
            if (item.getMetadata().getColor().getId().equals(color.getId())) {
                for (NewItemInfo.Options.ColorSizes.SizePrice sizePrice : colorSize.getSizePrices()) {
                    NewItemInfo.Options.ColorSizes.Size size = sizePrice.getSize();
                    if (item.getMetadata().getSize().getId().equals(size.getId())) {
                        return sizePrice.getPrice();
                    }
                }
            }
        }
        throw new RuntimeException();
    }

    public static void updateItem(Item item, NewItemInfo newItemInfo) {
        String name = newItemInfo.getName();
        if (newItemInfo.getMetadata() != null) {
            name += " | " + item.getMetadata().getColor().getName() + " | " + item.getMetadata().getSize().getName();
            item.setLatestPrice(getLatestPrice(newItemInfo, item));
        } else {
            item.setLatestPrice(newItemInfo.getPrice());
        }
        item.setName(name);
        item.setLatestPriceTimestamp(newItemInfo.getLatestPriceTimestamp());
        item.setImageUrl(newItemInfo.getImageUrl());
        item.setLatestPriceTimestamp(Instant.now());
        item.setUrl(newItemInfo.getUrl());
    }

}
