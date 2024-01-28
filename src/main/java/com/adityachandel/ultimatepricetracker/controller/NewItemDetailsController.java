package com.adityachandel.ultimatepricetracker.controller;

import com.adityachandel.ultimatepricetracker.model.NewItemDetails;
import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import com.adityachandel.ultimatepricetracker.service.NewItemDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/new-item-info")
public class NewItemDetailsController {

    private final NewItemDetailsService newItemDetailsService;

    @GetMapping("/store/{store}/item/{id}")
    public NewItemDetails get(@PathVariable String id, @PathVariable StoreType store) {
        return newItemDetailsService.get(store, id);
    }

}
