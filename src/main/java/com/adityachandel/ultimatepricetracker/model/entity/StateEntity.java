package com.adityachandel.ultimatepricetracker.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "app_state")
@Data
public class StateEntity {
    @Id
    private String name;
    private String value;
}
