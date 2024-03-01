package com.adityachandel.ultimatepricetracker.stores;

import com.adityachandel.ultimatepricetracker.FetchException;
import com.adityachandel.ultimatepricetracker.ItemUtils;
import com.adityachandel.ultimatepricetracker.model.Item;
import com.adityachandel.ultimatepricetracker.model.NewItemDetails;
import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;

@AllArgsConstructor
@Service
@Slf4j
public class MaxAroma implements Store {

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
        NewItemDetails newItemDetails = NewItemDetails.builder()
                .id(itemId)
                .store(StoreType.MAXAROMA)
                .url("https://www.maxaroma.com/" + itemId)
                .build();

        try {
            Document document = Jsoup.connect(newItemDetails.getUrl()).get();
            Elements prodPrices = document.body().getElementsByClass("prodprice");
            String rawPrice = prodPrices.get(0).text().trim();
            Long price = (long) Double.parseDouble(rawPrice.substring(1));

            Elements dtlName = document.body().getElementsByClass("dtl_name");
            String itemName = dtlName.get(0).select("div").getFirst().text().trim();

            Elements story = document.body().getElementsByClass("prd-dtl-story");
            Elements images = story.select("img");
            String imageUrl = null;
            for (Element e : images) {
                if (e.attr("src").contains("two.png")) {
                    imageUrl = e.attr("src");
                    break;
                }
            }

            newItemDetails.setName(itemName);
            newItemDetails.setImageUrl(imageUrl);
            newItemDetails.setPrice(price);

            return newItemDetails;

        } catch (IOException e) {
            log.error("URL: " + newItemDetails.getImageUrl());
            throw new FetchException(e.getMessage(), e);
        }

    }

}
