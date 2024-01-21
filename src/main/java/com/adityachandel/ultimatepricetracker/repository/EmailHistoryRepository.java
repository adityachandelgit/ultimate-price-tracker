package com.adityachandel.ultimatepricetracker.repository;


import com.adityachandel.ultimatepricetracker.model.entity.EmailHistoryEntity;
import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailHistoryRepository extends JpaRepository<EmailHistoryEntity, String> {
    Optional<EmailHistoryEntity> findTopByItemIdAndStoreOrderByEmailSentTimestampDesc(Long itemId, StoreType store);
}
