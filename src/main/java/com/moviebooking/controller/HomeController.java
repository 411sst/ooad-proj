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

    @GetMapping("/movies")
    public String moviesPage() {
        return "movies";
    }

    @GetMapping("/movies/{id}")
    public String movieDetailPage(@PathVariable Long id) {
        return "movie-detail";
    }

    @GetMapping("/admin")
    public String adminDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/admin/movies")
    public String adminMovies() {
        return "admin/movies";
    }

    @GetMapping("/admin/theaters")
    public String adminTheaters() {
        return "admin/theaters";
    }

    @GetMapping("/admin/showtimes")
    public String adminShowtimes() {
        return "admin/showtimes";
    }

    @GetMapping("/admin/users")
    public String adminUsers() {
        return "admin/users";
    }

    @GetMapping("/admin/analytics")
    public String adminAnalytics() {
        return "admin/analytics";
    }
}
