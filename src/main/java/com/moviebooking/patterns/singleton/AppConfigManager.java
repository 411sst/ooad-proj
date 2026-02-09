package com.moviebooking.patterns.singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton Pattern - Application Configuration Manager.
 * Manages app-wide configuration settings like pricing multipliers,
 * lock timeouts, booking limits, etc.
 * Thread-safe with double-checked locking.
 */
public class AppConfigManager {

    private static final Logger log = LoggerFactory.getLogger(AppConfigManager.class);

    private static volatile AppConfigManager instance;
    private final Map<String, String> configMap;

    // Private constructor prevents external instantiation
    private AppConfigManager() {
        configMap = new ConcurrentHashMap<>();
        loadDefaultConfig();
        log.info("AppConfigManager initialized with default configuration");
    }

    /**
     * Double-checked locking for thread-safe lazy initialization.
     */
    public static AppConfigManager getInstance() {
        if (instance == null) {
            synchronized (AppConfigManager.class) {
                if (instance == null) {
                    instance = new AppConfigManager();
                }
            }
        }
        return instance;
    }

    private void loadDefaultConfig() {
        // Seat lock settings
        configMap.put("seat.lock.timeout.minutes", "10");
        configMap.put("seat.max.per.booking", "10");

        // Pricing settings
        configMap.put("pricing.gst.rate", "0.18");
        configMap.put("pricing.peak.multiplier", "1.30");
        configMap.put("pricing.weekend.multiplier", "1.20");
        configMap.put("pricing.holiday.multiplier", "1.40");
        configMap.put("pricing.morning.discount", "0.80");
        configMap.put("pricing.high.demand.threshold", "0.70");
        configMap.put("pricing.high.demand.multiplier", "1.25");
        configMap.put("pricing.low.demand.multiplier", "0.90");

        // Peak hours (evening shows)
        configMap.put("pricing.peak.start", "18:00");
        configMap.put("pricing.peak.end", "22:00");

        // Morning show hours
        configMap.put("pricing.morning.start", "08:00");
        configMap.put("pricing.morning.end", "12:00");

        // Booking settings
        configMap.put("booking.cancellation.full.refund.hours", "24");
        configMap.put("booking.cancellation.half.refund.hours", "6");
        configMap.put("booking.max.advance.days", "7");

        // QR Code settings
        configMap.put("qr.code.width", "300");
        configMap.put("qr.code.height", "300");

        // Application info
        configMap.put("app.name", "MovieBook");
        configMap.put("app.version", "1.0.0");
        configMap.put("app.currency", "INR");
        configMap.put("app.currency.symbol", "₹");
    }

    public String get(String key) {
        return configMap.get(key);
    }

    public String get(String key, String defaultValue) {
        return configMap.getOrDefault(key, defaultValue);
    }

    public int getInt(String key) {
        return Integer.parseInt(configMap.get(key));
    }

    public int getInt(String key, int defaultValue) {
        String val = configMap.get(key);
        return val != null ? Integer.parseInt(val) : defaultValue;
    }

    public BigDecimal getBigDecimal(String key) {
        return new BigDecimal(configMap.get(key));
    }

    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        String val = configMap.get(key);
        return val != null ? new BigDecimal(val) : defaultValue;
    }

    public LocalTime getTime(String key) {
        return LocalTime.parse(configMap.get(key));
    }

    public void set(String key, String value) {
        configMap.put(key, value);
        log.info("Config updated: {} = {}", key, value);
    }

    public Map<String, String> getAllConfig() {
        return Map.copyOf(configMap);
    }
}
