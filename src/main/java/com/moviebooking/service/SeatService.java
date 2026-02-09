package com.moviebooking.service;

import com.moviebooking.entity.*;
import com.moviebooking.exception.BadRequestException;
import com.moviebooking.exception.ResourceNotFoundException;
import com.moviebooking.patterns.observer.SeatAvailabilitySubject;
import com.moviebooking.patterns.observer.SeatUpdateEvent;
import com.moviebooking.patterns.strategy.PricingEngine;
import com.moviebooking.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SeatService {

    private static final Logger log = LoggerFactory.getLogger(SeatService.class);
    private static final int MAX_SEATS_PER_BOOKING = 10;
    private static final int LOCK_DURATION_MINUTES = 10;

    private final SeatRepository seatRepository;
    private final SeatLockRepository seatLockRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatAvailabilitySubject seatAvailabilitySubject;
    private final PricingEngine pricingEngine;

    public SeatService(SeatRepository seatRepository, SeatLockRepository seatLockRepository,
                       BookingSeatRepository bookingSeatRepository, ShowtimeRepository showtimeRepository,
                       SeatAvailabilitySubject seatAvailabilitySubject, PricingEngine pricingEngine) {
        this.seatRepository = seatRepository;
        this.seatLockRepository = seatLockRepository;
        this.bookingSeatRepository = bookingSeatRepository;
        this.showtimeRepository = showtimeRepository;
        this.seatAvailabilitySubject = seatAvailabilitySubject;
        this.pricingEngine = pricingEngine;
    }

    public Map<String, Object> getSeatMapForShowtime(Long showtimeId) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime", "id", showtimeId));

        List<Seat> seats = seatRepository.findByScreenId(showtime.getScreen().getId());
        List<Long> bookedSeatIds = bookingSeatRepository.findBookedSeatIdsForShowtime(showtimeId);
        List<Long> lockedSeatIds = seatLockRepository.findLockedSeatIdsForShowtime(showtimeId, LocalDateTime.now());

        List<Map<String, Object>> seatData = seats.stream().map(seat -> {
            Map<String, Object> seatInfo = new LinkedHashMap<>();
            seatInfo.put("id", seat.getId());
            seatInfo.put("label", seat.getSeatLabel());
            seatInfo.put("row", seat.getRowLetter());
            seatInfo.put("number", seat.getSeatNumber());
            seatInfo.put("type", seat.getSeatType().name());
            // Strategy Pattern: Apply dynamic pricing
            seatInfo.put("basePrice", seat.getBasePrice());
            seatInfo.put("price", pricingEngine.calculateFinalPrice(seat.getBasePrice(), showtime));

            if (bookedSeatIds.contains(seat.getId())) {
                seatInfo.put("status", "BOOKED");
            } else if (lockedSeatIds.contains(seat.getId())) {
                seatInfo.put("status", "LOCKED");
            } else if (!seat.getIsAvailable()) {
                seatInfo.put("status", "UNAVAILABLE");
            } else {
                seatInfo.put("status", "AVAILABLE");
            }
            return seatInfo;
        }).collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("showtimeId", showtimeId);
        result.put("screenName", showtime.getScreen().getScreenName());
        result.put("rows", showtime.getScreen().getRows());
        result.put("columns", showtime.getScreen().getColumns());
        result.put("totalSeats", showtime.getTotalSeats());
        result.put("availableSeats", showtime.getAvailableSeats());
        result.put("pricingStrategy", showtime.getPricingStrategy());
        result.put("seats", seatData);
        return result;
    }

    @Transactional
    public List<SeatLock> lockSeats(Long showtimeId, List<Long> seatIds, User user) {
        if (seatIds.size() > MAX_SEATS_PER_BOOKING) {
            throw new BadRequestException("Maximum " + MAX_SEATS_PER_BOOKING + " seats allowed per booking");
        }

        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime", "id", showtimeId));

        List<Long> bookedSeatIds = bookingSeatRepository.findBookedSeatIdsForShowtime(showtimeId);
        List<Long> lockedSeatIds = seatLockRepository.findLockedSeatIdsForShowtime(showtimeId, LocalDateTime.now());

        for (Long seatId : seatIds) {
            if (bookedSeatIds.contains(seatId)) {
                throw new BadRequestException("Seat is already booked");
            }
            if (lockedSeatIds.contains(seatId)) {
                throw new BadRequestException("Seat is currently locked by another user");
            }
        }

        // Release any existing locks by this user for this showtime
        List<SeatLock> existingLocks = seatLockRepository.findByUserIdAndShowtimeIdAndIsActiveTrue(user.getId(), showtimeId);
        for (SeatLock lock : existingLocks) {
            lock.setIsActive(false);
        }
        seatLockRepository.saveAll(existingLocks);

        List<Seat> seats = seatRepository.findByIdIn(seatIds);
        LocalDateTime now = LocalDateTime.now();
        List<SeatLock> newLocks = new ArrayList<>();

        for (Seat seat : seats) {
            SeatLock lock = new SeatLock();
            lock.setSeat(seat);
            lock.setShowtime(showtime);
            lock.setUser(user);
            lock.setLockedAt(now);
            lock.setLockedUntil(now.plusMinutes(LOCK_DURATION_MINUTES));
            lock.setIsActive(true);
            newLocks.add(lock);
        }

        List<SeatLock> savedLocks = seatLockRepository.saveAll(newLocks);

        // Observer Pattern: Broadcast seat status change
        broadcastSeatUpdate(showtimeId, seats, "LOCKED", user.getId());

        log.info("User {} locked {} seats for showtime {}", user.getEmail(), seatIds.size(), showtimeId);
        return savedLocks;
    }

    @Transactional
    public void unlockSeats(Long showtimeId, List<Long> seatIds, User user) {
        List<SeatLock> locks = seatLockRepository.findByUserIdAndShowtimeIdAndIsActiveTrue(user.getId(), showtimeId);
        List<Seat> releasedSeats = new ArrayList<>();

        for (SeatLock lock : locks) {
            if (seatIds.contains(lock.getSeat().getId())) {
                lock.setIsActive(false);
                releasedSeats.add(lock.getSeat());
            }
        }
        seatLockRepository.saveAll(locks);

        if (!releasedSeats.isEmpty()) {
            broadcastSeatUpdate(showtimeId, releasedSeats, "RELEASED", user.getId());
        }
    }

    @Transactional
    public void releaseLocksForBooking(Long showtimeId, Long userId) {
        List<SeatLock> locks = seatLockRepository.findByUserIdAndShowtimeIdAndIsActiveTrue(userId, showtimeId);
        List<Seat> releasedSeats = new ArrayList<>();
        for (SeatLock lock : locks) {
            lock.setIsActive(false);
            releasedSeats.add(lock.getSeat());
        }
        seatLockRepository.saveAll(locks);

        if (!releasedSeats.isEmpty()) {
            broadcastSeatUpdate(showtimeId, releasedSeats, "BOOKED", userId);
        }
    }

    private void broadcastSeatUpdate(Long showtimeId, List<Seat> seats, String eventType, Long userId) {
        List<SeatUpdateEvent.SeatStatusDto> seatDtos = seats.stream()
                .map(s -> new SeatUpdateEvent.SeatStatusDto(s.getId(), s.getSeatLabel(),
                        eventType.equals("RELEASED") ? "AVAILABLE" : eventType, userId))
                .collect(Collectors.toList());

        SeatUpdateEvent event = new SeatUpdateEvent(showtimeId, seatDtos, eventType, userId, LocalDateTime.now());
        seatAvailabilitySubject.notifyObservers(event);
    }
}
