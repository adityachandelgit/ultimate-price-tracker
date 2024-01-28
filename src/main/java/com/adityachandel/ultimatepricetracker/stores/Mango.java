package com.adityachandel.ultimatepricetracker.stores;

import com.adityachandel.ultimatepricetracker.FetchException;
import com.adityachandel.ultimatepricetracker.ItemUtils;
import com.adityachandel.ultimatepricetracker.model.Item;
import com.adityachandel.ultimatepricetracker.model.NewItemDetails;
import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import com.adityachandel.ultimatepricetracker.model.NewItemDetails.ItemOptions;
import com.adityachandel.ultimatepricetracker.model.NewItemDetails.ItemOptions.Color;
import com.adityachandel.ultimatepricetracker.model.NewItemDetails.ItemOptions.Size;
import com.adityachandel.ultimatepricetracker.model.NewItemDetails.ItemOptions.SizePrice;


@AllArgsConstructor
@Service
@Slf4j
public class Mango implements Store {

    private ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public Item fetchItem(Item item) {
        try {
            NewItemDetails newItemDetails = getNewItemDetails(item.getExternalId());
            ItemUtils.updateItem(item, newItemDetails);
            return item;
        } catch (Exception e) {
            log.error("Failed to fetch " + item.getUrl() + " | Error: " + e.getMessage());
            throw new FetchException("Failed to fetch " + item.getUrl() + " | Error: " + e.getMessage(), e);
        }
    }

    @Override
    public NewItemDetails getNewItemDetails(String itemId) {
        JsonNode root = getData(itemId);
        List<ItemOptions> itemOptions = getItemOptions(root);
        return NewItemDetails.builder()
                .id(itemId)
                .store(StoreType.MANGO)
                .imageUrl("https://st.mngbcn.com/rcs/pics/static" + root.findPath("url").asText())
                .name(root.get("name").asText())
                .url("https://shop.mango.com" + root.get("canonicalUrl").asText())
                .options(itemOptions)
                .build();
    }

    private List<ItemOptions> getItemOptions(JsonNode root) {
        ArrayNode colors = (ArrayNode) root.get("colors").get("colors");
        List<ItemOptions> itemOptions = new ArrayList<>();
        for (JsonNode colorNode : colors) {
            Color color = Color.builder()
                    .id(colorNode.get("id").asText())
                    .name(colorNode.get("label").asText())
                    .build();
            List<SizePrice> sizePrices = new ArrayList<>();
            long price = colorNode.get("price").get("price").asLong();
            for (JsonNode sizeNode : colorNode.get("sizes")) {
                SizePrice sizePrice = SizePrice.builder()
                        .size(Size.builder()
                                .id(sizeNode.get("id").asText())
                                .name(sizeNode.get("value").asText())
                                .build())
                        .price(price)
                        .build();
                sizePrices.add(sizePrice);
            }
            itemOptions.add(ItemOptions.builder()
                    .color(color)
                    .imageUrl(getImageUrl(colorNode))
                    .sizePrices(sizePrices)
                    .build());
        }
        return itemOptions;
    }

    private String getImageUrl(JsonNode colorNode) {
        ArrayNode colorImages = (ArrayNode) colorNode.get("images");
        for (JsonNode colorImage : colorImages) {
            ArrayNode colorImageImages = (ArrayNode) colorImage;
            for (JsonNode colorImageImage : colorImageImages) {
                if (colorImageImage.get("altText").asText().contains("Media plane")) {
                    return colorImageImage.get("url").asText().split("\\?")[0];
                }
            }
        }
        throw new RuntimeException();
    }

    @SneakyThrows
    private JsonNode getData(String itemId) {
        HttpResponse<String> response = HttpClient.newBuilder().build().send(HttpRequest.newBuilder(new URI(buildProductUrl(itemId))).GET().build(), HttpResponse.BodyHandlers.ofString());
        return objectMapper.readTree(response.body());
    }

    private String buildProductUrl(String itemId) {
        return "https://shop.mango.com/services/garments/400/en/S/" + itemId;
    }
}
