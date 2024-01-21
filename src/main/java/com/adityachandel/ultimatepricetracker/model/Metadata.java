package com.adityachandel.ultimatepricetracker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Metadata {
    private Color color;
    private Size size;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class Color {
        private String name;
        private String id;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class Size {
        private String name;
        private String id;
    }
}
