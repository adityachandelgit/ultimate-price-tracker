package com.adityachandel.ultimatepricetracker.controller;

import com.adityachandel.ultimatepricetracker.model.api.request.ApiItemHistoryRequest;
import com.adityachandel.ultimatepricetracker.model.ItemPriceHistory;
import com.adityachandel.ultimatepricetracker.service.ItemPriceHistoryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/price-history")
public class ItemPriceHistoryController {

private final ItemPriceHistoryService itemPriceHistoryService;

    @PostMapping()
    public List<ItemPriceHistory> getItemHistoryForItem(@RequestBody ApiItemHistoryRequest request) {
        return itemPriceHistoryService.getItemHistory(request.getItemId(), request.getStoreType());
    }

}
