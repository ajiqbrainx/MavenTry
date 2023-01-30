package com.qbrainx.common.converter;

import com.fasterxml.jackson.databind.util.StdConverter;
import com.qbrainx.common.lang.util.ObjectUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateConverter extends StdConverter<String, LocalDate> {
    @Override
    public LocalDate convert(final String value) {
        if (ObjectUtil.isEmpty(value) || value.equals("NULL") || value.equals("0000-00-00")) {
            return null;
        }
        try {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            throw new IllegalStateException("Unable to parse date", e);
        }
    }
}