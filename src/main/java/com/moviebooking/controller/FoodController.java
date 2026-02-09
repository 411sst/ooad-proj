package com.moviebooking.controller;

import com.moviebooking.dto.ApiResponse;
import com.moviebooking.dto.FoodOrderRequest;
import com.moviebooking.entity.FoodItem;
import com.moviebooking.entity.enums.FoodCategory;
import com.moviebooking.patterns.decorator.BookingComponent;
import com.moviebooking.service.FoodService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/food")
public class FoodController {

    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping("/menu")
    public ResponseEntity<ApiResponse<List<FoodItem>>> getMenu() {
        List<FoodItem> items = foodService.getAvailableItems();
        return ResponseEntity.ok(ApiResponse.success("Menu retrieved", items));
    }

    @GetMapping("/menu/{category}")
    public ResponseEntity<ApiResponse<List<FoodItem>>> getMenuByCategory(@PathVariable String category) {
        FoodCategory cat = FoodCategory.valueOf(category.toUpperCase());
        List<FoodItem> items = foodService.getItemsByCategory(cat);
        return ResponseEntity.ok(ApiResponse.success("Menu items retrieved", items));
    }

    @PostMapping("/order")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addFoodToBooking(
            @Valid @RequestBody FoodOrderRequest request) {
        BookingComponent decorated = foodService.addFoodToBooking(request.getBookingId(), request.getItems());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("description", decorated.getDescription());
        result.put("totalCost", decorated.getCost());
        result.put("itemDetails", decorated.getItemDetails());

        return ResponseEntity.ok(ApiResponse.success("Food items added to booking", result));
    }
}
