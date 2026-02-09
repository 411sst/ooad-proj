package com.moviebooking.dto;

import com.moviebooking.entity.Booking;
import com.moviebooking.entity.BookingFood;
import com.moviebooking.entity.BookingSeat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class BookingDto {
    private Long id;
    private String bookingReference;
    private String movieTitle;
    private String theaterName;
    private String screenName;
    private LocalDateTime showDatetime;
    private int numSeats;
    private List<String> seatLabels;
    private BigDecimal ticketAmount;
    private BigDecimal foodAmount;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private String status;
    private String qrCodeUrl;
    private LocalDateTime bookingDatetime;
    private List<FoodItemDto> foodItems;

    @Getter
    @Setter
    public static class FoodItemDto {
        private String name;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
    }

    public static BookingDto fromEntity(Booking b, List<BookingSeat> seats, List<BookingFood> foods) {
        BookingDto dto = new BookingDto();
        dto.setId(b.getId());
        dto.setBookingReference(b.getBookingReference());
        dto.setMovieTitle(b.getMovie().getTitle());
        dto.setTheaterName(b.getTheater().getName());
        dto.setScreenName(b.getScreen().getScreenName());
        dto.setShowDatetime(b.getShowtime().getShowDatetime());
        dto.setNumSeats(b.getNumSeats());
        dto.setSeatLabels(seats.stream().map(bs -> bs.getSeat().getSeatLabel()).collect(Collectors.toList()));
        dto.setTicketAmount(b.getTicketAmount());
        dto.setFoodAmount(b.getFoodAmount());
        dto.setTaxAmount(b.getTaxAmount());
        dto.setDiscountAmount(b.getDiscountAmount());
        dto.setTotalAmount(b.getTotalAmount());
        dto.setStatus(b.getStatus().name());
        dto.setQrCodeUrl(b.getQrCodeUrl());
        dto.setBookingDatetime(b.getBookingDatetime());

        dto.setFoodItems(foods.stream().map(bf -> {
            FoodItemDto fi = new FoodItemDto();
            fi.setName(bf.getFoodItem().getName());
            fi.setQuantity(bf.getQuantity());
            fi.setUnitPrice(bf.getUnitPrice());
            fi.setSubtotal(bf.getSubtotal());
            return fi;
        }).collect(Collectors.toList()));

        return dto;
    }
}
