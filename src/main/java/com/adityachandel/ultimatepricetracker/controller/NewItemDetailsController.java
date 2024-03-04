package com.adityachandel.ultimatepricetracker.controller;

import com.adityachandel.ultimatepricetracker.model.NewItemDetails;
import com.adityachandel.ultimatepricetracker.model.api.request.GetNewItemDetailsRequest;
import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import com.adityachandel.ultimatepricetracker.service.NewItemDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/new-item-info")
public class NewItemDetailsController {

    private final NewItemDetailsService newItemDetailsService;

    @PostMapping()
    public NewItemDetails get(@RequestBody GetNewItemDetailsRequest request) {
        return newItemDetailsService.get(request.getStore(), request.getItemId());
    }

}
