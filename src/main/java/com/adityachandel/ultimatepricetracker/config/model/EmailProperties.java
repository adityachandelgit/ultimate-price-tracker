package com.adityachandel.ultimatepricetracker.config.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailProperties {
    private String from;
    private String fromPassword;
    private String to;
}
