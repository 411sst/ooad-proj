package com.moviebooking.service;

import com.moviebooking.entity.BookingFood;
import com.moviebooking.entity.FoodItem;
import com.moviebooking.entity.Booking;
import com.moviebooking.entity.enums.FoodCategory;
import com.moviebooking.exception.BadRequestException;
import com.moviebooking.exception.ResourceNotFoundException;
import com.moviebooking.patterns.decorator.*;
import com.moviebooking.repository.BookingFoodRepository;
import com.moviebooking.repository.FoodItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class FoodService {

    private static final Logger log = LoggerFactory.getLogger(FoodService.class);

    private final FoodItemRepository foodItemRepository;
    private final BookingFoodRepository bookingFoodRepository;
    private final BookingService bookingService;

    public FoodService(FoodItemRepository foodItemRepository, BookingFoodRepository bookingFoodRepository,
                       BookingService bookingService) {
        this.foodItemRepository = foodItemRepository;
        this.bookingFoodRepository = bookingFoodRepository;
        this.bookingService = bookingService;
    }

    public List<FoodItem> getAvailableItems() {
        return foodItemRepository.findByIsAvailableTrue();
    }

    public List<FoodItem> getItemsByCategory(FoodCategory category) {
        return foodItemRepository.findByCategoryAndIsAvailableTrue(category);
    }

    /**
     * Adds F&B items to a booking using the Decorator Pattern.
     * Each food item wraps the booking with additional cost and description.
     *
     * @param bookingId the booking to add food to
     * @param items     map of foodItemId -> quantity
     * @return the decorated booking component with total cost
     */
    @Transactional
    public BookingComponent addFoodToBooking(Long bookingId, Map<Long, Integer> items) {
        Booking booking = bookingService.getBookingById(bookingId);

        // Remove existing food items for this booking
        List<BookingFood> existing = bookingFoodRepository.findByBookingId(bookingId);
        bookingFoodRepository.deleteAll(existing);

        // Start with base booking (Decorator Pattern)
        BookingComponent decorated = new BaseBooking(
                booking.getTicketAmount(),
                booking.getNumSeats(),
                booking.getMovie().getTitle()
        );

        BigDecimal totalFoodAmount = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : items.entrySet()) {
            Long foodItemId = entry.getKey();
            int quantity = entry.getValue();

            if (quantity <= 0) continue;

            FoodItem foodItem = foodItemRepository.findById(foodItemId)
                    .orElseThrow(() -> new ResourceNotFoundException("FoodItem", "id", foodItemId));

            if (!foodItem.getIsAvailable()) {
                throw new BadRequestException(foodItem.getName() + " is currently unavailable");
            }

            // Save booking-food record
            BookingFood bf = new BookingFood();
            bf.setBooking(booking);
            bf.setFoodItem(foodItem);
            bf.setQuantity(quantity);
            bf.setUnitPrice(foodItem.getPrice());
            bf.setSubtotal(foodItem.getPrice().multiply(BigDecimal.valueOf(quantity)));
            bookingFoodRepository.save(bf);

            totalFoodAmount = totalFoodAmount.add(bf.getSubtotal());

            // Apply appropriate decorator based on category
            decorated = switch (foodItem.getCategory()) {
                case POPCORN, SNACK -> new SnackDecorator(decorated, foodItem.getName(), foodItem.getPrice(), quantity);
                case BEVERAGE -> new BeverageDecorator(decorated, foodItem.getName(), foodItem.getPrice(), quantity);
                case COMBO -> new ComboDecorator(decorated, foodItem.getName(), foodItem.getPrice(), quantity);
            };
        }

        // Update booking with food amount
        bookingService.updateFoodAmount(bookingId, totalFoodAmount);

        log.info("Added {} food items to booking {}, food total: ₹{}",
                items.size(), booking.getBookingReference(), totalFoodAmount);

        return decorated;
    }

    public List<BookingFood> getBookingFoodItems(Long bookingId) {
        return bookingFoodRepository.findByBookingId(bookingId);
    }
}
