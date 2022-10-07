package ru.ngs.summerjob.stproject.util;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

    public static final String PATTERN = "dd.MM.yyyy";

    @Override
    public LocalDate unmarshal(String s) {
        return LocalDate.parse(s, DateTimeFormatter.ofPattern(PATTERN));
    }

    @Override
    public String marshal(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern(PATTERN));
    }
}
