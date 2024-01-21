package com.adityachandel.ultimatepricetracker.repository;

import com.adityachandel.ultimatepricetracker.model.entity.ItemEntity;
import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
    Optional<ItemEntity> findByExternalIdAndStore(String externalId, StoreType store);
    List<ItemEntity> findByStoreAndTrackingEnabled(StoreType storeType, boolean trackingEnabled);
}
