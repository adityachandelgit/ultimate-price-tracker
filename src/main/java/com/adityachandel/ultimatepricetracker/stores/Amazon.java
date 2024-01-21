package com.adityachandel.ultimatepricetracker.stores;


import com.adityachandel.ultimatepricetracker.FetchException;
import com.adityachandel.ultimatepricetracker.config.model.StoreCookieProperties;
import com.adityachandel.ultimatepricetracker.model.NewItemInfo;
import com.adityachandel.ultimatepricetracker.model.Item;
import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
    public NewItemInfo getNewItemInfo(StoreType storeType, String itemId) {
        return null;
    }

    public Item getItem(Item item) {
        String url = buildUrl(item.getExternalId());
        Element root = null;
        try {
            root = Jsoup.parse(doGet(url)).body();
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

    private String doGet(String url) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("authority", "www.amazon.com")
                .addHeader("accept", "text/html,*/*")
                .addHeader("accept-language", "en-US,en;q=0.9")
                .addHeader("cookie", cookieProperties.getAmazon())
                .addHeader("device-memory", "8")
                .addHeader("downlink", "10")
                .addHeader("dpr", "2")
                .addHeader("ect", "4g")
                .addHeader("referer", "https://www.amazon.com/SAMSUNG-Unlocked-Smartphone-Adaptive-Lavender/dp/B0BLP3TL8J/?_encoding=UTF8&pd_rd_w=ve1As&content-id=amzn1.sym.5f7e0a27-49c0-47d3-80b2-fd9271d863ca%3Aamzn1.symc.e5c80209-769f-4ade-a325-2eaec14b8e0e&pf_rd_p=5f7e0a27-49c0-47d3-80b2-fd9271d863ca&pf_rd_r=D9XECQ5WFYYRZ2WRWGFC&pd_rd_wg=8mjfY&pd_rd_r=ec08118a-6b01-4f3d-a771-ba9e617eb87e&ref_=pd_gw_ci_mcx_mr_hp_atf_m&th=1")
                .addHeader("rtt", "50")
                .addHeader("sec-ch-device-memory", "8")
                .addHeader("sec-ch-dpr", "2")
                .addHeader("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Google Chrome\";v=\"116\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .addHeader("sec-ch-ua-platform", "\"macOS\"")
                .addHeader("sec-ch-viewport-width", "838")
                .addHeader("sec-fetch-dest", "empty")
                .addHeader("sec-fetch-mode", "cors")
                .addHeader("sec-fetch-site", "same-origin")
                .addHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36")
                .addHeader("viewport-width", "838")
                .addHeader("x-requested-with", "XMLHttpRequest")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private String buildUrl(String itemId) {
        return "https://www.amazon.com/gp/product/ajax/ref=dp_aod_ALL_mbc?asin=" + itemId + "&m=&qid=&smid=&sourcecustomerorglistid=&sourcecustomerorglistitemid=&sr=&pc=dp&experienceId=aodAjaxMain";
    }

}
