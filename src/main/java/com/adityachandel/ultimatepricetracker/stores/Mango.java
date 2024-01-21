package com.adityachandel.ultimatepricetracker.stores;

import com.adityachandel.ultimatepricetracker.model.NewItemInfo;
import com.adityachandel.ultimatepricetracker.model.Item;
import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.adityachandel.ultimatepricetracker.model.NewItemInfo.Metadata.ColorSizes;
import static com.adityachandel.ultimatepricetracker.model.NewItemInfo.Metadata.ColorSizes.Color;
import static com.adityachandel.ultimatepricetracker.model.NewItemInfo.Metadata.ColorSizes.Size;


@AllArgsConstructor
@Service
public class Mango implements Store {

    private ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public Item fetchItem(Item item) {
        JsonNode jsonNode = getData(item.getExternalId());
        item.setUrl("https://shop.mango.com" + jsonNode.get("canonicalUrl").asText());
        if (item.getMetadata() != null && item.getMetadata().getColor() != null && item.getMetadata().getSize() != null) {
            setDataForSizeColor(jsonNode, item);
        } else {
            setDataForNoSizeColor(jsonNode, item);
        }
        item.setLatestPriceTimestamp(Instant.now());
        return item;
    }

    private void setDataForSizeColor(JsonNode jsonNode, Item item) {
        item.setName(jsonNode.get("name").asText() + " | " + item.getMetadata().getColor().getName() + " | " + item.getMetadata().getSize().getName());
        ArrayNode colors = (ArrayNode) jsonNode.get("colors").get("colors");
        for (JsonNode colorNode : colors) {
            if (colorNode.get("id").asText().equals(item.getMetadata().getColor().getId())) {
                item.setLatestPrice(colorNode.get("price").get("price").asInt());
                item.setImageUrl("https://st.mngbcn.com/rcs/pics/static" + colorNode.get("images").get(0).get(0).get("url").asText());
            }
        }
    }

    void setDataForNoSizeColor(JsonNode jsonNode, Item item) {
        item.setName(jsonNode.get("name").asText());
        item.setImageUrl("https://st.mngbcn.com/rcs/pics/static" + jsonNode.findPath("url").asText());
        item.setLatestPrice(jsonNode.get("price").get("price").asDouble());
    }

    @Override
    public NewItemInfo getNewItemInfo(StoreType storeType, String itemId) {
        JsonNode jsonNode = getData(itemId);

        List<ColorSizes> colorSizes = new ArrayList<>();
        ArrayNode colors = (ArrayNode) jsonNode.get("colors").get("colors");
        for (JsonNode colorNode : colors) {
            Color color = Color.builder()
                    .id(colorNode.get("id").asText())
                    .name(colorNode.get("label").asText())
                    .build();
            List<Size> sizes = new ArrayList<>();
            for (JsonNode sizeNode : colorNode.get("sizes")) {
                sizes.add(Size.builder()
                        .id(sizeNode.get("id").asText())
                        .name(sizeNode.get("value").asText())
                        .build());
            }
            colorSizes.add(ColorSizes.builder()
                    .color(color)
                    .sizes(sizes)
                    .build());
        }

        return NewItemInfo.builder()
                .store(storeType)
                .imageUrl("https://st.mngbcn.com/rcs/pics/static" + jsonNode.findPath("url").asText())
                .name(jsonNode.get("name").asText())
                .metadata(NewItemInfo.Metadata.builder()
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
