package com.adityachandel.ultimatepricetracker.repository;

import com.adityachandel.ultimatepricetracker.model.entity.ItemPriceHistoryEntity;
import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemPriceHistoryRepository extends JpaRepository<ItemPriceHistoryEntity, Long> {

    List<ItemPriceHistoryEntity> findAllByItemIdAndStore(Long itemId, StoreType storeType);

    @Query("SELECT h " +
            "FROM ItemEntity i " +
            "LEFT JOIN ItemPriceHistoryEntity h ON i.id = h.itemId " +
            "WHERE h.timestamp IN (" +
            "    SELECT h1.timestamp " +
            "    FROM ItemPriceHistoryEntity h1 " +
            "    WHERE h1.itemId = i.id " +
            "    ORDER BY h1.timestamp DESC " +
            "    LIMIT 5" +
            ")")
    List<ItemPriceHistoryEntity> findLatestPricesForAllItem1s();


    @Query(value = "SELECT * FROM ( " +
            "   SELECT " +
            "       iph.*, " +
            "       ROW_NUMBER() OVER (PARTITION BY iph.itemId ORDER BY iph.timestamp DESC) AS row_num " +
            "   FROM " +
            "       item_price_history iph " +
            "   ) AS ranked " +
            "WHERE " +
            "   row_num <= 5",
            nativeQuery = true)
    List<ItemPriceHistoryEntity> findLatestPricesForAllItems();


}
