# **FILE 2: DATABASE SCHEMA SPECIFICATION**

---

## **Overview**

The Movie Ticket Booking System uses **PostgreSQL** as the relational database management system. The schema is designed with:
- **Normalization**: 3NF to minimize redundancy
- **ACID Compliance**: Ensures transaction integrity for bookings and payments
- **Referential Integrity**: Foreign keys with CASCADE/RESTRICT rules
- **Indexing**: Strategic indexes for performance optimization
- **Concurrency Control**: Row-level locking for seat booking operations

---

## **ENTITY-RELATIONSHIP DIAGRAM SPECIFICATION**

### **Entities (Tables)**:
1. users
2. movies
3. movie_cast
4. theaters
5. screens
6. seats
7. showtimes
8. bookings
9. booking_seats
10. seat_locks
11. food_items
12. booking_food
13. payments
14. promo_codes
15. user_preferences
16. reviews

### **Relationships**:
- User (1) ─── (M) Bookings
- User (1) ─── (M) Reviews
- User (1) ─── (1) UserPreferences
- Movie (1) ─── (M) MovieCast
- Movie (1) ─── (M) Showtimes
- Movie (1) ─── (M) Reviews
- Theater (1) ─── (M) Screens
- Screen (1) ─── (M) Seats
- Screen (1) ─── (M) Showtimes
- Showtime (1) ─── (M) Bookings
- Showtime (1) ─── (M) SeatLocks
- Booking (1) ─── (M) BookingSeats
- Booking (1) ─── (M) BookingFood
- Booking (1) ─── (1) Payment
- Seat (M) ─── (M) Bookings (through BookingSeats)
- FoodItem (M) ─── (M) Bookings (through BookingFood)
- PromoCode (1) ─── (M) Bookings

---

## **TABLE SPECIFICATIONS**

### **1. users**

**Purpose**: Store user account information and authentication credentials

**Columns**:
| Column Name | Data Type | Constraints | Description |
|-------------|-----------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Auto-incrementing user ID |
| email | VARCHAR(255) | UNIQUE, NOT NULL | User's email address (used for login) |
| password_hash | VARCHAR(255) | NOT NULL | BCrypt hashed password |
| first_name | VARCHAR(100) | NOT NULL | User's first name |
| last_name | VARCHAR(100) | NOT NULL | User's last name |
| phone | VARCHAR(15) | UNIQUE, NOT NULL | Phone number with country code |
| date_of_birth | DATE | NOT NULL | Date of birth (for age verification) |
| gender | VARCHAR(10) | | Gender (Male/Female/Other/Prefer not to say) |
| role | VARCHAR(20) | NOT NULL, DEFAULT 'CUSTOMER' | Role (CUSTOMER/MANAGER/ADMIN) |
| is_active | BOOLEAN | DEFAULT TRUE | Account active status |
| email_verified | BOOLEAN | DEFAULT FALSE | Email verification status |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Account creation timestamp |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Last update timestamp |

**Indexes**:
- PRIMARY KEY on `id`
- UNIQUE INDEX on `email`
- UNIQUE INDEX on `phone`
- INDEX on `role` (for filtering by user type)

**Business Rules**:
- Email must be valid format
- Password must be at least 8 characters
- Age calculated from date_of_birth for certification checks
- Phone must include country code (+91 for India)

---

### **2. movies**

**Purpose**: Store movie catalog information

**Columns**:
| Column Name | Data Type | Constraints | Description |
|-------------|-----------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Auto-incrementing movie ID |
| title | VARCHAR(255) | NOT NULL | Movie title |
| description | TEXT | | Short description/tagline |
| synopsis | TEXT | | Full plot summary |
| genre | VARCHAR(100) | NOT NULL | Comma-separated genres (Action, Comedy, Drama, etc.) |
| language | VARCHAR(50) | NOT NULL | Primary language (English, Hindi, Kannada, etc.) |
| duration | INTEGER | NOT NULL | Duration in minutes |
| certification | VARCHAR(10) | NOT NULL | Rating (U/UA/A) |
| release_date | DATE | NOT NULL | Release date |
| poster_url | VARCHAR(500) | | URL to poster image |
| trailer_url | VARCHAR(500) | | URL to trailer video |
| imdb_rating | DECIMAL(3,1) | | IMDb rating (0.0 to 10.0) |
| status | VARCHAR(20) | DEFAULT 'UPCOMING' | Status (UPCOMING/NOW_SHOWING/ENDED) |
| director | VARCHAR(255) | | Director name(s) |
| producer | VARCHAR(255) | | Producer name(s) |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Record creation timestamp |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Last update timestamp |

**Indexes**:
- PRIMARY KEY on `id`
- INDEX on `status` (for filtering active movies)
- INDEX on `release_date` (for sorting by date)
- FULLTEXT INDEX on `title` (for search functionality)

**Business Rules**:
- Status automatically changes to NOW_SHOWING on release_date
- Status changes to ENDED 30 days after last showtime
- Genre must be from predefined list

---

### **3. movie_cast**

**Purpose**: Store cast and crew information for movies

**Columns**:
| Column Name | Data Type | Constraints | Description |
|-------------|-----------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Auto-incrementing cast ID |
| movie_id | BIGINT | FOREIGN KEY REFERENCES movies(id) ON DELETE CASCADE, NOT NULL | Reference to movie |
| person_name | VARCHAR(255) | NOT NULL | Name of cast/crew member |
| role_type | VARCHAR(50) | NOT NULL | Role type (ACTOR/DIRECTOR/PRODUCER/MUSIC_DIRECTOR/etc.) |
| character_name | VARCHAR(255) | | Character name (for actors) |
| display_order | INTEGER | DEFAULT 0 | Display order (for featured cast) |

**Indexes**:
- PRIMARY KEY on `id`
- INDEX on `movie_id` (for fetching cast by movie)
- INDEX on `person_name` (for search by actor/director)

**Business Rules**:
- One movie can have multiple cast members
- Same person can appear in multiple movies
- Display order determines prominence on movie details page

---

### **4. theaters**

**Purpose**: Store theater/multiplex information

**Columns**:
| Column Name | Data Type | Constraints | Description |
|-------------|-----------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Auto-incrementing theater ID |
| name | VARCHAR(255) | NOT NULL | Theater name (e.g., "PVR Phoenix Mall") |
| location | VARCHAR(255) | NOT NULL | Address/location |
| city | VARCHAR(100) | NOT NULL | City name |
| state | VARCHAR(100) | NOT NULL | State name |
| pincode | VARCHAR(10) | NOT NULL | Postal code |
| latitude | DECIMAL(10,8) | | GPS latitude for maps |
| longitude | DECIMAL(11,8) | | GPS longitude for maps |
| theater_type | VARCHAR(20) | NOT NULL | Theater type (REGULAR/IMAX/FOUR_DX) |
| total_screens | INTEGER | NOT NULL, DEFAULT 1 | Number of screens in theater |
| facilities | TEXT | | Comma-separated facilities (Parking, Food Court, Wheelchair Access, etc.) |
| is_active | BOOLEAN | DEFAULT TRUE | Theater operational status |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Record creation timestamp |

**Indexes**:
- PRIMARY KEY on `id`
- INDEX on `city` (for filtering by city)
- INDEX on `theater_type` (for filtering by type)
- SPATIAL INDEX on `latitude, longitude` (for location-based search)

**Business Rules**:
- Theater type determines seat configuration via Abstract Factory pattern
- IMAX theaters have premium pricing multiplier
- 4DX theaters have motion seats configuration

---

### **5. screens**

**Purpose**: Store individual screen information within theaters

**Columns**:
| Column Name | Data Type | Constraints | Description |
|-------------|-----------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Auto-incrementing screen ID |
| theater_id | BIGINT | FOREIGN KEY REFERENCES theaters(id) ON DELETE CASCADE, NOT NULL | Reference to parent theater |
| screen_number | INTEGER | NOT NULL | Screen number within theater |
| screen_name | VARCHAR(100) | | Screen name (e.g., "Audi 1", "Gold Class") |
| total_seats | INTEGER | NOT NULL | Total seating capacity |
| rows | INTEGER | NOT NULL | Number of rows |
| columns | INTEGER | NOT NULL | Number of columns per row |
| screen_type | VARCHAR(20) | NOT NULL | Type (REGULAR/IMAX/FOUR_DX) |
| sound_system | VARCHAR(100) | | Sound system (Dolby Atmos, DTS, etc.) |
| screen_size | VARCHAR(50) | | Screen dimensions (40 ft, 72 ft, etc.) |
| special_features | TEXT | | Comma-separated features (Laser Projection, Motion Seats, etc.) |
| is_active | BOOLEAN | DEFAULT TRUE | Screen operational status |

**Indexes**:
- PRIMARY KEY on `id`
- INDEX on `theater_id` (for fetching screens by theater)
- UNIQUE INDEX on `(theater_id, screen_number)` (no duplicate screen numbers within theater)

**Business Rules**:
- Screen type must match parent theater type (or be REGULAR)
- Total seats should equal actual seat count in seats table
- Rows × Columns should approximately equal total_seats (accounting for aisles)

---

### **6. seats**

**Purpose**: Store individual seat information for each screen

**Columns**:
| Column Name | Data Type | Constraints | Description |
|-------------|-----------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Auto-incrementing seat ID |
| screen_id | BIGINT | FOREIGN KEY REFERENCES screens(id) ON DELETE CASCADE, NOT NULL | Reference to parent screen |
| row_letter | VARCHAR(5) | NOT NULL | Row identifier (A, B, C, etc.) |
| seat_number | INTEGER | NOT NULL | Seat number within row |
| seat_label | VARCHAR(10) | NOT NULL | Display label (A1, A2, B1, etc.) |
| seat_type | VARCHAR(20) | NOT NULL | Seat type (REGULAR/PREMIUM/VIP/RECLINER/MOTION) |
| base_price | DECIMAL(10,2) | NOT NULL | Base price for this seat |
| is_available | BOOLEAN | DEFAULT TRUE | Permanent availability (false for broken/removed seats) |
| is_wheelchair | BOOLEAN | DEFAULT FALSE | Wheelchair accessible seat |
| is_aisle | BOOLEAN | DEFAULT FALSE | Aisle seat flag |

**Indexes**:
- PRIMARY KEY on `id`
- INDEX on `screen_id` (for fetching seats by screen)
- UNIQUE INDEX on `(screen_id, row_letter, seat_number)` (no duplicate seats)
- INDEX on `seat_type` (for filtering by seat type)

**Business Rules**:
- Seat labels must be unique within a screen
- Premium seats have 1.5x base_price multiplier
- VIP seats have 2x base_price multiplier
- Motion seats (4DX) have uniform pricing
- Wheelchair seats typically in back rows with easy access

---

### **7. showtimes**

**Purpose**: Store movie screening schedules

**Columns**:
| Column Name | Data Type | Constraints | Description |
|-------------|-----------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Auto-incrementing showtime ID |
| movie_id | BIGINT | FOREIGN KEY REFERENCES movies(id) ON DELETE RESTRICT, NOT NULL | Reference to movie |
| screen_id | BIGINT | FOREIGN KEY REFERENCES screens(id) ON DELETE RESTRICT, NOT NULL | Reference to screen |
| show_date | DATE | NOT NULL | Show date |
| show_time | TIME | NOT NULL | Show start time |
| show_datetime | TIMESTAMP | NOT NULL | Combined date-time for easier queries |
| end_datetime | TIMESTAMP | NOT NULL | Calculated end time (start + movie duration + buffer) |
| base_price | DECIMAL(10,2) | NOT NULL | Base ticket price (before seat type adjustments) |
| pricing_strategy | VARCHAR(50) | | Active pricing strategy (MATINEE/WEEKEND/HOLIDAY/etc.) |
| available_seats | INTEGER | NOT NULL | Current available seats count |
| total_seats | INTEGER | NOT NULL | Total seats for this show |
| status | VARCHAR(20) | DEFAULT 'ACTIVE' | Status (ACTIVE/CANCELLED/COMPLETED) |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Record creation timestamp |

**Indexes**:
- PRIMARY KEY on `id`
- INDEX on `movie_id` (for fetching showtimes by movie)
- INDEX on `screen_id` (for fetching showtimes by screen)
- INDEX on `show_datetime` (for chronological sorting)
- UNIQUE INDEX on `(screen_id, show_datetime)` (prevent double-booking of screens)

**Business Rules**:
- show_datetime = show_date + show_time (stored for performance)
- end_datetime = show_datetime + movie.duration + 15 min buffer
- No overlapping showtimes for same screen (enforced by unique index)
- available_seats decremented on each confirmed booking
- Status changes to COMPLETED after show_datetime passes
- Pricing strategy determines which Strategy pattern implementation to use

---

### **8. bookings**

**Purpose**: Store ticket booking records

**Columns**:
| Column Name | Data Type | Constraints | Description |
|-------------|-----------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Auto-incrementing booking ID |
| booking_reference | VARCHAR(20) | UNIQUE, NOT NULL | Human-readable booking reference (e.g., BK20260209001) |
| user_id | BIGINT | FOREIGN KEY REFERENCES users(id) ON DELETE RESTRICT, NOT NULL | Reference to user who made booking |
| showtime_id | BIGINT | FOREIGN KEY REFERENCES showtimes(id) ON DELETE RESTRICT, NOT NULL | Reference to showtime |
| movie_id | BIGINT | FOREIGN KEY REFERENCES movies(id) ON DELETE RESTRICT, NOT NULL | Reference to movie (denormalized for reporting) |
| screen_id | BIGINT | FOREIGN KEY REFERENCES screens(id) ON DELETE RESTRICT, NOT NULL | Reference to screen (denormalized) |
| theater_id | BIGINT | FOREIGN KEY REFERENCES theaters(id) ON DELETE RESTRICT, NOT NULL | Reference to theater (denormalized) |
| num_seats | INTEGER | NOT NULL | Number of seats booked |
| ticket_amount | DECIMAL(10,2) | NOT NULL | Total ticket cost |
| food_amount | DECIMAL(10,2) | DEFAULT 0.00 | Total food & beverage cost |
| tax_amount | DECIMAL(10,2) | NOT NULL | Tax amount (GST) |
| discount_amount | DECIMAL(10,2) | DEFAULT 0.00 | Discount applied |
| total_amount | DECIMAL(10,2) | NOT NULL | Final payable amount |
| promo_code_id | BIGINT | FOREIGN KEY REFERENCES promo_codes(id) ON DELETE SET NULL | Reference to applied promo code |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'PENDING' | Booking status (PENDING/LOCKED/CONFIRMED/CANCELLED/REFUNDED) |
| qr_code_url | VARCHAR(500) | | URL to generated QR code image |
| booking_datetime | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Booking creation time |
| confirmed_datetime | TIMESTAMP | | Confirmation time (when payment successful) |
| cancelled_datetime | TIMESTAMP | | Cancellation time (if cancelled) |
| cancellation_reason | TEXT | | Reason for cancellation |

**Indexes**:
- PRIMARY KEY on `id`
- UNIQUE INDEX on `booking_reference`
- INDEX on `user_id` (for user's booking history)
- INDEX on `showtime_id` (for showtime bookings)
- INDEX on `status` (for filtering by status)
- INDEX on `booking_datetime` (for chronological sorting)

**Business Rules**:
- booking_reference format: BK + YYYYMMDD + sequential number
- Status transitions managed by State Pattern:
  - PENDING → LOCKED (seats selected, 10 min timer)
  - LOCKED → CONFIRMED (payment successful)
  - LOCKED → PENDING (timer expired, released)
  - CONFIRMED → CANCELLED (user cancels)
  - CANCELLED → REFUNDED (refund processed)
- total_amount = ticket_amount + food_amount + tax_amount - discount_amount
- tax_amount = 18% GST on (ticket_amount + food_amount - discount_amount)
- QR code generated upon confirmation

---

### **9. booking_seats**

**Purpose**: Junction table linking bookings to specific seats

**Columns**:
| Column Name | Data Type | Constraints | Description |
|-------------|-----------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Auto-incrementing record ID |
| booking_id | BIGINT | FOREIGN KEY REFERENCES bookings(id) ON DELETE CASCADE, NOT NULL | Reference to booking |
| seat_id | BIGINT | FOREIGN KEY REFERENCES seats(id) ON DELETE RESTRICT, NOT NULL | Reference to seat |
| showtime_id | BIGINT | FOREIGN KEY REFERENCES showtimes(id) ON DELETE RESTRICT, NOT NULL | Reference to showtime (denormalized) |
| seat_price | DECIMAL(10,2) | NOT NULL | Actual price paid for this seat (after pricing strategy) |

**Indexes**:
- PRIMARY KEY on `id`
- INDEX on `booking_id` (for fetching seats by booking)
- UNIQUE INDEX on `(seat_id, showtime_id)` (prevent double-booking same seat for same show)
- INDEX on `showtime_id` (for checking seat availability)

**Business Rules**:
- One booking can have multiple seats
- One seat can appear in multiple bookings (different showtimes)
- seat_price stored here (not recalculated) to preserve historical pricing
- Unique constraint prevents same seat being booked twice for same show

---

### **10. seat_locks**

**Purpose**: Temporarily lock seats during booking process

**Columns**:
| Column Name | Data Type | Constraints | Description |
|-------------|-----------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Auto-incrementing lock ID |
| seat_id | BIGINT | FOREIGN KEY REFERENCES seats(id) ON DELETE CASCADE, NOT NULL | Reference to locked seat |
| showtime_id | BIGINT | FOREIGN KEY REFERENCES showtimes(id) ON DELETE CASCADE, NOT NULL | Reference to showtime |
| user_id | BIGINT | FOREIGN KEY REFERENCES users(id) ON DELETE CASCADE, NOT NULL | User who locked the seat |
| locked_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Lock creation time |
| locked_until | TIMESTAMP | NOT NULL | Lock expiry time (locked_at + 10 minutes) |
| is_active | BOOLEAN | DEFAULT TRUE | Lock active status |

**Indexes**:
- PRIMARY KEY on `id`
- UNIQUE INDEX on `(seat_id, showtime_id)` WHERE is_active = TRUE (only one active lock per seat-showtime)
- INDEX on `user_id` (for fetching user's locked seats)
- INDEX on `locked_until` (for cleanup of expired locks)

**Business Rules**:
- locked_until = locked_at + 10 minutes
- Background job runs every minute to expire locks where locked_until < now()
- When lock expires: is_active set to FALSE, seat becomes available again
- Observer pattern broadcasts seat release to all connected clients
- User can only lock up to 10 seats at once

---

### **11. food_items**

**Purpose**: Store food and beverage menu items

**Columns**:
| Column Name | Data Type | Constraints | Description |
|-------------|-----------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Auto-incrementing item ID |
| name | VARCHAR(255) | NOT NULL | Item name (e.g., "Large Popcorn") |
| description | TEXT | | Item description |
| category | VARCHAR(50) | NOT NULL | Category (POPCORN/BEVERAGE/SNACK/COMBO) |
| price | DECIMAL(10,2) | NOT NULL | Item price |
| image_url | VARCHAR(500) | | URL to item image |
| is_vegetarian | BOOLEAN | DEFAULT TRUE | Vegetarian flag |
| is_available | BOOLEAN | DEFAULT TRUE | Current availability |
| display_order | INTEGER | DEFAULT 0 | Display order in menu |

**Indexes**:
- PRIMARY KEY on `id`
- INDEX on `category` (for filtering by category)
- INDEX on `is_available` (for showing only available items)

**Business Rules**:
- Items grouped by category on F&B page
- Combos have discounted pricing vs individual items
- Display order determines prominence in menu

---

### **12. booking_food**

**Purpose**: Junction table linking bookings to food items

**Columns**:
| Column Name | Data Type | Constraints | Description |
|-------------|-----------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Auto-incrementing record ID |
| booking_id | BIGINT | FOREIGN KEY REFERENCES bookings(id) ON DELETE CASCADE, NOT NULL | Reference to booking |
| food_item_id | BIGINT | FOREIGN KEY REFERENCES food_items(id) ON DELETE RESTRICT, NOT NULL | Reference to food item |
| quantity | INTEGER | NOT NULL, CHECK (quantity > 0) | Quantity ordered |
| unit_price | DECIMAL(10,2) | NOT NULL | Price per unit (historical) |
| subtotal | DECIMAL(10,2) | NOT NULL | quantity × unit_price |

**Indexes**:
- PRIMARY KEY on `id`
- INDEX on `booking_id` (for fetching food by booking)

**Business Rules**:
- Multiple food items can be added to one booking (Decorator pattern)
- unit_price stored here to preserve historical pricing
- subtotal = quantity × unit_price

---

### **13. payments**

**Purpose**: Store payment transaction records

**Columns**:
| Column Name | Data Type | Constraints | Description |
|-------------|-----------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Auto-incrementing payment ID |
| booking_id | BIGINT | FOREIGN KEY REFERENCES bookings(id) ON DELETE RESTRICT, NOT NULL | Reference to booking |
| user_id | BIGINT | FOREIGN KEY REFERENCES users(id) ON DELETE RESTRICT, NOT NULL | Reference to user |
| amount | DECIMAL(10,2) | NOT NULL | Payment amount |
| payment_method | VARCHAR(50) | NOT NULL | Payment method (CREDIT_CARD/DEBIT_CARD/UPI/NET_BANKING/WALLET) |
| transaction_id | VARCHAR(255) | UNIQUE | Gateway transaction ID |
| gateway_name | VARCHAR(100) | | Payment gateway name (PayPal, Stripe, Razorpay, etc.) |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'INITIATED' | Status (INITIATED/PROCESSING/SUCCESS/FAILED/REFUNDED) |
| payment_datetime | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Payment initiation time |
| success_datetime | TIMESTAMP | | Payment success time |
| failure_reason | TEXT | | Failure reason (if failed) |
| refund_amount | DECIMAL(10,2) | DEFAULT 0.00 | Refunded amount |
| refund_datetime | TIMESTAMP | | Refund processing time |
| refund_transaction_id | VARCHAR(255) | | Refund transaction ID |

**Indexes**:
- PRIMARY KEY on `id`
- UNIQUE INDEX on `transaction_id`
- INDEX on `booking_id` (for booking-payment lookup)
- INDEX on `user_id` (for user's payment history)
- INDEX on `status` (for filtering by status)

**Business Rules**:
- Status transitions: INITIATED → PROCESSING → SUCCESS/FAILED
- If FAILED: user can retry payment (new payment record created)
- If SUCCESS then user cancels booking: status becomes REFUNDED
- transaction_id comes from payment gateway
- Facade pattern handles gateway routing based on payment_method

---

### **14. promo_codes**

**Purpose**: Store promotional discount codes

**Columns**:
| Column Name | Data Type | Constraints | Description |
|-------------|-----------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Auto-incrementing promo ID |
| code | VARCHAR(50) | UNIQUE, NOT NULL | Promo code (e.g., "FIRST50", "WEEKEND20") |
| description | TEXT | | Code description for admins |
| discount_type | VARCHAR(20) | NOT NULL | Type (PERCENTAGE/FIXED) |
| discount_value | DECIMAL(10,2) | NOT NULL | Discount value (percentage or fixed amount) |
| minimum_amount | DECIMAL(10,2) | DEFAULT 0.00 | Minimum booking amount required |
| max_discount | DECIMAL(10,2) | | Maximum discount cap (for percentage discounts) |
| max_usage | INTEGER | DEFAULT 1000 | Maximum number of times code can be used |
| current_usage | INTEGER | DEFAULT 0 | Current usage count |
| valid_from | TIMESTAMP | NOT NULL | Code validity start time |
| valid_until | TIMESTAMP | NOT NULL | Code validity end time |
| is_active | BOOLEAN | DEFAULT TRUE | Code active status |
| created_by | BIGINT | FOREIGN KEY REFERENCES users(id) | Admin who created the code |

**Indexes**:
- PRIMARY KEY on `id`
- UNIQUE INDEX on `code`
- INDEX on `is_active` (for filtering active codes)
- INDEX on `valid_from, valid_until` (for date range queries)

**Business Rules**:
- Validated by Chain of Responsibility PromoCodeHandler
- current_usage incremented on each successful booking
- Code becomes invalid if current_usage >= max_usage
- discount_type = PERCENTAGE: discount = booking_amount × (discount_value / 100), capped at max_discount
- discount_type = FIXED: discount = discount_value

---

### **15. user_preferences**

**Purpose**: Store user preferences for personalized recommendations

**Columns**:
| Column Name | Data Type | Constraints | Description |
|-------------|-----------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Auto-incrementing preference ID |
| user_id | BIGINT | FOREIGN KEY REFERENCES users(id) ON DELETE CASCADE, UNIQUE, NOT NULL | Reference to user (one-to-one) |
| preferred_genres | TEXT | | Comma-separated genres (Action, Comedy, etc.) |
| preferred_languages | TEXT | | Comma-separated languages (English, Hindi, etc.) |
| preferred_theater_types | TEXT | | Comma-separated types (REGULAR, IMAX, FOUR_DX) |
| notification_email | BOOLEAN | DEFAULT TRUE | Email notification preference |
| notification_sms | BOOLEAN | DEFAULT FALSE | SMS notification preference |
| show_reminders | BOOLEAN | DEFAULT TRUE | Show reminder preference |

**Indexes**:
- PRIMARY KEY on `id`
- UNIQUE INDEX on `user_id`

**Business Rules**:
- Created automatically when user registers
- Used for personalized recommendations (Saffiya's module)
- Updated based on user's booking history (implicit preferences)

---

### **16. reviews**

**Purpose**: Store user reviews and ratings for movies

**Columns**:
| Column Name | Data Type | Constraints | Description |
|-------------|-----------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Auto-incrementing review ID |
| movie_id | BIGINT | FOREIGN KEY REFERENCES movies(id) ON DELETE CASCADE, NOT NULL | Reference to movie |
| user_id | BIGINT | FOREIGN KEY REFERENCES users(id) ON DELETE CASCADE, NOT NULL | Reference to user |
| rating | INTEGER | NOT NULL, CHECK (rating >= 1 AND rating <= 5) | Star rating (1-5) |
| review_text | TEXT | | Written review |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Review creation time |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Review update time |

**Indexes**:
- PRIMARY KEY on `id`
- INDEX on `movie_id` (for fetching reviews by movie)
- UNIQUE INDEX on `(movie_id, user_id)` (one review per user per movie)

**Business Rules**:
- Users can only review movies they've watched (have confirmed booking for)
- Average rating displayed on movie details page
- Reviews displayed in reverse chronological order

---

## **RELATIONSHIPS SUMMARY**

### **One-to-Many Relationships**:
1. User → Bookings (one user can have many bookings)
2. User → Reviews (one user can write many reviews)
3. Movie → Showtimes (one movie can have many showtimes)
4. Movie → MovieCast (one movie has many cast members)
5. Movie → Reviews (one movie can have many reviews)
6. Theater → Screens (one theater has many screens)
7. Screen → Seats (one screen has many seats)
8. Screen → Showtimes (one screen can have many showtimes)
9. Showtime → Bookings (one showtime can have many bookings)
10. Showtime → SeatLocks (one showtime can have many locked seats)
11. Booking → BookingSeats (one booking has many seats)
12. Booking → BookingFood (one booking can have many food items)
13. PromoCode → Bookings (one promo code can be used in many bookings)

### **One-to-One Relationships**:
1. User → UserPreferences (one user has one preference record)
2. Booking → Payment (one booking has one primary payment record)

### **Many-to-Many Relationships** (via junction tables):
1. Bookings ↔ Seats (via booking_seats)
2. Bookings ↔ FoodItems (via booking_food)

---

## **CRITICAL CONSTRAINTS**

### **Prevent Double Booking**:
- UNIQUE INDEX on `booking_seats(seat_id, showtime_id)` ensures same seat cannot be booked twice for same show
- UNIQUE INDEX on `seat_locks(seat_id, showtime_id) WHERE is_active = TRUE` ensures only one user can lock a seat at a time

### **Prevent Schedule Conflicts**:
- UNIQUE INDEX on `showtimes(screen_id, show_datetime)` ensures screen cannot have overlapping showtimes
- Application logic checks: new_show_datetime must be after previous_show.end_datetime

### **Data Integrity**:
- Foreign keys with ON DELETE RESTRICT for critical relationships (bookings, payments) prevent accidental data loss
- Foreign keys with ON DELETE CASCADE for dependent data (booking_seats when booking deleted)

### **Business Logic Constraints**:
- CHECK constraint: `booking_food.quantity > 0`
- CHECK constraint: `reviews.rating BETWEEN 1 AND 5`
- CHECK constraint: `bookings.num_seats <= 10` (application enforced)
- CHECK constraint: `seat_locks.locked_until > locked_at`

---

## **INDEXES FOR PERFORMANCE**

### **Critical Indexes** (must have):
1. `users.email` - Fast login lookups
2. `bookings.user_id` - Fast booking history queries
3. `showtimes.movie_id` - Fast "Find showtimes for movie X"
4. `seats.screen_id` - Fast seat map loading
5. `booking_seats(seat_id, showtime_id)` - Fast seat availability checks
6. `seat_locks.locked_until` - Fast cleanup of expired locks

### **Composite Indexes** (for complex queries):
1. `showtimes(movie_id, show_datetime)` - "Get showtimes for movie X after date Y"
2. `bookings(user_id, status)` - "Get user's confirmed bookings"
3. `showtimes(screen_id, show_date)` - "Get today's shows for screen X"

---

## **SAMPLE DATA SCENARIOS**

### **Scenario 1: User Books Ticket with Food**

**Step-by-step database operations**:
1. User selects 2 seats → INSERT into `seat_locks` (locked_until = now + 10 min)
2. User adds popcorn and coke → Stored temporarily in session (not DB yet)
3. User proceeds to payment → INSERT into `bookings` (status = PENDING)
4. User pays successfully → 
   - INSERT into `payments` (status = SUCCESS)
   - UPDATE `bookings` SET status = 'CONFIRMED'
   - INSERT into `booking_seats` (2 records for 2 seats)
   - INSERT into `booking_food` (2 records for popcorn and coke)
   - UPDATE `seat_locks` SET is_active = FALSE (locks released)
   - UPDATE `showtimes` SET available_seats = available_seats - 2
5. Observer pattern triggers WebSocket broadcast to all users viewing this showtime

### **Scenario 2: Seat Lock Expires**

**Background job runs every minute**:
1. SELECT * FROM seat_locks WHERE locked_until < now() AND is_active = TRUE
2. For each expired lock:
   - UPDATE seat_locks SET is_active = FALSE
   - WebSocket broadcast: {"seatId": X, "status": "AVAILABLE"}

### **Scenario 3: User Cancels Booking**

**Step-by-step**:
1. User requests cancellation → System checks cancellation policy
2. Calculate refund amount based on time before show
3. UPDATE bookings SET status = 'CANCELLED', cancelled_datetime = now()
4. INSERT into payments (new record with status = 'REFUNDED')
5. DELETE from booking_seats (or keep with cancelled flag)
6. UPDATE showtimes SET available_seats = available_seats + num_seats
7. WebSocket broadcast: seats now available again

---

## **DATABASE INITIALIZATION SEQUENCE**

**Order of table creation** (respecting foreign key dependencies):
1. users
2. movies
3. movie_cast
4. theaters
5. screens
6. seats
7. food_items
8. promo_codes
9. user_preferences
10. showtimes
11. bookings
12. booking_seats
13. booking_food
14. payments
15. seat_locks
16. reviews

---

## **BACKUP & MAINTENANCE**

### **Backup Strategy**:
- Daily full backup at 3 AM (off-peak hours)
- Incremental backups every 4 hours
- Retain backups for 30 days
- Test restoration monthly

### **Maintenance Tasks**:
- Cleanup expired seat_locks (every minute via background job)
- Archive old bookings (>1 year) to separate table (monthly)
- Update movie status (UPCOMING → NOW_SHOWING → ENDED) based on dates (daily)
- Rebuild indexes (weekly during maintenance window)
- Analyze query performance and optimize slow queries (monthly)

---