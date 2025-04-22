package com.peakmeshop.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface BaseMapper {
    @Named("toLocalDateTime")
    default String toLocalDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
    }
    
    @Named("fromLocalDateTime")
    default LocalDateTime fromLocalDateTime(String dateTime) {
        return dateTime != null ? LocalDateTime.parse(dateTime) : null;
    }

    @Named("toString")
    default String toString(Object obj) {
        return obj != null ? obj.toString() : null;
    }

    @Named("toDouble")
    default Double toDouble(String value) {
        return value != null ? Double.parseDouble(value) : null;
    }

    @Named("toLong")
    default Long toLong(String value) {
        return value != null ? Long.parseLong(value) : null;
    }
} 