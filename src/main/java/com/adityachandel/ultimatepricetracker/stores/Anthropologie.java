package com.adityachandel.ultimatepricetracker.stores;

import com.adityachandel.ultimatepricetracker.FetchException;
import com.adityachandel.ultimatepricetracker.config.model.StoreCookieProperties;
import com.adityachandel.ultimatepricetracker.model.NewItemInfo;
import com.adityachandel.ultimatepricetracker.model.Item;
import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Scanner;

@Slf4j
@AllArgsConstructor
@Service
public class Anthropologie implements Store {

    private final ObjectMapper objectMapper;
    private final StoreCookieProperties cookieProperties;

    @SneakyThrows
    @Override
    public Item fetchItem(Item item) {
        String url = "https://www.anthropologie.com/shop/" + item.getExternalId();
        item.setUrl(url);
        try {
            fetchAndPopulateItem(url, item);
            if (item.getLatestPrice() == 0.0) {
                log.error("Failed to fetch " + url);
                throw new FetchException("Failed to fetch " + url, new RuntimeException());
            }
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

    private void fetchAndPopulateItem(String url, Item item) throws IOException {
        Scanner scanner = new Scanner(getBody(url));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.contains("window.urbn.initialState")) {
                String l = line.replace("window.urbn.initialState = JSON.parse(", "").replace(", freezeReviver);", "").trim();
                JsonNode root = objectMapper.readTree(objectMapper.readTree(l).asText());
                String image = root.at("/product--" + item.getExternalId() + "/core/catalogData/product/defaultImage").asText();
                item.setImageUrl("https://images.urbndata.com/is/image/Anthropologie/" + image);
                item.setName(root.at("/product--" + item.getExternalId() + "/core/catalogData/product/displayName").asText());
                ArrayNode sliceItems = (ArrayNode) root.at("/product--" + item.getExternalId() + "/core/catalogData/skuInfo/primarySlice/sliceItems");
                for (JsonNode sliceItem : sliceItems) {
                    if (sliceItem.get("code").asText().equals(item.getMetadata().getColor().getName())) {
                        ArrayNode includedSkus = (ArrayNode) sliceItem.get("includedSkus");
                        for (JsonNode sku : includedSkus) {
                            if (sku.get("size").asText().equals("0 p") || sku.get("size").asText().equals("XS P")) {
                                if (sku.get("availableStatus").asInt() == 1003 || sku.get("availableStatus").asInt() == 1000) {
                                    int salePrice = sku.get("salePrice").asInt();
                                    item.setLatestPrice(salePrice);
                                }
                            }
                        }
                    }
                }
            }
        }
        scanner.close();
    }

    @NotNull
    private String getBody(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Cookie", cookieProperties.getAnthropologie())
                .build();
        return new OkHttpClient().newBuilder().build().newCall(request).execute().body().string();
    }
}
