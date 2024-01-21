package com.adityachandel.ultimatepricetracker.service;

import com.adityachandel.ultimatepricetracker.model.Item;
import com.adityachandel.ultimatepricetracker.model.ItemPriceHistory;
import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import com.adityachandel.ultimatepricetracker.repository.ItemPriceHistoryRepository;
import com.adityachandel.ultimatepricetracker.transformer.ItemPriceHistoryTransformer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ItemPriceHistoryService {

    private final ItemPriceHistoryRepository repository;
    private final ItemPriceHistoryTransformer transformer;

    public void saveItemHistory(Item item) {
        ItemPriceHistory itemPriceHistory = ItemPriceHistory.builder()
                .price(item.getLatestPrice())
                .itemId(item.getId())
                .store(item.getStore())
                .timestamp(Instant.now())
                .build();
        repository.save(transformer.transform(itemPriceHistory));
    }

    public List<ItemPriceHistory> getItemHistory(Long itemId, StoreType storeType) {
        List<ItemPriceHistory> collect = repository.findAllByItemIdAndStore(itemId, storeType)
                .stream()
                .map(transformer::transform)
                .collect(Collectors.toList());
        int times = 0;
        while (collect.size() > 500 && times < 10) {
            collect = v2(collect);
            times++;
        }
        return collect;
    }

    private List<ItemPriceHistory> v2(List<ItemPriceHistory> input) {
        List<ItemPriceHistory> result = new ArrayList<>();
        int step = 5;
        for (int start = 0; start < input.size(); start += step) {
            List<ItemPriceHistory> priceHistories = input.subList(start, Math.min(start + step, input.size()));
            if (areAllPricesSame(priceHistories)) {
                result.add(priceHistories.get(priceHistories.size() - 1));
            } else {
                result.addAll(getUniques(priceHistories));
            }
        }
        return result;
    }

    private boolean areAllPricesSame(List<ItemPriceHistory> priceHistories) {
        return priceHistories.stream()
                .map(ItemPriceHistory::getPrice)
                .distinct()
                .limit(2)
                .count() <= 1;
    }

    private List<ItemPriceHistory> remove5Points(List<ItemPriceHistory> input) {
        List<ItemPriceHistory> result = new ArrayList<>();
        int step = 5;
        for (int i = 0; i < input.size() - 1; i += step) {
            double firstPrice = input.get(i).getPrice();
            boolean isBroken = false;
            for (int j = i + 1; j < i + step; j++) {
                if (input.get(j).getPrice() != firstPrice) {
                    isBroken = true;
                    break;
                }
            }
            if (isBroken) {
                result.addAll(getUniques(input.subList(i, i + step - 1)));
            } else {
                result.add(input.get(i));
                result.add(input.get(i + step - 1));
            }
        }
        return result;
    }

    List<ItemPriceHistory> getUniques(List<ItemPriceHistory> input) {
        LinkedHashMap<Double, ItemPriceHistory> map = new LinkedHashMap<>();
        for (ItemPriceHistory item : input) {
            map.put(item.getPrice(), item);
        }
        return new ArrayList<>(map.values());
    }

    List<ItemPriceHistory> findLatest5PricesForAllItems() {
        return transformer.transform(repository.findLatestPricesForAllItems());
    }

}
