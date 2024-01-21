package com.adityachandel.ultimatepricetracker.stores;

import com.adityachandel.ultimatepricetracker.FetchException;
import com.adityachandel.ultimatepricetracker.model.NewItemInfo;
import com.adityachandel.ultimatepricetracker.model.Item;
import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.time.Instant;

@AllArgsConstructor
@Service
@Slf4j
public class AnnTaylor implements Store {

    private ObjectMapper objectMapper;

    @Override
    public Item fetchItem(Item item) {
        String url = "https://www.anntaylor.com/" + item.getImageUrl();
        try {
            String response = Jsoup.connect(url).followRedirects(true).execute().body();
            String json = Jsoup.parse(response)
                    .getElementsByClass("bfx-price-container").get(0)
                    .select("script[type=application/ld+json]").get(0).childNode(0).toString().trim();
            JsonNode jsonNode = objectMapper.readTree(json);
            String image = Jsoup.parse(response).select("a[data-product-id=\"" + item.getExternalId() + "\"]").get(0).attributes().get("data-product-image");

            item.setName(jsonNode.get("name").asText());
            item.setUrl(url);
            item.setImageUrl(image);
            item.setLatestPrice(jsonNode.findValue("price").asDouble());
            item.setLatestPriceTimestamp(Instant.now());
            return item;
        } catch (Exception e) {
            log.error("Failed to fetch " + url + " | Error: " + e.getMessage());
            throw new FetchException("Failed to fetch " + url + " | Error: " + e.getMessage(), e);
        }
    }

    @Override
    public NewItemInfo getNewItemInfo(StoreType storeType, String itemId) {
        return null;
    }


}
