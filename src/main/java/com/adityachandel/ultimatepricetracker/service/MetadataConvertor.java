package com.adityachandel.ultimatepricetracker.service;

import com.adityachandel.ultimatepricetracker.model.Metadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.SneakyThrows;

@Converter
public class MetadataConvertor implements AttributeConverter<Metadata, String> {

    private final ObjectMapper objectMapper;

    public MetadataConvertor() {
        objectMapper = new ObjectMapper();
    }

    @SneakyThrows
    @Override
    public String convertToDatabaseColumn(Metadata attribute) {
        return attribute == null ? null : objectMapper.writeValueAsString(attribute);
    }

    @SneakyThrows
    @Override
    public Metadata convertToEntityAttribute(String dbData) {
        return dbData == null ? null : objectMapper.readValue(dbData, Metadata.class);
    }
}
