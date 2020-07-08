package org.slos.util;

import org.slf4j.MDC;

import java.util.Map;
import java.util.function.Supplier;

public class WithMDC {


    public static Runnable run(Runnable runnable) {
        Map<String, String> mdc = MDC.getCopyOfContextMap();
        return () -> {
            MDC.setContextMap(mdc);
            runnable.run();
        };
    }

    public static <U> Supplier<U> run(Supplier<U> supplier) {
        Map<String, String> mdc = MDC.getCopyOfContextMap();
        return (Supplier) () -> {
            MDC.setContextMap(mdc);
            return supplier.get();
        };
    }
}
