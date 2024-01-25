package com.adityachandel.ultimatepricetracker.model;

import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class NewItemInfo {
    private String id;
    private StoreType store;
    private String name;
    private String url;
    private String imageUrl;
    private Instant latestPriceTimestamp;
    private Options metadata;

    @Data
    @Builder
    public static class Options {
        private List<ColorSizes> colorSizes;

        @Data
        @Builder
        public static class ColorSizes {
            private Color color;
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
}


