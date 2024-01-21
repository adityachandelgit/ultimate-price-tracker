package com.adityachandel.ultimatepricetracker.model;

import lombok.Data;

import java.util.List;

@Data
public class MacysItem {
    private String name;
    private String category;
    private Brand brand;
    private String productID;
    private String url;
    private String description;
    private String image;
    private List<Offer> offers;

    @Data
    public static class Offer {
        private ItemOffered itemOffered;
        private String SKU;
        private String price;
    }

    @Data
    public static class Brand {
        private String name;
    }

    @Data
    public static class ItemOffered {
        private String color;
    }

}
