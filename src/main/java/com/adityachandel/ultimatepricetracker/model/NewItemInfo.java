package com.adityachandel.ultimatepricetracker.model;

import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class NewItemInfo {
    private StoreType store;
    private String name;
    private String imageUrl;
    private Metadata metadata;

    @Data
    @Builder
    public static class Metadata {
        private List<ColorSizes> colorSizes;

        @Data
        @Builder
        public static class ColorSizes {
            private Color color;
            private List<Size> sizes;

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


