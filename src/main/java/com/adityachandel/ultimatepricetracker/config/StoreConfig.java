package com.adityachandel.ultimatepricetracker.config;

import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import com.adityachandel.ultimatepricetracker.stores.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StoreConfig {
    private final Macys macys;
    private final Amazon amazon;
    private final Mango mango;
    private final AnnTaylor annTaylor;
    private final Anthropologie anthropologie;
    private final MaxAroma maxAroma;
    private final Map<StoreType, Store> mappings = new HashMap<>();

    public StoreConfig(Macys macys, Amazon amazon, Mango mango, AnnTaylor annTaylor, Anthropologie anthropologie, MaxAroma maxAroma) {
        this.macys = macys;
        this.amazon = amazon;
        this.mango = mango;
        this.annTaylor = annTaylor;
        this.anthropologie = anthropologie;
        this.maxAroma = maxAroma;
    }

    @PostConstruct
    private void init() {
        mappings.put(StoreType.MACYS, macys);
        mappings.put(StoreType.AMAZON, amazon);
        mappings.put(StoreType.MANGO, mango);
        mappings.put(StoreType.ANN_TAYLOR, annTaylor);
        mappings.put(StoreType.ANTHROPOLOGIE, anthropologie);
        mappings.put(StoreType.MAXAROMA, maxAroma);
    }

    public Store getStore(StoreType storeType) {
        return mappings.get(storeType);
    }

}
