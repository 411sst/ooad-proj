package com.moviebooking.config;

import com.moviebooking.patterns.observer.SeatAvailabilitySubject;
import com.moviebooking.patterns.observer.WebSocketSeatObserver;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Registers the WebSocket observer with the seat availability subject on startup.
 */
@Component
public class ObserverConfig {

    private static final Logger log = LoggerFactory.getLogger(ObserverConfig.class);

    private final SeatAvailabilitySubject subject;
    private final WebSocketSeatObserver webSocketObserver;

    public ObserverConfig(SeatAvailabilitySubject subject, WebSocketSeatObserver webSocketObserver) {
        this.subject = subject;
        this.webSocketObserver = webSocketObserver;
    }

    @PostConstruct
    public void init() {
        subject.addObserver(webSocketObserver);
        log.info("WebSocket seat observer registered with SeatAvailabilitySubject");
    }
}
