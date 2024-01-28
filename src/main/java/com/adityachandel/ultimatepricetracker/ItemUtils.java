package com.adityachandel.ultimatepricetracker;


import com.adityachandel.ultimatepricetracker.model.Item;
import com.adityachandel.ultimatepricetracker.model.Metadata;
import com.adityachandel.ultimatepricetracker.model.NewItemDetails;
import com.adityachandel.ultimatepricetracker.model.NewItemDetails.ItemOptions;
import com.adityachandel.ultimatepricetracker.model.NewItemDetails.ItemOptions.Color;
import com.adityachandel.ultimatepricetracker.model.NewItemDetails.ItemOptions.Size;
import com.adityachandel.ultimatepricetracker.model.NewItemDetails.ItemOptions.SizePrice;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class ItemUtils {

    public static void updateItem(Item item, NewItemDetails newItemDetails) {
        item.setName(getName(item, newItemDetails));
        item.setLatestPrice(getLatestPrice(item, newItemDetails));
        item.setImageUrl(getImageUrl(item, newItemDetails));
        item.setLatestPriceTimestamp(Instant.now());
        item.setUrl(newItemDetails.getUrl());
    }

    private static String getName(Item item, NewItemDetails newItemDetails) {
        String name = newItemDetails.getName();
        if (item.getMetadata() != null) {
            name += " | " + item.getMetadata().getColor().getName() + " | " + item.getMetadata().getSize().getName();
        }
        return name;
    }

    private static long getLatestPrice(Item item, NewItemDetails newItemDetails) {
        if (item.getMetadata() != null) {
            Metadata.Color color = item.getMetadata().getColor();
            if (color != null) {
                Optional<ItemOptions> options = newItemDetails.getOptions().stream().filter(o -> o.getColor().getId().equals(color.getId())).findFirst();
                if (options.isPresent() && options.get().getSizePrices() != null) {
                    Optional<SizePrice> sizePrice = options.get().getSizePrices().stream().filter(sp -> Objects.equals(sp.getSize().getId(), item.getMetadata().getSize().getId())).findFirst();
                    if (sizePrice.isPresent()) {
                        return sizePrice.get().getPrice();
                    }
                }
            }
        }
        return newItemDetails.getPrice();
    }

    private static String getImageUrl(Item item, NewItemDetails newItemDetails) {
        if (item.getMetadata() != null) {
            Metadata.Color color = item.getMetadata().getColor();
            if (color != null) {
                Optional<ItemOptions> options = newItemDetails.getOptions().stream().filter(o -> o.getColor().getId().equals(color.getId())).findFirst();
                if (options.isPresent() && options.get().getImageUrl() != null) {
                    return options.get().getImageUrl();
                }
            }
        }
        return newItemDetails.getImageUrl();
    }

}
