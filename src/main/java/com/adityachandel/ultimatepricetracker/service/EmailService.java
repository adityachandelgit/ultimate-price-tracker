package com.adityachandel.ultimatepricetracker.service;

import com.adityachandel.ultimatepricetracker.config.model.EmailProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Slf4j
public class EmailService {

    private final EmailProperties emailProperties;
    private final JavaMailSender javaMailSender;

    public EmailService(EmailProperties emailProperties, JavaMailSender javaMailSender) {
        this.emailProperties = emailProperties;
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(String messageSubject, String messageBody) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(emailProperties.getTo());
        msg.setSubject(messageSubject);
        msg.setText(messageBody);
        javaMailSender.send(msg);
        log.info("Email sent to " + emailProperties.getTo() + " at: " + Instant.now().toString());
    }

}
