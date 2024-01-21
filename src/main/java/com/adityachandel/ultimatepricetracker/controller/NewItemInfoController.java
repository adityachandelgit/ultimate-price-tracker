package com.adityachandel.ultimatepricetracker.controller;

import com.adityachandel.ultimatepricetracker.model.NewItemInfo;
import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import com.adityachandel.ultimatepricetracker.service.NewItemInfoService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/new-item-info")
public class NewItemInfoController {

    private final NewItemInfoService service;

    @GetMapping("/store/{store}/item/{id}")
    public NewItemInfo get(@PathVariable String id, @PathVariable StoreType store) {
        return service.get(store, id);
    }

}
