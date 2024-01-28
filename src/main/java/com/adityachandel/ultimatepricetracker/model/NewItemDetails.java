package com.adityachandel.ultimatepricetracker.model;

import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class NewItemDetails {
    private String id;
    private StoreType store;
    private String name;
    private String url;
    private String imageUrl;
    private Long price;
    private Instant latestPriceTimestamp;
    private List<ItemOptions> options;


    @Data
    @Builder
    public static class ItemOptions {
        private Color color;
        private String imageUrl;
        private List<SizePrice> sizePrices;

        @Data
        @Builder
        public static class SizePrice {
            private Size size;
            private Long price;
        }

        @Data
        @Builder
        public static class Color {
            private String name;
            private String id;
        }

        @Data
        @Builder
        public static class Size {
            private String name;
            private String id;
        }

    }

}


