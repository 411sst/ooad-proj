package com.moviebooking.controller;

import com.moviebooking.dto.ApiResponse;
import com.moviebooking.dto.SeatLockRequest;
import com.moviebooking.entity.SeatLock;
import com.moviebooking.entity.User;
import com.moviebooking.service.SeatService;
import com.moviebooking.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    private final SeatService seatService;
    private final UserService userService;

    public SeatController(SeatService seatService, UserService userService) {
        this.seatService = seatService;
        this.userService = userService;
    }

    @GetMapping("/showtime/{showtimeId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSeatMap(@PathVariable Long showtimeId) {
        Map<String, Object> seatMap = seatService.getSeatMapForShowtime(showtimeId);
        return ResponseEntity.ok(ApiResponse.success("Seat map retrieved", seatMap));
    }

    @PostMapping("/lock")
    public ResponseEntity<ApiResponse<Object>> lockSeats(@Valid @RequestBody SeatLockRequest request,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        List<SeatLock> locks = seatService.lockSeats(request.getShowtimeId(), request.getSeatIds(), user);
        return ResponseEntity.ok(ApiResponse.success("Seats locked for 10 minutes",
                Map.of("lockedSeats", locks.size(), "expiresAt", locks.get(0).getLockedUntil())));
    }

    @PostMapping("/unlock")
    public ResponseEntity<ApiResponse<Void>> unlockSeats(@Valid @RequestBody SeatLockRequest request,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        seatService.unlockSeats(request.getShowtimeId(), request.getSeatIds(), user);
        return ResponseEntity.ok(ApiResponse.success("Seats unlocked"));
    }
}
