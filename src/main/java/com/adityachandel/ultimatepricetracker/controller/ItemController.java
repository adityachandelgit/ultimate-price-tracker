package com.adityachandel.ultimatepricetracker.controller;

import com.adityachandel.ultimatepricetracker.model.api.dto.ItemDTO;
import com.adityachandel.ultimatepricetracker.model.api.request.ApiItemRequest;
import com.adityachandel.ultimatepricetracker.model.Item;
import com.adityachandel.ultimatepricetracker.service.ItemService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping()
    public List<ItemDTO> getAllItems() {
        return itemService.getAll();
    }

    @GetMapping("/{id}")
    public ItemDTO getItem(@PathVariable long id) {
        return itemService.get(id);
    }

    @PostMapping()
    public Item createItem(@RequestBody ApiItemRequest itemRequest) {
        return itemService.track(itemRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable long id) {
        itemService.delete(id);
    }

}
