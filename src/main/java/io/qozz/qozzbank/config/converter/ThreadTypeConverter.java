package io.qozz.qozzbank.config.converter;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class ThreadTypeConverter extends ClassicConverter {
    @Override
    public String convert(ILoggingEvent event) {
        return Thread.currentThread().isVirtual() ? "V" : "P";
    }
}
