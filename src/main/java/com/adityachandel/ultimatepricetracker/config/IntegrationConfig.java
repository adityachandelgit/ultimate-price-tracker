package com.adityachandel.ultimatepricetracker.config;

import com.adityachandel.ultimatepricetracker.config.model.StoreCookieProperties;
import com.adityachandel.ultimatepricetracker.config.model.EmailProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IntegrationConfig {

    @Bean
    @ConfigurationProperties(("app.email"))
    public EmailProperties emailProperties() {
        return new EmailProperties();
    }

    @Bean
    @ConfigurationProperties(("app.store-cookie"))
    public StoreCookieProperties amazonProperties() {
        return new StoreCookieProperties();
    }

}
