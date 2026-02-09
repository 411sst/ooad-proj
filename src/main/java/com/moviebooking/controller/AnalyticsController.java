package com.moviebooking.controller;

import com.moviebooking.dto.ApiResponse;
import com.moviebooking.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSummary() {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getAnalyticsSummary()));
    }

    @GetMapping("/revenue/daily")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDailyRevenue(
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getDailyRevenue(days)));
    }

    @GetMapping("/bookings/status")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getBookingStatus() {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getBookingStatusDistribution()));
    }

    @GetMapping("/movies/performance")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getMoviePerformance() {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getMoviePerformance()));
    }

    @GetMapping("/theaters/occupancy")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTheaterOccupancy() {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getTheaterOccupancy()));
    }

    @GetMapping("/payments/methods")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getPaymentMethods() {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getPaymentMethodDistribution()));
    }
}
