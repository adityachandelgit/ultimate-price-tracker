package com.adityachandel.ultimatepricetracker.controller;

import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/config")
public class ConfigController {

    @GetMapping("/store")
    Set<StoreType> getStoreConfig() {
        return Arrays.stream(StoreType.values()).collect(Collectors.toSet());
    }

}
