package com.moviebooking.service;

import com.moviebooking.entity.SeatLock;
import com.moviebooking.patterns.observer.SeatAvailabilitySubject;
import com.moviebooking.patterns.observer.SeatUpdateEvent;
import com.moviebooking.repository.SeatLockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Scheduled job that releases expired seat locks every 60 seconds.
 * Broadcasts seat availability updates via Observer Pattern.
 */
@Component
public class SeatLockScheduler {

    private static final Logger log = LoggerFactory.getLogger(SeatLockScheduler.class);

    private final SeatLockRepository seatLockRepository;
    private final SeatAvailabilitySubject seatAvailabilitySubject;

    public SeatLockScheduler(SeatLockRepository seatLockRepository,
                             SeatAvailabilitySubject seatAvailabilitySubject) {
        this.seatLockRepository = seatLockRepository;
        this.seatAvailabilitySubject = seatAvailabilitySubject;
    }

    @Scheduled(fixedRate = 60000) // Every 60 seconds
    @Transactional
    public void releaseExpiredLocks() {
        LocalDateTime now = LocalDateTime.now();
        List<SeatLock> expiredLocks = seatLockRepository.findExpiredLocks(now);

        if (expiredLocks.isEmpty()) return;

        log.info("Releasing {} expired seat locks", expiredLocks.size());

        // Group by showtime for broadcasting
        Map<Long, List<SeatLock>> byShowtime = expiredLocks.stream()
                .collect(Collectors.groupingBy(sl -> sl.getShowtime().getId()));

        // Deactivate expired locks
        for (SeatLock lock : expiredLocks) {
            lock.setIsActive(false);
        }
        seatLockRepository.saveAll(expiredLocks);

        // Broadcast updates per showtime
        for (Map.Entry<Long, List<SeatLock>> entry : byShowtime.entrySet()) {
            List<SeatUpdateEvent.SeatStatusDto> seatDtos = entry.getValue().stream()
                    .map(sl -> new SeatUpdateEvent.SeatStatusDto(
                            sl.getSeat().getId(), sl.getSeat().getSeatLabel(), "AVAILABLE", null))
                    .collect(Collectors.toList());

            SeatUpdateEvent event = new SeatUpdateEvent(entry.getKey(), seatDtos, "EXPIRED", null, now);
            seatAvailabilitySubject.notifyObservers(event);
        }

        log.info("Released expired locks for {} showtimes", byShowtime.size());
    }
}
