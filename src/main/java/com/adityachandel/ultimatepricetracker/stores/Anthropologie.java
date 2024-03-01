package com.adityachandel.ultimatepricetracker.stores;

import com.adityachandel.ultimatepricetracker.FetchException;
import com.adityachandel.ultimatepricetracker.ItemUtils;
import com.adityachandel.ultimatepricetracker.config.model.StoreCookieProperties;
import com.adityachandel.ultimatepricetracker.model.Item;
import com.adityachandel.ultimatepricetracker.model.NewItemDetails;
import com.adityachandel.ultimatepricetracker.model.NewItemDetails.ItemOptions;
import com.adityachandel.ultimatepricetracker.model.NewItemDetails.ItemOptions.Color;
import com.adityachandel.ultimatepricetracker.model.NewItemDetails.ItemOptions.Size;
import com.adityachandel.ultimatepricetracker.model.NewItemDetails.ItemOptions.SizePrice;
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
import java.util.ArrayList;
import java.util.List;
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
        try {
            NewItemDetails newItemDetails = getNewItemDetails(item.getExternalId());
            ItemUtils.updateItem(item, newItemDetails);
            return item;
        } catch (Exception e) {
            log.error("Failed to fetch " + item.getUrl() + " | Error: " + e.getMessage());
            throw new FetchException("Failed to fetch " + item.getUrl() + " | Error: " + e.getMessage(), e);
        }
    }

    @SneakyThrows
    @Override
    public NewItemDetails getNewItemDetails(String itemId) {
        NewItemDetails newItemDetails = NewItemDetails.builder()
                .id(itemId)
                .store(StoreType.ANTHROPOLOGIE)
                .url("https://www.anthropologie.com/shop/" + itemId)
                .build();
        List<ItemOptions> colorSizesList = new ArrayList<>();
        String url = "https://www.anthropologie.com/shop/" + itemId;
        String body = getBody(url);
        Scanner scanner = new Scanner(body);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.contains("clientSideSlugs")) {
                line = line.replace("<script type=\"mime/invalid\" id=\"urbnInitialState\">", "").replace("</script>", "").trim();
                JsonNode root = objectMapper.readTree(line);
                String image = root.at("/product--" + itemId + "/core/catalogData/product/defaultImage").asText();
                newItemDetails.setImageUrl("https://images.urbndata.com/is/image/Anthropologie/" + image);
                ArrayNode sliceItems = (ArrayNode) root.at("/product--" + itemId + "/core/catalogData/skuInfo/primarySlice/sliceItems");
                newItemDetails.setName(root.at("/product--" + itemId + "/core/catalogData/product/displayName").asText());
                for (JsonNode sliceItem : sliceItems) {
                    ItemOptions itemOptions = ItemOptions.builder()
                            .color(Color.builder()
                                    .id(sliceItem.get("code").asText())
                                    .name(sliceItem.get("displayName").asText())
                                    .build())
                            .build();
                    List<SizePrice> sizePrices = new ArrayList<>();
                    ArrayNode includedSkus = (ArrayNode) sliceItem.get("includedSkus");
                    for (JsonNode sku : includedSkus) {
                        sizePrices.add(SizePrice.builder()
                                .size(Size.builder()
                                        .name(sku.get("size").asText())
                                        .id(sku.get("sizeId").asText())
                                        .build())
                                .price(sku.get("salePrice").asLong())
                                .build());
                    }
                    itemOptions.setSizePrices(sizePrices);
                    itemOptions.setImageUrl(
                            sliceItem.get("swatchUrl").asText().substring(0, sliceItem.get("swatchUrl").asText().length() - 1) + sliceItem.get("images").get(0).asText()
                    );
                    colorSizesList.add(itemOptions);
                }
                newItemDetails.setOptions(colorSizesList);
            }
        }
        return newItemDetails;
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
