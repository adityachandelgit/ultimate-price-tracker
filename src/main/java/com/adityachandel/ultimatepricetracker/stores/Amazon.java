package com.adityachandel.ultimatepricetracker.stores;


import com.adityachandel.ultimatepricetracker.FetchException;
import com.adityachandel.ultimatepricetracker.config.model.StoreCookieProperties;
import com.adityachandel.ultimatepricetracker.model.NewItemInfo;
import com.adityachandel.ultimatepricetracker.model.Item;
import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.Objects;

@AllArgsConstructor
@Service
@Slf4j
public class Amazon implements Store {

    private final StoreCookieProperties cookieProperties;

    @Override
    public Item fetchItem(Item item) {
        item.setUrl("https://www.amazon.com/dp/" + item.getExternalId());
        return getItem(item);
    }

    @Override
    public NewItemInfo getNewItemInfo(String itemId) {
        return null;
    }

    public Item getItem(Item item) {
        String url = buildUrl(item.getExternalId());
        Element root = null;
        try {
            root = Jsoup.parse(doGet1(url)).body();
            Elements elements = root.select("div[id=aod-offer]");

            int lowestPrice = Integer.MAX_VALUE;
            String condition = "";
            String soldBy = "";
            for (Element element : elements) {
                String soldByInner = Objects.requireNonNull(element.getElementById("aod-offer-soldBy")).text();
                String conditionInner = Objects.requireNonNull(element.getElementById("aod-offer-heading")).text();
                int price = Integer.parseInt(element.getElementsByClass("a-price-whole").get(0).text().split("\\.")[0].replace(",", ""));
                if (soldByInner.contains("Amazon Warehouse")) {
                    lowestPrice = Math.min(lowestPrice, price);
                    soldBy = soldByInner;
                    condition = conditionInner;
                }
            }

            if (lowestPrice == Integer.MAX_VALUE) {
                lowestPrice = Integer.parseInt(root.select("span[class=a-price-whole]").get(0).text().split("\\.")[0].replace(",", ""));
                condition = "New";
                soldBy = "Amazon.com";
            }

            String name = root.select("div[id=aod-asin-title]").get(0).text();
            String image = root.select("img[id=aod-asin-image-id]").attr("src");

            item.setName(name);
            item.setImageUrl(image);
            item.setLatestPrice(lowestPrice);
            item.setLatestPriceTimestamp(Instant.now());

            return item;
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

    private String doGet(String url) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("authority", "www.amazon.com")
                .addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .addHeader("accept-language", "en-US,en;q=0.9")
                .addHeader("cookie", cookieProperties.getAmazon())
                .addHeader("device-memory", "8")
                .addHeader("downlink", "7.1")
                .addHeader("dpr", "2")
                .addHeader("ect", "4g")
                .addHeader("rtt", "50")
                .addHeader("sec-ch-device-memory", "8")
                .addHeader("sec-ch-dpr", "2")
                .addHeader("sec-ch-ua", "\"Not_A Brand\";v=\"8\", \"Chromium\";v=\"120\", \"Google Chrome\";v=\"120\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .addHeader("sec-ch-ua-platform", "\"macOS\"")
                .addHeader("sec-ch-viewport-width", "689")
                .addHeader("sec-fetch-dest", "document")
                .addHeader("sec-fetch-mode", "navigate")
                .addHeader("sec-fetch-site", "none")
                .addHeader("sec-fetch-user", "?1")
                .addHeader("upgrade-insecure-requests", "1")
                .addHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .addHeader("viewport-width", "689")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private String buildUrl(String itemId) {
        return "https://www.amazon.com/gp/product/ajax/ref=dp_aod_ALL_mbc?asin=" + itemId + "&m=&qid=&smid=&sourcecustomerorglistid=&sourcecustomerorglistitemid=&sr=&pc=dp&experienceId=aodAjaxMain";
    }

}
