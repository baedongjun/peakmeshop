package com.peakmeshop.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class LongListConverter implements AttributeConverter<List<Long>, String> {

    private static final String DELIMITER = ",";

    @Override
    public String convertToDatabaseColumn(List<Long> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "";
        }
        return attribute.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(DELIMITER));
    }

    @Override
    public List<Long> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(dbData.split(DELIMITER))
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }
}