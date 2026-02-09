package com.moviebooking.config;

import com.moviebooking.entity.*;
import com.moviebooking.entity.enums.*;
import com.moviebooking.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;
    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;
    private final ShowtimeRepository showtimeRepository;
    private final FoodItemRepository foodItemRepository;
    private final PromoCodeRepository promoCodeRepository;
    private final MovieCastRepository movieCastRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, MovieRepository movieRepository,
                          TheaterRepository theaterRepository, ScreenRepository screenRepository,
                          SeatRepository seatRepository, ShowtimeRepository showtimeRepository,
                          FoodItemRepository foodItemRepository, PromoCodeRepository promoCodeRepository,
                          MovieCastRepository movieCastRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.theaterRepository = theaterRepository;
        this.screenRepository = screenRepository;
        this.seatRepository = seatRepository;
        this.showtimeRepository = showtimeRepository;
        this.foodItemRepository = foodItemRepository;
        this.promoCodeRepository = promoCodeRepository;
        this.movieCastRepository = movieCastRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already initialized, skipping...");
            return;
        }

        log.info("Initializing database with sample data...");
        createUsers();
        List<Movie> movies = createMovies();
        List<Theater> theaters = createTheaters();
        createFoodItems();
        createPromoCodes();
        createShowtimes(movies, theaters);
        log.info("Database initialization complete!");
    }

    private void createUsers() {
        String encodedPassword = passwordEncoder.encode("Password123!");

        User customer1 = new User();
        customer1.setEmail("shrish@example.com");
        customer1.setPasswordHash(encodedPassword);
        customer1.setFirstName("Shrish");
        customer1.setLastName("Kumar");
        customer1.setPhone("9876543210");
        customer1.setDateOfBirth(LocalDate.of(2004, 5, 15));
        customer1.setGender("Male");
        customer1.setRole(UserRole.CUSTOMER);
        customer1.setIsActive(true);
        customer1.setEmailVerified(true);
        userRepository.save(customer1);

        User customer2 = new User();
        customer2.setEmail("vaishnav@example.com");
        customer2.setPasswordHash(encodedPassword);
        customer2.setFirstName("Vaishnav");
        customer2.setLastName("Reddy");
        customer2.setPhone("9876543211");
        customer2.setDateOfBirth(LocalDate.of(2004, 8, 20));
        customer2.setGender("Male");
        customer2.setRole(UserRole.CUSTOMER);
        customer2.setIsActive(true);
        customer2.setEmailVerified(true);
        userRepository.save(customer2);

        User customer3 = new User();
        customer3.setEmail("saffiya@example.com");
        customer3.setPasswordHash(encodedPassword);
        customer3.setFirstName("Saffiya");
        customer3.setLastName("Ahmed");
        customer3.setPhone("9876543212");
        customer3.setDateOfBirth(LocalDate.of(2004, 3, 10));
        customer3.setGender("Female");
        customer3.setRole(UserRole.CUSTOMER);
        customer3.setIsActive(true);
        customer3.setEmailVerified(true);
        userRepository.save(customer3);

        User manager = new User();
        manager.setEmail("manager@example.com");
        manager.setPasswordHash(encodedPassword);
        manager.setFirstName("Rushad");
        manager.setLastName("Manager");
        manager.setPhone("9876543213");
        manager.setDateOfBirth(LocalDate.of(1990, 1, 1));
        manager.setGender("Male");
        manager.setRole(UserRole.MANAGER);
        manager.setIsActive(true);
        manager.setEmailVerified(true);
        userRepository.save(manager);

        User admin = new User();
        admin.setEmail("admin@example.com");
        admin.setPasswordHash(encodedPassword);
        admin.setFirstName("System");
        admin.setLastName("Admin");
        admin.setPhone("9876543214");
        admin.setDateOfBirth(LocalDate.of(1985, 6, 15));
        admin.setGender("Male");
        admin.setRole(UserRole.ADMIN);
        admin.setIsActive(true);
        admin.setEmailVerified(true);
        userRepository.save(admin);

        log.info("Created 5 sample users");
    }

    private List<Movie> createMovies() {
        Movie m1 = createMovie("Avengers: Endgame", "The Avengers assemble once more to reverse Thanos' actions and restore balance to the universe.",
                "Action", "English", 181, "U/A", LocalDate.of(2026, 1, 15), new BigDecimal("8.4"), "Anthony Russo", "Kevin Feige", MovieStatus.NOW_SHOWING);
        Movie m2 = createMovie("Oppenheimer", "The story of American scientist J. Robert Oppenheimer and his role in the development of the atomic bomb.",
                "Drama", "English", 180, "A", LocalDate.of(2026, 1, 20), new BigDecimal("8.5"), "Christopher Nolan", "Emma Thomas", MovieStatus.NOW_SHOWING);
        Movie m3 = createMovie("Barbie", "Barbie suffers a crisis that leads her to question her world and her existence.",
                "Comedy", "English", 114, "U/A", LocalDate.of(2026, 2, 1), new BigDecimal("6.9"), "Greta Gerwig", "Margot Robbie", MovieStatus.NOW_SHOWING);
        Movie m4 = createMovie("Inception", "A thief who enters the dreams of others to steal secrets from their subconscious.",
                "Sci-Fi", "English", 148, "U/A", LocalDate.of(2026, 1, 10), new BigDecimal("8.8"), "Christopher Nolan", "Emma Thomas", MovieStatus.NOW_SHOWING);
        Movie m5 = createMovie("The Dark Knight", "Batman raises the stakes in his war on crime with the help of Lt. Jim Gordon and District Attorney Harvey Dent.",
                "Action", "English", 152, "U/A", LocalDate.of(2026, 1, 5), new BigDecimal("9.0"), "Christopher Nolan", "Charles Roven", MovieStatus.NOW_SHOWING);
        Movie m6 = createMovie("Interstellar", "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival.",
                "Sci-Fi", "English", 169, "U/A", LocalDate.of(2026, 2, 10), new BigDecimal("8.7"), "Christopher Nolan", "Lynda Obst", MovieStatus.NOW_SHOWING);
        Movie m7 = createMovie("RRR", "A fictional story about two legendary revolutionaries and their journey far from home.",
                "Action", "Telugu", 187, "U/A", LocalDate.of(2026, 1, 25), new BigDecimal("7.8"), "S.S. Rajamouli", "D.V.V. Danayya", MovieStatus.NOW_SHOWING);
        Movie m8 = createMovie("KGF Chapter 2", "In the blood-soaked Kolar Gold Fields, Rocky's name echoes with fear and dread.",
                "Action", "Kannada", 168, "U/A", LocalDate.of(2026, 2, 5), new BigDecimal("7.4"), "Prashanth Neel", "Vijay Kiragandur", MovieStatus.NOW_SHOWING);
        Movie m9 = createMovie("Pathaan", "An Indian spy takes on the leader of a group of mercenaries who have nefarious plans.",
                "Action", "Hindi", 146, "U/A", LocalDate.of(2026, 3, 1), new BigDecimal("6.5"), "Siddharth Anand", "Aditya Chopra", MovieStatus.UPCOMING);
        Movie m10 = createMovie("Jawan", "A man is driven by a personal vendetta to rectify the wrongs in society.",
                "Thriller", "Hindi", 169, "U/A", LocalDate.of(2026, 3, 15), new BigDecimal("7.1"), "Atlee", "Gauri Khan", MovieStatus.UPCOMING);

        List<Movie> movies = List.of(m1, m2, m3, m4, m5, m6, m7, m8, m9, m10);

        addCast(m1, "Robert Downey Jr.", CastRoleType.ACTOR, "Tony Stark");
        addCast(m1, "Chris Evans", CastRoleType.ACTOR, "Steve Rogers");
        addCast(m2, "Cillian Murphy", CastRoleType.ACTOR, "J. Robert Oppenheimer");
        addCast(m4, "Leonardo DiCaprio", CastRoleType.ACTOR, "Dom Cobb");
        addCast(m5, "Christian Bale", CastRoleType.ACTOR, "Bruce Wayne");
        addCast(m5, "Heath Ledger", CastRoleType.ACTOR, "Joker");
        addCast(m7, "N.T. Rama Rao Jr.", CastRoleType.ACTOR, "Bheem");
        addCast(m7, "Ram Charan", CastRoleType.ACTOR, "Ram");
        addCast(m8, "Yash", CastRoleType.ACTOR, "Rocky");

        log.info("Created 10 sample movies with cast");
        return movies;
    }

    private Movie createMovie(String title, String description, String genre, String language,
                              int duration, String certification, LocalDate releaseDate,
                              BigDecimal rating, String director, String producer, MovieStatus status) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setDescription(description);
        movie.setSynopsis(description);
        movie.setGenre(genre);
        movie.setLanguage(language);
        movie.setDuration(duration);
        movie.setCertification(certification);
        movie.setReleaseDate(releaseDate);
        movie.setImdbRating(rating);
        movie.setDirector(director);
        movie.setProducer(producer);
        movie.setStatus(status);
        return movieRepository.save(movie);
    }

    private void addCast(Movie movie, String name, CastRoleType role, String character) {
        MovieCast cast = new MovieCast();
        cast.setMovie(movie);
        cast.setPersonName(name);
        cast.setRoleType(role);
        cast.setCharacterName(character);
        cast.setDisplayOrder(0);
        movieCastRepository.save(cast);
    }

    private List<Theater> createTheaters() {
        // Theater 1: PVR Phoenix Mall (Regular)
        Theater t1 = new Theater();
        t1.setName("PVR Phoenix Mall");
        t1.setLocation("Whitefield, Bangalore");
        t1.setCity("Bangalore");
        t1.setState("Karnataka");
        t1.setPincode("560066");
        t1.setTheaterType(TheaterType.REGULAR);
        t1.setTotalScreens(3);
        t1.setFacilities("Parking, Food Court, Wheelchair Access");
        t1.setIsActive(true);
        t1 = theaterRepository.save(t1);

        createScreenWithSeats(t1, 1, "Audi 1", ScreenType.REGULAR, 10, 15, "Dolby 5.1", "40 ft", SeatType.REGULAR, new BigDecimal("150"));
        createScreenWithSeats(t1, 2, "Audi 2", ScreenType.REGULAR, 8, 12, "Dolby 5.1", "35 ft", SeatType.REGULAR, new BigDecimal("150"));
        createScreenWithSeats(t1, 3, "Audi 3", ScreenType.REGULAR, 8, 12, "Dolby 7.1", "38 ft", SeatType.REGULAR, new BigDecimal("180"));

        // Theater 2: IMAX Bangalore
        Theater t2 = new Theater();
        t2.setName("IMAX Bangalore");
        t2.setLocation("MG Road, Bangalore");
        t2.setCity("Bangalore");
        t2.setState("Karnataka");
        t2.setPincode("560001");
        t2.setTheaterType(TheaterType.IMAX);
        t2.setTotalScreens(2);
        t2.setFacilities("IMAX, Dolby Atmos, Premium Lounge, Valet Parking");
        t2.setIsActive(true);
        t2 = theaterRepository.save(t2);

        createScreenWithSeats(t2, 1, "IMAX Screen 1", ScreenType.IMAX, 15, 20, "Dolby Atmos 12-ch", "72 ft", SeatType.PREMIUM, new BigDecimal("400"));
        createScreenWithSeats(t2, 2, "IMAX Screen 2", ScreenType.IMAX, 12, 18, "Dolby Atmos 12-ch", "65 ft", SeatType.PREMIUM, new BigDecimal("380"));

        // Theater 3: 4DX Mantri Mall
        Theater t3 = new Theater();
        t3.setName("4DX Mantri Mall");
        t3.setLocation("Malleshwaram, Bangalore");
        t3.setCity("Bangalore");
        t3.setState("Karnataka");
        t3.setPincode("560003");
        t3.setTheaterType(TheaterType.FOUR_DX);
        t3.setTotalScreens(1);
        t3.setFacilities("4DX Motion Seats, Wind, Water, Scent Effects");
        t3.setIsActive(true);
        t3 = theaterRepository.save(t3);

        createScreenWithSeats(t3, 1, "4DX Screen 1", ScreenType.FOUR_DX, 10, 10, "Dolby Atmos", "50 ft", SeatType.MOTION, new BigDecimal("500"));

        log.info("Created 3 theaters with 6 screens");
        return List.of(t1, t2, t3);
    }

    private Screen createScreenWithSeats(Theater theater, int screenNum, String name, ScreenType type,
                                         int rows, int cols, String sound, String size, SeatType defaultSeatType,
                                         BigDecimal basePrice) {
        Screen screen = new Screen();
        screen.setTheater(theater);
        screen.setScreenNumber(screenNum);
        screen.setScreenName(name);
        screen.setTotalSeats(rows * cols);
        screen.setRows(rows);
        screen.setColumns(cols);
        screen.setScreenType(type);
        screen.setSoundSystem(sound);
        screen.setScreenSize(size);
        screen.setIsActive(true);
        screen = screenRepository.save(screen);

        for (int r = 0; r < rows; r++) {
            String rowLetter = String.valueOf((char) ('A' + r));
            for (int c = 1; c <= cols; c++) {
                Seat seat = new Seat();
                seat.setScreen(screen);
                seat.setRowLetter(rowLetter);
                seat.setSeatNumber(c);
                seat.setSeatLabel(rowLetter + c);
                seat.setIsAvailable(true);
                seat.setIsWheelchair(false);
                seat.setIsAisle(c == cols / 2 || c == cols / 2 + 1);

                // Set seat types: first 2 rows VIP, last 2 rows regular/cheaper, rest premium
                if (r < 2) {
                    seat.setSeatType(SeatType.VIP);
                    seat.setBasePrice(basePrice.multiply(new BigDecimal("1.5")));
                } else if (r >= rows - 2) {
                    seat.setSeatType(SeatType.REGULAR);
                    seat.setBasePrice(basePrice.multiply(new BigDecimal("0.8")));
                } else {
                    seat.setSeatType(defaultSeatType);
                    seat.setBasePrice(basePrice);
                }
                seatRepository.save(seat);
            }
        }

        return screen;
    }

    private void createShowtimes(List<Movie> movies, List<Theater> theaters) {
        List<Screen> allScreens = screenRepository.findAll();
        LocalDate today = LocalDate.now();
        LocalTime[] showTimes = {
            LocalTime.of(9, 30), LocalTime.of(13, 0), LocalTime.of(16, 30), LocalTime.of(20, 0)
        };

        int movieIndex = 0;
        for (int dayOffset = 0; dayOffset < 7; dayOffset++) {
            LocalDate showDate = today.plusDays(dayOffset);
            for (Screen screen : allScreens) {
                for (LocalTime time : showTimes) {
                    Movie movie = movies.get(movieIndex % 8); // Only NOW_SHOWING movies (first 8)
                    movieIndex++;

                    Showtime showtime = new Showtime();
                    showtime.setMovie(movie);
                    showtime.setScreen(screen);
                    showtime.setShowDate(showDate);
                    showtime.setShowTime(time);
                    showtime.setShowDatetime(LocalDateTime.of(showDate, time));
                    showtime.setEndDatetime(LocalDateTime.of(showDate, time).plusMinutes(movie.getDuration()));
                    showtime.setBasePrice(screen.getSeats().isEmpty()
                            ? new BigDecimal("200")
                            : seatRepository.findByScreenId(screen.getId()).get(0).getBasePrice());
                    showtime.setAvailableSeats(screen.getTotalSeats());
                    showtime.setTotalSeats(screen.getTotalSeats());
                    showtime.setStatus(ShowtimeStatus.ACTIVE);
                    showtime.setPricingStrategy("STANDARD");
                    showtimeRepository.save(showtime);
                }
            }
        }

        log.info("Created showtimes for the next 7 days");
    }

    private void createFoodItems() {
        createFood("Small Popcorn", "Freshly popped classic salted popcorn", FoodCategory.POPCORN, new BigDecimal("120"), true);
        createFood("Medium Popcorn", "Freshly popped classic salted popcorn", FoodCategory.POPCORN, new BigDecimal("180"), true);
        createFood("Large Popcorn", "Freshly popped classic salted popcorn", FoodCategory.POPCORN, new BigDecimal("250"), true);
        createFood("Caramel Popcorn", "Sweet caramel coated popcorn", FoodCategory.POPCORN, new BigDecimal("200"), true);
        createFood("Coca-Cola (Regular)", "Chilled Coca-Cola 300ml", FoodCategory.BEVERAGE, new BigDecimal("100"), true);
        createFood("Coca-Cola (Large)", "Chilled Coca-Cola 500ml", FoodCategory.BEVERAGE, new BigDecimal("150"), true);
        createFood("Mineral Water", "Packaged drinking water 500ml", FoodCategory.BEVERAGE, new BigDecimal("40"), true);
        createFood("Cold Coffee", "Iced cold coffee with cream", FoodCategory.BEVERAGE, new BigDecimal("180"), true);
        createFood("Nachos with Cheese", "Crispy nachos with warm cheese dip", FoodCategory.SNACK, new BigDecimal("200"), true);
        createFood("Samosa (2 pcs)", "Crispy vegetable samosas", FoodCategory.SNACK, new BigDecimal("80"), true);
        createFood("Veg Sandwich", "Grilled vegetable sandwich", FoodCategory.SNACK, new BigDecimal("150"), true);
        createFood("Chicken Sandwich", "Grilled chicken sandwich", FoodCategory.SNACK, new BigDecimal("180"), false);
        createFood("Couple Combo", "2 Regular Popcorn + 2 Coke + Nachos", FoodCategory.COMBO, new BigDecimal("450"), true);
        createFood("Family Combo", "1 Large Popcorn + 4 Coke + 2 Nachos", FoodCategory.COMBO, new BigDecimal("750"), true);
        createFood("Solo Combo", "1 Small Popcorn + 1 Coke", FoodCategory.COMBO, new BigDecimal("180"), true);

        log.info("Created 15 food items");
    }

    private void createFood(String name, String desc, FoodCategory category, BigDecimal price, boolean veg) {
        FoodItem item = new FoodItem();
        item.setName(name);
        item.setDescription(desc);
        item.setCategory(category);
        item.setPrice(price);
        item.setIsVegetarian(veg);
        item.setIsAvailable(true);
        foodItemRepository.save(item);
    }

    private void createPromoCodes() {
        PromoCode p1 = new PromoCode();
        p1.setCode("FIRST50");
        p1.setDescription("50% off on first booking");
        p1.setDiscountType(DiscountType.PERCENTAGE);
        p1.setDiscountValue(new BigDecimal("50"));
        p1.setMaxDiscount(new BigDecimal("200"));
        p1.setMinimumAmount(new BigDecimal("200"));
        p1.setMaxUsage(1000);
        p1.setCurrentUsage(0);
        p1.setValidFrom(LocalDateTime.now().minusDays(30));
        p1.setValidUntil(LocalDateTime.now().plusDays(90));
        p1.setIsActive(true);
        promoCodeRepository.save(p1);

        PromoCode p2 = new PromoCode();
        p2.setCode("WEEKEND20");
        p2.setDescription("20% off on weekend bookings");
        p2.setDiscountType(DiscountType.PERCENTAGE);
        p2.setDiscountValue(new BigDecimal("20"));
        p2.setMaxDiscount(new BigDecimal("150"));
        p2.setMinimumAmount(new BigDecimal("300"));
        p2.setMaxUsage(500);
        p2.setCurrentUsage(0);
        p2.setValidFrom(LocalDateTime.now().minusDays(10));
        p2.setValidUntil(LocalDateTime.now().plusDays(60));
        p2.setIsActive(true);
        promoCodeRepository.save(p2);

        PromoCode p3 = new PromoCode();
        p3.setCode("STUDENT15");
        p3.setDescription("Flat Rs.15 off for students");
        p3.setDiscountType(DiscountType.FIXED);
        p3.setDiscountValue(new BigDecimal("100"));
        p3.setMinimumAmount(new BigDecimal("150"));
        p3.setMaxUsage(2000);
        p3.setCurrentUsage(0);
        p3.setValidFrom(LocalDateTime.now().minusDays(5));
        p3.setValidUntil(LocalDateTime.now().plusDays(120));
        p3.setIsActive(true);
        promoCodeRepository.save(p3);

        log.info("Created 3 promo codes");
    }
}
