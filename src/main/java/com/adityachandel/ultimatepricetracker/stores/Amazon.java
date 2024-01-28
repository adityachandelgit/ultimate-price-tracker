package com.adityachandel.ultimatepricetracker.stores;


import com.adityachandel.ultimatepricetracker.FetchException;
import com.adityachandel.ultimatepricetracker.ItemUtils;
import com.adityachandel.ultimatepricetracker.config.model.StoreCookieProperties;
import com.adityachandel.ultimatepricetracker.model.Item;
import com.adityachandel.ultimatepricetracker.model.NewItemDetails;
import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.Objects;

@AllArgsConstructor
@Service
@Slf4j
public class Amazon implements Store {

    private final StoreCookieProperties cookieProperties;

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
        String url = buildUrl(itemId);
        Element root = null;
        try {
            root = Jsoup.parse(doGet1(url)).body();
            Elements elements = root.select("div[id=aod-offer]");
            int lowestPrice = Integer.MAX_VALUE;
            for (Element element : elements) {
                String soldByInner = Objects.requireNonNull(element.getElementById("aod-offer-soldBy")).text();
                int price = Integer.parseInt(element.getElementsByClass("a-price-whole").get(0).text().split("\\.")[0].replace(",", ""));
                if (soldByInner.contains("Amazon Warehouse")) {
                    lowestPrice = Math.min(lowestPrice, price);
                }
            }
            if (lowestPrice == Integer.MAX_VALUE) {
                lowestPrice = Integer.parseInt(root.select("span[class=a-price-whole]").get(0).text().split("\\.")[0].replace(",", ""));
            }
            String name = root.select("div[id=aod-asin-title]").get(0).text();
            String image = root.select("img[id=aod-asin-image-id]").attr("src");
            return NewItemDetails.builder()
                    .id(itemId)
                    .store(StoreType.AMAZON)
                    .imageUrl(image)
                    .name(name)
                    .url("https://www.amazon.com/dp/" + itemId)
                    .price((long) lowestPrice)
                    .build();
        } catch (Exception e) {
            assert root != null;
            log.error("URL: " + url);
            log.error("Page: " + root.html());
            throw new FetchException(e.getMessage(), e);
        }
    }

    private String doGet1(String url) {
        HttpResponse<String> response = Unirest.get(url)
                .header("authority", "www.amazon.com")
                .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .header("accept-language", "en-US,en;q=0.9")
                .header("cookie", cookieProperties.getAmazon())
                .header("device-memory", "8")
                .header("downlink", "7.1")
                .header("dpr", "2")
                .header("ect", "4g")
                .header("rtt", "50")
                .header("sec-ch-device-memory", "8")
                .header("sec-ch-dpr", "2")
                .header("sec-ch-ua", "\"Not_A Brand\";v=\"8\", \"Chromium\";v=\"120\", \"Google Chrome\";v=\"120\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("sec-ch-ua-platform", "\"macOS\"")
                .header("sec-ch-viewport-width", "689")
                .header("sec-fetch-dest", "document")
                .header("sec-fetch-mode", "navigate")
                .header("sec-fetch-site", "none")
                .header("sec-fetch-user", "?1")
                .header("upgrade-insecure-requests", "1")
                .header("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .header("viewport-width", "689")
                .asString();
        return response.getBody();
    }


    private String buildUrl(String itemId) {
        return "https://www.amazon.com/gp/product/ajax/ref=dp_aod_ALL_mbc?asin=" + itemId + "&m=&qid=&smid=&sourcecustomerorglistid=&sourcecustomerorglistitemid=&sr=&pc=dp&experienceId=aodAjaxMain";
    }

}
