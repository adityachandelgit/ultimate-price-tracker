package com.adityachandel.ultimatepricetracker.model.entity;

import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "email_history")
@Data
public class EmailHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long itemId;
    private StoreType store;
    private double price;
    private Instant emailSentTimestamp;
}
