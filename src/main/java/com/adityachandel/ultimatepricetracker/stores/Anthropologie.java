package com.adityachandel.ultimatepricetracker.stores;

import com.adityachandel.ultimatepricetracker.FetchException;
import com.adityachandel.ultimatepricetracker.ItemUtils;
import com.adityachandel.ultimatepricetracker.config.model.StoreCookieProperties;
import com.adityachandel.ultimatepricetracker.model.Item;
import com.adityachandel.ultimatepricetracker.model.NewItemInfo;
import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.adityachandel.ultimatepricetracker.model.NewItemInfo.Options.ColorSizes;
import static com.adityachandel.ultimatepricetracker.model.NewItemInfo.Options.ColorSizes.Color;
import static com.adityachandel.ultimatepricetracker.model.NewItemInfo.Options.ColorSizes.Size;

@Slf4j
@AllArgsConstructor
@Service
public class Anthropologie implements Store {

    private final ObjectMapper objectMapper;
    private final StoreCookieProperties cookieProperties;

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

    @SneakyThrows
    @Override
    public NewItemInfo getNewItemInfo(String itemId) {
        NewItemInfo newItemInfo = NewItemInfo.builder()
                .id(itemId)
                .store(StoreType.ANTHROPOLOGIE)
                .url("https://www.anthropologie.com/shop/" + itemId)
                .build();
        List<ColorSizes> colorSizesList = new ArrayList<>();
        String url = "https://www.anthropologie.com/shop/" + itemId;
        Scanner scanner = new Scanner(getBody(url));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.contains("window.urbn.initialState")) {
                String l = line.replace("window.urbn.initialState = JSON.parse(", "").replace(", freezeReviver);", "").trim();
                JsonNode root = objectMapper.readTree(objectMapper.readTree(l).asText());
                String image = root.at("/product--" + itemId + "/core/catalogData/product/defaultImage").asText();
                newItemInfo.setImageUrl("https://images.urbndata.com/is/image/Anthropologie/" + image);
                ArrayNode sliceItems = (ArrayNode) root.at("/product--" + itemId + "/core/catalogData/skuInfo/primarySlice/sliceItems");
                newItemInfo.setName(root.at("/product--" + itemId + "/core/catalogData/product/displayName").asText());
                for (JsonNode sliceItem : sliceItems) {
                    ColorSizes colorSizes = ColorSizes.builder()
                            .color(Color.builder()
                                    .id(sliceItem.get("code").asText())
                                    .name(sliceItem.get("displayName").asText())
                                    .build())
                            .build();
                    List<ColorSizes.SizePrice> sizePrices = new ArrayList<>();
                    ArrayNode includedSkus = (ArrayNode) sliceItem.get("includedSkus");
                    for (JsonNode sku : includedSkus) {
                        sizePrices.add(ColorSizes.SizePrice.builder()
                                .size(Size.builder()
                                        .name(sku.get("size").asText())
                                        .id(sku.get("sizeId").asText())
                                        .build())
                                .price(sku.get("salePrice").asLong())
                                .build());
                    }
                    colorSizes.setSizePrices(sizePrices);
                    colorSizesList.add(colorSizes);
                }
                newItemInfo.setMetadata(NewItemInfo.Options.builder()
                        .colorSizes(colorSizesList)
                        .build());
            }
        }
        return newItemInfo;
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
