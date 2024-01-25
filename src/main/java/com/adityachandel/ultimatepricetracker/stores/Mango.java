package com.adityachandel.ultimatepricetracker.stores;

import com.adityachandel.ultimatepricetracker.FetchException;
import com.adityachandel.ultimatepricetracker.ItemUtils;
import com.adityachandel.ultimatepricetracker.model.NewItemInfo;
import com.adityachandel.ultimatepricetracker.model.Item;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.adityachandel.ultimatepricetracker.model.NewItemInfo.Options.ColorSizes;
import static com.adityachandel.ultimatepricetracker.model.NewItemInfo.Options.ColorSizes.Color;
import static com.adityachandel.ultimatepricetracker.model.NewItemInfo.Options.ColorSizes.Size;
import static com.adityachandel.ultimatepricetracker.model.NewItemInfo.Options.ColorSizes.SizePrice;


@AllArgsConstructor
@Service
@Slf4j
public class Mango implements Store {

    private ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public Item fetchItem(Item item) {
        try {
            NewItemInfo newItemInfo = getNewItemInfo(item.getExternalId());
            ItemUtils.updateItem(item, newItemInfo);
            return item;
        } catch (Exception e) {
            log.error("Failed to fetch " + item.getUrl() + " | Error: " + e.getMessage());
            throw new FetchException("Failed to fetch " + item.getUrl() + " | Error: " + e.getMessage(), e);
        }
    }

    @Override
    public NewItemInfo getNewItemInfo(String itemId) {
        JsonNode root = getData(itemId);
        ArrayNode colors = (ArrayNode) root.get("colors").get("colors");
        List<ColorSizes> colorSizes = new ArrayList<>();
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
            colorSizes.add(ColorSizes.builder()
                    .color(color)
                    .sizePrices(sizePrices)
                    .build());
        }

        return NewItemInfo.builder()
                .store(StoreType.MANGO)
                .imageUrl("https://st.mngbcn.com/rcs/pics/static" + root.findPath("url").asText())
                .name(root.get("name").asText())
                .url("https://shop.mango.com" + root.get("canonicalUrl").asText())
                .metadata(NewItemInfo.Options.builder()
                        .colorSizes(colorSizes)
                        .build())
                .build();
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
