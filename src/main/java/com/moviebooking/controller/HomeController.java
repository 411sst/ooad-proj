package com.moviebooking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/seats/{showtimeId}")
    public String seatSelectionPage(@PathVariable Long showtimeId) {
        return "seats";
    }

    @GetMapping("/food/{bookingId}")
    public String foodSelectionPage(@PathVariable Long bookingId) {
        return "food";
    }

    @GetMapping("/payment/{bookingId}")
    public String paymentPage(@PathVariable Long bookingId) {
        return "payment";
    }

    @GetMapping("/booking/confirmation/{bookingId}")
    public String confirmationPage(@PathVariable Long bookingId) {
        return "confirmation";
    }

    @GetMapping("/bookings")
    public String bookingsPage() {
        return "bookings";
    }

    @GetMapping("/profile")
    public String profilePage() {
        return "profile";
    }
}
