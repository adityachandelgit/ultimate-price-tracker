package com.adityachandel.ultimatepricetracker.transformer;

import com.adityachandel.ultimatepricetracker.model.ItemPriceHistory;
import com.adityachandel.ultimatepricetracker.model.entity.ItemPriceHistoryEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ItemPriceHistoryTransformer {

    private final ModelMapper modelMapper;

    public ItemPriceHistory transform(ItemPriceHistoryEntity entity) {
        ItemPriceHistory map = modelMapper.map(entity, ItemPriceHistory.class);
        return map;
    }

    public ItemPriceHistoryEntity transform(ItemPriceHistory item) {
        ItemPriceHistoryEntity map = modelMapper.map(item, ItemPriceHistoryEntity.class);
        return map;
    }

    public List<ItemPriceHistory> transform(List<ItemPriceHistoryEntity> entities) {
        return entities.stream().map(this::transform).collect(Collectors.toList());
    }

}
