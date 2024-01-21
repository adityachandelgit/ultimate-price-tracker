package com.adityachandel.ultimatepricetracker.stores;


import com.adityachandel.ultimatepricetracker.FetchException;
import com.adityachandel.ultimatepricetracker.model.MacysItem;
import com.adityachandel.ultimatepricetracker.model.NewItemInfo;
import com.adityachandel.ultimatepricetracker.model.Item;
import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

@Slf4j
@Service
@AllArgsConstructor
public class Macys implements Store {

    private final ObjectMapper objectMapper;

    @Override
    public Item fetchItem(Item item) {
        MacysItem macysItem = getMacysItem(item.getExternalId());
        item.setName(macysItem.getBrand().getName() + ": " + macysItem.getName());
        item.setUrl(macysItem.getUrl());
        item.setImageUrl(macysItem.getImage());
        item.setLatestPrice(Double.parseDouble(macysItem.getOffers().get(0).getPrice()));
        item.setLatestPriceTimestamp(Instant.now());
        return item;
    }

    @Override
    public NewItemInfo getNewItemInfo(StoreType storeType, String itemId) {
        return null;
    }

    @SneakyThrows
    private MacysItem getMacysItem(String itemId) {
        try {
            HttpResponse<String> response = HttpClient.newBuilder().build().send(HttpRequest.newBuilder(new URI(getUrl(itemId))).GET().build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 301) {
                String[] split = response.body().split("/product/");
                response = HttpClient.newBuilder().build().send(HttpRequest.newBuilder(new URI(getUrl301(split[1]))).GET().build(), HttpResponse.BodyHandlers.ofString());
            }
            String elements = Jsoup.parse(response.body()).body().select("script[id=productMktData]").get(0).childNodes().get(0).toString();
            return objectMapper.readValue(elements, MacysItem.class);
        } catch (Exception e) {
            log.error("Failed to fetch " + itemId + " | Error: " + e.getMessage());
            throw new FetchException("Failed to fetch " + itemId + " | Error: " + e.getMessage(), e);
        }
    }

    private String getUrl(String itemId) {
        return "https://www.macys.com/shop/product/?ID=" + itemId;
    }

    private String getUrl301(String itemId) {
        return "https://www.macys.com/shop/product/" + itemId;
    }


}
