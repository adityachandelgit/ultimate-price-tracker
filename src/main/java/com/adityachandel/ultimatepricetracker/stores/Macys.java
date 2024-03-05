package com.adityachandel.ultimatepricetracker.stores;


import com.adityachandel.ultimatepricetracker.FetchException;
import com.adityachandel.ultimatepricetracker.ItemUtils;
import com.adityachandel.ultimatepricetracker.model.Item;
import com.adityachandel.ultimatepricetracker.model.NewItemDetails;
import com.adityachandel.ultimatepricetracker.model.NewItemDetails.ItemOptions;
import com.adityachandel.ultimatepricetracker.model.NewItemDetails.ItemOptions.Color;
import com.adityachandel.ultimatepricetracker.model.NewItemDetails.ItemOptions.SizePrice;
import com.adityachandel.ultimatepricetracker.model.NewItemDetails.ItemOptions.Size;
import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class Macys implements Store {

    private final ObjectMapper objectMapper;

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
        try {
            HttpResponse<String> response = HttpClient.newBuilder().build().send(HttpRequest.newBuilder(new URI(getUrl(itemId))).GET().build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 301) {
                String[] split = response.body().split("/product/");
                response = HttpClient.newBuilder().build().send(HttpRequest.newBuilder(new URI(getUrl301(split[1]))).GET().build(), HttpResponse.BodyHandlers.ofString());
            }
            Elements elements = Jsoup.parse(response.body()).getElementsByTag("script");
            for (Element element : elements) {
                List<DataNode> dataNodes = element.dataNodes();
                for (DataNode node : dataNodes) {
                    String wholeData = node.getWholeData();
                    if (wholeData.contains("window.__INITIAL_STATE__")) {
                        String cleaned = wholeData.trim().replace("window.__INITIAL_STATE__ = ", "").replace("\"};", "");
                        String[] split = cleaned.split("\"_PDP_BOOTSTRAP_DATA\":\"");
                        String s = "\"" + split[1] + "\"";
                        JsonNode root = objectMapper.readTree(objectMapper.readTree(s).asText());
                        boolean isAvailable = root.get("product").get("availability").get("available").asBoolean();
                        if (isAvailable) {
                            String name = root.get("product").get("detail").get("name").asText();
                            String brand = root.get("product").get("detail").get("brand").get("name").asText();
                            String imageUrl = "https://slimages.macysassets.com/is/image/MCY/products/" + root.get("product").get("imagery").get("images").get(0).get("filePath").asText();
                            JsonNode colorMapNode = root.get("product").get("traits").get("colors").get("colorMap");
                            Map<String, JsonNode> colorMap = objectMapper.convertValue(colorMapNode, new TypeReference<Map<String, JsonNode>>() {
                            });
                            List<ItemOptions> itemOptions = new ArrayList<>();
                            for (var colorJson : colorMap.entrySet()) {
                                JsonNode colorNode = colorJson.getValue();
                                String colorId = colorNode.get("id").asText();
                                String colorName = colorNode.get("name").asText();
                                String url = "https://slimages.macysassets.com/is/image/MCY/products/" + colorNode.get("imagery").get("images").get(0).get("filePath").asText();
                                JsonNode sizeMapNode = root.get("product").get("traits").get("sizes").get("sizeMap");
                                Map<String, JsonNode> sizeMap = objectMapper.convertValue(sizeMapNode, new TypeReference<Map<String, JsonNode>>() {
                                });
                                Map<String, Long> colorToPriceMap = new HashMap<>();
                                ArrayNode priceToColor = (ArrayNode) root.get("product").get("traits").get("traitsMaps").get("priceToColors");
                                for (JsonNode priceToColorNode : priceToColor) {
                                    Long price = (long) Float.parseFloat(priceToColorNode.get("price").asText().substring(1));
                                    ArrayNode colorIds = (ArrayNode) priceToColorNode.get("colorIds");
                                    for (JsonNode colorIdNode : colorIds) {
                                        colorToPriceMap.put(colorIdNode.asText(), price);
                                    }
                                }
                                List<SizePrice> sizePrices = new ArrayList<>();
                                for (var sizeJson : sizeMap.entrySet()) {
                                    String sizeId = null;
                                    String sizeName = null;
                                    JsonNode sizeNode = sizeJson.getValue();
                                    ArrayNode colorsArray = (ArrayNode) sizeNode.get("colors");
                                    for (JsonNode sizeColorNode : colorsArray) {
                                        if (sizeColorNode.asText().equals(colorId)) {
                                            sizeId = sizeNode.get("id").asText();
                                            sizeName = sizeNode.get("name").asText();
                                        }
                                    }
                                    sizePrices.add(SizePrice.builder()
                                            .size(Size.builder()
                                                    .id(sizeId)
                                                    .name(sizeName)
                                                    .build())
                                            .price(colorToPriceMap.get(colorId))
                                            .build());
                                }
                                itemOptions.add(ItemOptions.builder()
                                        .color(Color.builder()
                                                .id(colorId)
                                                .name(colorName)
                                                .build())
                                        .sizePrices(sizePrices)
                                        .imageUrl(url)
                                        .build());
                            }
                            return NewItemDetails.builder()
                                    .id(itemId)
                                    .name(brand + " - " + name)
                                    .store(StoreType.MACYS)
                                    .url("https://www.macys.com/shop/product/?ID=" + itemId)
                                    .imageUrl(imageUrl)
                                    .options(itemOptions)
                                    .build();
                        }
                    }
                }
            }
            log.error("Failed to fetch " + itemId + " | Error: Item not available");
            throw new FetchException("Failed to fetch " + itemId + " | Error: Item not available", new RuntimeException("Item not available " + itemId));
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
