package com.adityachandel.ultimatepricetracker.service;

import com.adityachandel.ultimatepricetracker.config.StoreConfig;
import com.adityachandel.ultimatepricetracker.model.Item;
import com.adityachandel.ultimatepricetracker.model.Metadata;
import com.adityachandel.ultimatepricetracker.model.api.dto.ItemDTO;
import com.adityachandel.ultimatepricetracker.model.ItemPriceHistory;
import com.adityachandel.ultimatepricetracker.model.api.request.ApiItemRequest;
import com.adityachandel.ultimatepricetracker.model.entity.ItemEntity;
import com.adityachandel.ultimatepricetracker.model.enums.PriceTrend;
import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import com.adityachandel.ultimatepricetracker.repository.ItemRepository;
import com.adityachandel.ultimatepricetracker.stores.Store;
import com.adityachandel.ultimatepricetracker.transformer.ItemTransformer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemService {

    private final StoreConfig storeConfig;
    private final ItemRepository itemRepository;
    private final ItemTransformer itemTransformer;
    private final ItemPriceHistoryService itemHistoryService;

    @Transactional
    public Item track(ApiItemRequest request) {
        Store store = storeConfig.getStore(request.getStore());
        Item item = store.fetchItem(Item.builder()
                .externalId(request.getExternalId())
                .store(request.getStore())
                .metadata(request.getMetadata())
                .build());
        Optional<Item> existingItemOpt = findByExternalIdAndStore(request.getExternalId(), request.getStore(), request.getMetadata());
        if (existingItemOpt.isPresent()) {
            Item existingItem = existingItemOpt.get();
            updateExistingItem(existingItem, item);
            item = existingItem;
        }
        item.setTrackingEnabled(request.isTrackingEnabled());
        item.setDesiredPrice(request.getDesiredPrice());
        item = saveItem(item);
        itemHistoryService.saveItemHistory(item);
        return item;
    }

    Optional<Item> findByExternalIdAndStore(String externalId, StoreType store, Metadata metadata) {
        return itemRepository.findByExternalIdAndStoreAndMetadata(externalId, store, metadata).map(itemTransformer::transform);
    }

    Item saveItem(Item item) {
        return itemTransformer.transform(itemRepository.save(itemTransformer.transform(item)));
    }

    private void updateExistingItem(Item existingItem, Item newItem) {
        existingItem.setName(newItem.getName());
        existingItem.setLatestPrice(newItem.getLatestPrice());
        existingItem.setImageUrl(newItem.getImageUrl());
    }

    public List<Item> findByStoreAndTrackingEnabled(StoreType store, boolean enabled) {
        return itemTransformer.transform(itemRepository.findByStoreAndTrackingEnabled(store, enabled));
    }

    public void updateItemTrackWithNewDetails(Item item) {
        itemRepository.save(itemTransformer.transform(item));
    }

    public List<ItemDTO> getAll() {
        List<Item> items = itemTransformer.transform(itemRepository.findAll());
        List<ItemPriceHistory> latestPrices = itemHistoryService.findLatest5PricesForAllItems();
        Map<Long, List<ItemPriceHistory>> map = latestPrices.stream().collect(Collectors.groupingBy(ItemPriceHistory::getItemId, HashMap::new, Collectors.toCollection(ArrayList::new)));
        for (Item item : items) {
            List<ItemPriceHistory> priceHistory = map.get(item.getId());
            if (priceHistory != null && !priceHistory.isEmpty()) {
                priceHistory.sort(Comparator.comparing(ItemPriceHistory::getId));
                double firstPrice = priceHistory.get(0).getPrice();
                double average = firstPrice;
                if (priceHistory.size() > 1) {
                    average = priceHistory.subList(1, priceHistory.size()).stream().mapToDouble(ItemPriceHistory::getPrice).average().orElse(0);
                }
                if (average > firstPrice) {
                    item.setPriceTrend(PriceTrend.UP);
                } else if (average < firstPrice) {
                    item.setPriceTrend(PriceTrend.DOWN);
                } else {
                    item.setPriceTrend(PriceTrend.FLAT);
                }
            }
        }
        return items.stream().map(itemTransformer::transformDTO).collect(Collectors.toList());
    }

    public void delete(Long id) {
        itemRepository.deleteById(id);
    }

    public ItemDTO get(long id) {
        return itemTransformer.transformDTO(itemTransformer.transform(itemRepository.findById(id).orElseThrow()));
    }

    public ItemDTO updatePrice(long id, int newPrice) {
        Optional<ItemEntity> item = itemRepository.findById(id);
        if (item.isPresent()) {
            item.get().setDesiredPrice(newPrice);
            return itemTransformer.transformDTO(itemTransformer.transform(itemRepository.save(item.get())));
        } else {
            throw new RuntimeException();
        }
    }
}
