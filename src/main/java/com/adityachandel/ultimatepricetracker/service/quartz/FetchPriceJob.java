package com.adityachandel.ultimatepricetracker.service.quartz;

import com.adityachandel.ultimatepricetracker.FetchException;
import com.adityachandel.ultimatepricetracker.config.StoreConfig;
import com.adityachandel.ultimatepricetracker.model.Item;
import com.adityachandel.ultimatepricetracker.model.entity.EmailHistoryEntity;
import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import com.adityachandel.ultimatepricetracker.repository.EmailHistoryRepository;
import com.adityachandel.ultimatepricetracker.service.EmailService;
import com.adityachandel.ultimatepricetracker.service.ItemPriceHistoryService;
import com.adityachandel.ultimatepricetracker.service.ItemService;
import com.adityachandel.ultimatepricetracker.stores.Store;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.springframework.util.StringUtils.capitalize;

@Slf4j
@Component
@AllArgsConstructor
public class FetchPriceJob implements Job {

    private final ItemPriceHistoryService itemHistoryService;
    private final ItemService itemTrackService;
    private final EmailHistoryRepository emailHistoryRepository;
    private final StoreConfig storeConfig;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @Override
    public void execute(JobExecutionContext context) {
        String name = context.getJobDetail().getKey().getName();
        StoreType storeType = StoreType.valueOf(name);
        log.info("Starting cron: " + storeType.name());
        getLatestItems(storeType);
        log.info("Finished cron: " + storeType.name());
    }

    public void getLatestItems(StoreType amazon) {
        List<Item> items = itemTrackService.findByStoreAndTrackingEnabled(amazon, true);
        Collections.shuffle(items);
        Store store = storeConfig.getStore(amazon);
        for (Item item : items) {
            try {
                Item freshItemTrack = store.fetchItem(item);
                if (freshItemTrack != null) {
                    freshItemTrack.setId(item.getId());
                    log.info("Curr $" + String.format("%-6s", freshItemTrack.getLatestPrice()) + " | Want $" + String.format("%-6s", item.getDesiredPrice()) + " | " + item.getStore() + " | " + item.getName());
                    if (freshItemTrack.getLatestPrice() <= item.getDesiredPrice()) {
                        if (shouldSendEmail(item, freshItemTrack.getLatestPrice())) {
                            emailService.sendEmail(
                                    "♠ PTU: Price Drop | $" + freshItemTrack.getLatestPrice() + " | " + capitalize(freshItemTrack.getStore().name()) + " | " + freshItemTrack.getName(),
                                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(freshItemTrack)
                            );
                            emailHistoryRepository.save(EmailHistoryEntity.builder()
                                    .itemId(item.getId())
                                    .store(item.getStore())
                                    .price(freshItemTrack.getLatestPrice())
                                    .emailSentTimestamp(Instant.now())
                                    .build());
                        }
                    }
                    itemHistoryService.saveItemHistory(freshItemTrack);
                    itemTrackService.updateItemTrackWithNewDetails(freshItemTrack);
                }
                TimeUnit.SECONDS.sleep(5);
            } catch (FetchException | InterruptedException | IOException e) {
                log.error("Error in getLatestItems: " + e.getMessage());
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    private boolean shouldSendEmail(Item item, double latestPrice) {
        Optional<EmailHistoryEntity> emailHistoryOpt = emailHistoryRepository.findTopByItemIdAndStoreOrderByEmailSentTimestampDesc(item.getId(), item.getStore());
        if (emailHistoryOpt.isEmpty()) {
            return true;
        }
        EmailHistoryEntity emailHistory = emailHistoryOpt.get();
        if (latestPrice < emailHistory.getPrice()) {
            return true;
        } else {
            long days = ChronoUnit.DAYS.between(emailHistory.getEmailSentTimestamp(), Instant.now());
            return days >= 7;
        }
    }

}
