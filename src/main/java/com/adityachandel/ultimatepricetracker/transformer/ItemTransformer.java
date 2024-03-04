package com.adityachandel.ultimatepricetracker.transformer;

import com.adityachandel.ultimatepricetracker.model.Item;
import com.adityachandel.ultimatepricetracker.model.api.dto.ItemDTO;
import com.adityachandel.ultimatepricetracker.model.entity.ItemEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ItemTransformer {

    public Item transform(ItemEntity entity) {
        return Item.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .name(entity.getName())
                .store(entity.getStore())
                .url(entity.getUrl())
                .imageUrl(entity.getImageUrl())
                .desiredPrice(entity.getDesiredPrice())
                .latestPrice(entity.getLatestPrice())
                .latestPriceTimestamp(entity.getLatestPriceTimestamp())
                .trackingEnabled(entity.isTrackingEnabled())
                .metadata(entity.getMetadata() == null ? null : entity.getMetadata())
                .build();
    }

    public ItemEntity transform(Item item) {
        return ItemEntity.builder()
                .id(item.getId())
                .externalId(item.getExternalId())
                .name(item.getName())
                .store(item.getStore())
                .url(item.getUrl())
                .imageUrl(item.getImageUrl())
                .desiredPrice(item.getDesiredPrice())
                .latestPrice(item.getLatestPrice())
                .latestPriceTimestamp(item.getLatestPriceTimestamp())
                .trackingEnabled(item.isTrackingEnabled())
                .metadata(item.getMetadata() == null ? null : item.getMetadata())
                .build();
    }

    public ItemDTO transformDTO(Item item) {
        return ItemDTO.builder()
                .id(item.getId())
                .externalId(item.getExternalId())
                .name(item.getName())
                .store(item.getStore().name())
                .url(item.getUrl())
                .imageUrl(item.getImageUrl())
                .desiredPrice(item.getDesiredPrice())
                .latestPrice(item.getLatestPrice())
                .latestPriceTimestamp(item.getLatestPriceTimestamp())
                .trackingEnabled(item.isTrackingEnabled())
                .metadata(item.getMetadata())
                .priceTrend(item.getPriceTrend())
                .build();
    }

    public List<Item> transform(List<ItemEntity> itemEntities) {
        return itemEntities.stream().map(this::transform).collect(Collectors.toList());
    }
}
