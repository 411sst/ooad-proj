# **FILE 1: MAIN PRODUCT REQUIREMENTS DOCUMENT (PRD)**

---

## **Document Information**
- **Course**: UE23CS352B – Object Oriented Analysis & Design
- **Project**: Movie Ticket Booking System
- **Team Members**: Shrish, Vaishnav, Saffiya, Rushad
- **Institution**: PES University, Bengaluru
- **Semester**: 6th Semester, 2026-27

---

## **🎯 MANDATORY REQUIREMENTS CHECKLIST**

### **A. TEAM & SCOPE**
- [x] Team of exactly 4 members: Shrish, Vaishnav, Saffiya, Rushad
- [x] Domain: Entertainment (Movie Ticket Booking)
- [x] Problem statement with clear functional requirements

### **B. UML DIAGRAMS (2 marks)**
- [ ] **1 Use Case diagram** - Complete system with all actors and relationships
- [ ] **1 Class diagram** - All entities, relationships, design patterns visible
- [ ] **4 Activity diagrams** (1 per member):
  - [ ] Shrish: Seat Selection & Real-time Booking Flow
  - [ ] Vaishnav: Payment Processing & F&B Order Flow
  - [ ] Saffiya: Movie Browsing & Recommendation Flow
  - [ ] Rushad: Theater Management & Dynamic Pricing Flow
- [ ] **4 State diagrams** (1 per member):
  - [ ] Shrish: Booking State Machine (Pending→Locked→Confirmed→Cancelled→Refunded)
  - [ ] Vaishnav: Payment State Machine (Initiated→Processing→Success/Failed→Refunded)
  - [ ] Saffiya: Movie Lifecycle State (Upcoming→NowShowing→Ended)
  - [ ] Rushad: Seat State Machine (Available→Locked→Booked→Released)

### **C. ARCHITECTURE (2 marks)**
- [ ] **MVC Architecture Pattern** implemented using Spring Boot
- [ ] Clear separation: Model (JPA Entities), View (Thymeleaf templates), Controller (REST APIs)
- [ ] Architecture diagram showing all layers

### **D. DESIGN PATTERNS & PRINCIPLES (3 marks)**

**Design Patterns (8 total - 2 per member):**
- [ ] **Shrish**: 
  - State Pattern (Behavioral) - Booking lifecycle management
  - Observer Pattern (Behavioral) - Real-time seat availability updates via WebSocket
- [ ] **Vaishnav**:
  - Decorator Pattern (Structural) - Dynamic F&B add-ons to bookings
  - Facade Pattern (Structural) - Unified payment gateway interface
- [ ] **Saffiya**:
  - Abstract Factory Pattern (Creational) - Theater type configurations (Regular/IMAX/4DX)
  - Singleton Pattern (Creational) - Database connection pool manager
- [ ] **Rushad**:
  - Chain of Responsibility (Behavioral) - Booking validation pipeline
  - Strategy Pattern (Behavioral) - Dynamic pricing algorithms

**Design Principles (8 total - 2 per member):**
- [ ] **Shrish**: 
  - Single Responsibility Principle (SRP) - Each class has one clear purpose
  - Information Expert (GRASP) - Objects manage their own data
- [ ] **Vaishnav**: 
  - Open-Closed Principle - Extensible without modification
  - Polymorphism (GRASP) - Uniform interfaces for varied implementations
- [ ] **Saffiya**: 
  - High Cohesion (GRASP) - Related functionality grouped together
  - Creator (GRASP) - Objects create what they're responsible for
- [ ] **Rushad**: 
  - Low Coupling (GRASP) - Minimal dependencies between components
  - Controller (GRASP) - Coordination of workflows

### **E. FEATURES/USE CASES**

**Major Use Cases (4 total - 1 per member):**
- [ ] **Shrish**: Interactive Seat Selection with Real-time Updates
- [ ] **Vaishnav**: Payment Processing & Food/Beverage Integration
- [ ] **Saffiya**: Movie Catalog Management & Smart Recommendations
- [ ] **Rushad**: Theater Management & Dynamic Pricing Engine

**Minor Use Cases (4 total - 1 per member):**
- [ ] **Shrish**: User Profile Management & Booking History
- [ ] **Vaishnav**: Booking Cancellation & Refund Processing
- [ ] **Saffiya**: Advanced Search & Filtering System
- [ ] **Rushad**: Analytics Dashboard & Revenue Reports

### **F. IMPLEMENTATION REQUIREMENTS**
- [ ] **Java** (JDK 17 or above)
- [ ] **Spring Boot 3.x** - MVC framework (MANDATORY)
- [ ] **Thymeleaf** - Server-side templating engine
- [ ] **Bootstrap 5** - Responsive UI framework
- [ ] **PostgreSQL** - Relational database with ACID compliance
- [ ] **Spring Data JPA** - ORM for database operations
- [ ] **Spring Security** - Authentication & Authorization
- [ ] **WebSocket (STOMP protocol)** - Real-time seat updates
- [ ] **Maven** - Dependency management and build tool
- [ ] **Web application** (Desktop OR Web - NO mobile apps)
- [ ] **Database persistence** with proper schema design
- [ ] All use cases integrated into **single application**

### **G. PRESENTATION/DEMO (3 marks)**
- [ ] Team-based presentation prepared
- [ ] Live demonstration of working application
- [ ] Individual code walkthroughs explaining patterns and principles
- [ ] Each member explains their module completely

### **H. SUBMISSION - PDF REPORT**
- [ ] **Title page** using PESU template (course code, project title, team member details with USNs)
- [ ] **Problem statement** with synopsis and motivation
- [ ] **All UML diagrams**:
  - Use Case diagram (system-wide)
  - Class diagram (complete with all entities and patterns)
  - 4 Activity diagrams (one per member)
  - 4 State diagrams (one per member)
- [ ] **MVC Architecture** documentation with layer explanations
- [ ] **Design Principles** (8 principles with detailed explanations of application)
- [ ] **Design Patterns** (8 patterns with UML diagrams and explanations)
- [ ] **GitHub repository link** (must be PUBLIC and accessible)
- [ ] **Individual contributions** section documenting each member's work
- [ ] **Screenshots** with populated inputs and outputs (WHITE background mandatory)

### **I. CRITICAL CONSTRAINTS & NOTES**
- ⚠️ Each of the 8 design patterns must be from different categories or clearly differentiated
- ⚠️ The 4th pattern per category should NOT be one enforced by the framework itself
- ⚠️ Each student evaluated individually despite team presentation
- ⚠️ Screenshots MUST have WHITE backgrounds (not dark mode)
- ⚠️ Each member must own COMPLETE use case (not just frontend or just backend)
- ⚠️ Equal participation must be demonstrated and documented

---

## **📋 EXECUTIVE SUMMARY**

### **Problem Statement**

Traditional movie ticket booking systems suffer from critical limitations:

1. **Lack of Real-time Updates**: Users cannot see live seat availability, leading to booking conflicts and poor user experience
2. **No Integrated Services**: Food and beverage ordering is separate from ticket booking, causing inconvenience
3. **Static Pricing Models**: Theater managers cannot implement dynamic pricing strategies based on demand, time, or seat location
4. **Limited Analytics**: No comprehensive reporting for revenue analysis and occupancy tracking
5. **Poor Recommendation Systems**: Users receive no personalized movie suggestions based on preferences

### **Proposed Solution**

The Movie Ticket Booking System is a comprehensive web-based application that addresses all these gaps through:

**For Customers:**
- Browse movie catalog with filters (genre, language, rating, theater type)
- View personalized movie recommendations based on booking history
- Interactive seat selection with real-time availability updates via WebSocket
- Pre-order food and beverages with ticket bookings
- Multiple payment options with secure processing
- Booking management (view history, cancel, get refunds)
- QR code tickets for contactless entry

**For Theater Managers:**
- Add/edit movies with complete metadata (cast, crew, trailer, poster)
- Configure theaters with different types (Regular, IMAX, 4DX)
- Schedule showtimes with conflict detection
- Implement dynamic pricing strategies (time-based, day-based, seat-based, demand-based)
- Generate comprehensive analytics reports (revenue, occupancy, trends)
- Manage seat layouts and configurations per screen

**For System:**
- Prevent double bookings through seat locking mechanism
- Real-time synchronization across multiple users
- Secure payment processing with transaction management
- Email notifications for confirmations and cancellations
- Database-backed persistence with ACID compliance

### **Target Users**

1. **End Customers**:
   - Movie enthusiasts who want convenient online booking
   - Families planning outings with food pre-orders
   - Users seeking personalized recommendations

2. **Theater Managers**:
   - Multiplex administrators managing multiple screens
   - Pricing strategists optimizing revenue
   - Operations managers tracking performance

3. **System Administrators**:
   - IT staff managing user accounts and system health
   - Support staff handling customer queries

---

## **🎬 DETAILED FUNCTIONAL REQUIREMENTS BY MODULE**

### **MODULE 1: SHRISH - SEAT SELECTION & REAL-TIME UPDATES**

#### **Major Use Case: Interactive Seat Selection with Real-time Updates**

**Actors**: Customer, System, Other Concurrent Users

**Preconditions**:
- User is logged in
- User has selected a movie and showtime
- Seats exist for the selected showtime

**Main Flow**:
1. User navigates to seat selection page for chosen showtime
2. System displays interactive seat map with current availability
3. System shows color-coded seats:
   - Green: Available for selection
   - Red: Already booked by someone
   - Yellow: Currently locked by another user (in their booking process)
   - Blue: Selected by current user
4. User clicks seats to select/deselect (maximum 10 seats)
5. System immediately locks selected seats for 10 minutes
6. System broadcasts seat status change to all connected users via WebSocket
7. Other users see the seats turn yellow on their screens in real-time
8. System calculates total price based on seat types and pricing strategy
9. User proceeds to food selection or directly to payment

**Alternative Flows**:
- **A1**: User tries to select already booked seat → System shows error message
- **A2**: User tries to select locked seat → System shows "Another user is booking this seat"
- **A3**: User's seat lock expires (10 minutes) → System releases seats back to available pool and notifies all users
- **A4**: User leaves page without booking → System automatically releases locks after timeout

**Postconditions**:
- Selected seats are locked for current user
- All other users see updated seat availability
- Booking moves to next stage (F&B or payment)

**Non-Functional Requirements**:
- Seat map must load within 2 seconds
- Real-time updates must reflect within 500ms across all connected clients
- System must handle concurrent seat selection by 1000+ users
- Seat locks must be persisted in database (survive server restarts)

**Observable Pattern Application**:
- **Subject**: SeatAvailabilitySubject (manages seat status)
- **Observers**: All connected WebSocket clients viewing same showtime
- **Notification**: Whenever seat status changes (selected/booked/released), all observers receive instant update
- **Implementation**: Using Spring WebSocket with STOMP protocol

**State Pattern Application**:
- **Context**: BookingContext (manages booking lifecycle)
- **States**: 
  - PendingState: Initial state when seats selected
  - LockedState: Seats temporarily reserved (10 min timer)
  - ConfirmedState: Payment successful, booking confirmed
  - CancelledState: User cancelled booking
  - RefundedState: Refund processed
- **Transitions**: Each state defines valid operations and next states
- **Benefit**: Clean separation of behavior for each booking stage

**Design Principles**:
- **Single Responsibility**: SeatSelectionService handles only seat operations, BookingService handles bookings
- **Information Expert**: Seat entity knows its own availability status and can validate its state

---

#### **Minor Use Case: User Profile Management & Booking History**

**Actors**: Customer, System

**Preconditions**:
- User is logged in

**Main Flow**:
1. User navigates to profile page
2. System displays user information (name, email, phone, preferences)
3. User can edit personal details
4. User can view booking history with filters (upcoming/past/cancelled)
5. User can click on any booking to view complete details:
   - Movie name, theater, screen, showtime
   - Seat numbers and types
   - Food items ordered (if any)
   - Total amount paid
   - QR code for entry
   - Booking ID and timestamp
6. User can download booking receipt as PDF
7. User can cancel upcoming bookings (redirects to cancellation flow)

**Features**:
- Password change functionality
- Email/SMS notification preferences
- Preferred genres and languages
- Loyalty points tracking (optional enhancement)

**Non-Functional Requirements**:
- Profile updates must reflect immediately
- Booking history should paginate (10 bookings per page)
- PDF generation should complete within 3 seconds

---

### **MODULE 2: VAISHNAV - PAYMENT PROCESSING & F&B INTEGRATION**

#### **Major Use Case: Payment Processing with F&B Integration**

**Actors**: Customer, System, Payment Gateway, Kitchen System (optional)

**Preconditions**:
- User has selected seats
- Seats are locked for user
- User is on F&B/payment page

**Main Flow**:
1. System displays food & beverage menu organized by categories:
   - Popcorn (Small/Medium/Large)
   - Beverages (Soft drinks, Water, Coffee)
   - Snacks (Nachos, Samosas, Sandwiches)
   - Combos (Couple Combo, Family Combo, etc.)
2. User browses menu with images and prices
3. User adds items to cart with quantity selection
4. System applies Decorator Pattern to wrap base booking with F&B items
5. System calculates running total (tickets + F&B + GST)
6. User can apply promo code (validated by Chain of Responsibility)
7. System shows order summary:
   - Seat details and ticket cost
   - Food items with quantities and subtotal
   - Taxes (GST breakdown)
   - Discount (if promo applied)
   - Final payable amount
8. User selects payment method (Card/UPI/Net Banking/Wallet)
9. System routes payment through Facade to appropriate gateway
10. User enters payment details and confirms
11. System processes payment and generates transaction ID
12. On success:
    - Booking state changes to Confirmed
    - Generate QR code with booking details
    - Send confirmation email with ticket and F&B details
    - Release seat locks (seats now permanently booked)
    - Notify kitchen system about F&B order (optional)
13. System displays success page with QR code and booking details

**Alternative Flows**:
- **A1**: Payment fails → System shows error, keeps seats locked for retry (within 10 min window)
- **A2**: User cancels payment → System releases seat locks after 2 minutes
- **A3**: Seat lock expires during payment → System cancels payment attempt, releases seats, shows timeout message
- **A4**: Invalid promo code → System shows error, proceeds without discount

**Postconditions**:
- Payment transaction recorded in database
- Booking confirmed and stored
- Seats permanently marked as booked
- Email/SMS notification sent
- F&B order sent to kitchen (if applicable)

**Decorator Pattern Application**:
- **Component**: Booking interface with getCost() and getDescription() methods
- **Concrete Component**: BaseBooking (contains only ticket information)
- **Decorators**:
  - SnackDecorator: Adds snack items and cost
  - BeverageDecorator: Adds beverage items and cost
  - ComboDecorator: Adds combo deals with discounted pricing
- **Benefit**: Dynamically add F&B items without modifying base booking class; clean cost calculation

**Facade Pattern Application**:
- **Subsystems**: CardPaymentGateway, UPIPaymentGateway, NetBankingGateway, WalletGateway
- **Facade**: PaymentFacade provides unified interface with methods:
  - processPayment(paymentRequest)
  - verifyPayment(transactionId)
  - processRefund(transactionId, amount)
- **Benefit**: Client code doesn't need to know about complex gateway integrations; easy to add new payment methods

**Design Principles**:
- **Open-Closed Principle**: Can add new payment methods and F&B decorators without modifying existing classes
- **Polymorphism (GRASP)**: All decorators and payment gateways treated uniformly through interfaces

---

#### **Minor Use Case: Booking Cancellation & Refund Processing**

**Actors**: Customer, System, Payment Gateway

**Preconditions**:
- User has confirmed booking
- Booking is not already cancelled
- Show has not yet started

**Main Flow**:
1. User selects booking to cancel from booking history
2. System displays booking details
3. System calculates refund amount based on cancellation policy:
   - >24 hours before show: 100% refund
   - 6-24 hours before show: 50% refund
   - <6 hours before show: No refund (or platform credit)
4. System shows refund breakdown to user
5. User confirms cancellation
6. System:
   - Changes booking state to Cancelled
   - Releases booked seats back to available pool
   - Initiates refund to original payment method
   - Generates refund transaction ID
   - Sends cancellation confirmation email
7. Refund is processed by payment gateway (3-7 business days)
8. System tracks refund status and updates user

**Alternative Flows**:
- **A1**: Refund fails → System retries automatically, notifies support team
- **A2**: User cancels within no-refund period → System offers platform credit instead

**Postconditions**:
- Booking marked as cancelled
- Seats released and available for others
- Refund initiated
- All users see seats as available again (via Observer pattern)

---

### **MODULE 3: SAFFIYA - MOVIE CATALOG & RECOMMENDATIONS**

#### **Major Use Case: Movie Catalog Management & Smart Recommendations**

**Actors**: Customer, System

**Preconditions**:
- User has accessed the application (logged in or guest)

**Main Flow**:
1. User lands on homepage
2. System displays featured movies in sections:
   - Now Showing (currently running)
   - Coming Soon (upcoming releases)
   - Trending This Week
   - Recommended For You (personalized)
3. User browses movies in grid/list view
4. Each movie card shows:
   - Poster image
   - Title and language
   - Genre tags
   - Rating (U/U/A/A) and IMDb score
   - Duration
   - Quick book button
5. User can use filters:
   - Genre (Action, Comedy, Drama, Horror, Thriller, Romance, Sci-Fi, etc.)
   - Language (English, Hindi, Kannada, Tamil, Telugu, etc.)
   - Rating/Certification
   - Theater Type (Regular, IMAX, 4DX)
6. User clicks on movie for details
7. System displays comprehensive movie page:
   - Large poster and trailer video
   - Synopsis/plot summary
   - Cast and crew information
   - User ratings and reviews
   - Available showtimes across all theaters (grouped by theater)
8. System shows personalized recommendations:
   - "Because you watched [previous movie]"
   - "Similar movies you might like"
   - "Popular in your area"
9. User selects showtime to proceed with booking

**Recommendation Algorithm**:
- **Content-Based**: Movies with similar genres to user's watch history
- **Collaborative Filtering**: Movies liked by users with similar preferences
- **Popularity-Based**: Trending movies in user's location
- **Time-Based**: Recent releases matching user's genre preferences

**Abstract Factory Pattern Application**:
- **Factories**: RegularTheaterFactory, IMAXTheaterFactory, FourDXTheaterFactory
- **Products Created**:
  - SeatConfiguration (layout, total seats, seat types)
  - PricingModel (base price, multipliers)
  - TheaterEquipment (screen specs, sound system, special features)
- **Theater Types**:
  - **Regular**: 150 seats, standard screen (40 ft), Dolby 5.1, base price ₹150
  - **IMAX**: 300 seats, IMAX screen (72 ft), 12-channel sound, laser projection, base price ₹400
  - **4DX**: 100 seats, motion seats, environmental effects (wind, water, scent), base price ₹500
- **Benefit**: Ensures all theater components are compatible; easy to add new theater types

**Singleton Pattern Application**:
- **Class**: DatabaseConnectionManager or ApplicationCacheManager
- **Purpose**: Single instance manages database connection pool across entire application
- **Benefit**: Prevents resource wastage from multiple connection pools; global access point

**Design Principles**:
- **High Cohesion (GRASP)**: MovieService contains all movie-related operations (browse, search, recommend)
- **Creator (GRASP)**: TheaterFactory creates Seat and PricingModel objects it's responsible for

---

#### **Minor Use Case: Advanced Search & Filtering System**

**Actors**: Customer, System

**Preconditions**:
- User is on movies page

**Main Flow**:
1. User enters search query in search bar
2. System performs real-time search across:
   - Movie titles
   - Actor names
   - Director names
   - Genre keywords
3. System displays matching results instantly (autocomplete)
4. User can apply multiple filters simultaneously:
   - Genre (multi-select)
   - Language (multi-select)
   - Rating/Certification
   - Theater type
   - Date range (for upcoming movies)
   - Price range (for budget filtering)
5. User can sort results by:
   - Popularity (default)
   - Release date (newest/oldest)
   - Rating (highest/lowest)
   - Name (A-Z/Z-A)
6. System shows result count and applies filters dynamically
7. User clicks on any result to view details

**Non-Functional Requirements**:
- Search results must appear within 300ms
- Support partial/fuzzy matching for typos
- Handle 10,000+ concurrent search requests

---

### **MODULE 4: RUSHAD - THEATER MANAGEMENT & DYNAMIC PRICING**

#### **Major Use Case: Theater Management & Dynamic Pricing Engine**

**Actors**: Theater Manager, System

**Preconditions**:
- User is logged in as Theater Manager/Admin

**Main Flow**:

**Part A: Movie Management**
1. Manager navigates to admin dashboard
2. Manager clicks "Add Movie"
3. System displays movie creation form
4. Manager enters:
   - Title, description, synopsis
   - Genre, language, certification
   - Duration, release date
   - Poster image URL, trailer video URL
   - Cast members (names and roles)
   - Director, producer names
5. Manager submits form
6. System validates inputs and saves movie to database
7. Movie appears in "Upcoming" section until release date

**Part B: Theater Configuration**
1. Manager clicks "Manage Theaters"
2. Manager adds new theater with:
   - Theater name and location
   - Theater type (Regular/IMAX/4DX)
3. System uses Abstract Factory to create theater configuration:
   - For IMAX: 300 seats, premium equipment, higher pricing
   - For 4DX: 100 motion seats, special effects, highest pricing
4. Manager configures screens within theater:
   - Screen number, total capacity
   - Custom seat layout (rows and columns)
   - Seat type designation (Regular/Premium/VIP/Motion)
5. System generates seat map and saves configuration

**Part C: Showtime Scheduling**
1. Manager selects movie to schedule
2. Manager selects theater and screen
3. Manager picks date and time
4. System checks for conflicts (same screen, overlapping times)
5. If no conflict:
   - Manager sets base ticket price
   - Manager selects pricing strategy
6. System creates showtime and makes available for booking

**Part D: Dynamic Pricing Configuration**
1. Manager navigates to "Pricing Strategies"
2. System shows available strategies:
   - Matinee Pricing (morning shows: -20%)
   - Weekend Pricing (Sat/Sun: +25%)
   - Holiday Pricing (special days: +40%)
   - Seat-Zone Pricing (front: -10%, middle: standard, back: -5%)
   - Last-Minute Discounts (unsold seats: -15% within 3 hours of show)
3. Manager can activate/deactivate strategies for specific shows
4. System applies active strategies during price calculation
5. Prices update dynamically based on rules

**Chain of Responsibility Pattern Application**:
- **Validation Pipeline** for booking creation:
  1. **SeatAvailabilityHandler**: Checks if seats are available and not locked by others
  2. **PaymentValidationHandler**: Validates payment details (card number, CVV, expiry, amount)
  3. **UserEligibilityHandler**: Checks age restrictions (A/U/A certification), booking limits (max 10 seats)
  4. **PromoCodeHandler**: Validates and applies promo code discounts
  5. **FinalBookingHandler**: Creates confirmed booking in database
- **Flow**: Request passes through each handler; if any fails, chain stops and returns specific error
- **Benefit**: Each handler has single responsibility; easy to add/remove/reorder validation steps

**Strategy Pattern Application**:
- **Context**: PricingContext holds current showtime and pricing strategy
- **Strategy Interface**: PricingStrategy with calculatePrice(seat, showtime) method
- **Concrete Strategies**:
  - **MatineePricingStrategy**: Reduces price by 20% for shows before 12 PM
  - **WeekendPricingStrategy**: Increases price by 25% for Saturday/Sunday
  - **HolidayPricingStrategy**: Increases price by 40% for configured holiday dates
  - **SeatZonePricingStrategy**: Adjusts price based on row position (front cheaper, middle premium)
  - **DemandBasedPricingStrategy**: Dynamic pricing based on occupancy rate
- **Selection**: System automatically selects appropriate strategy based on showtime date/time
- **Benefit**: Easy to add new pricing algorithms without changing existing code; strategies can be combined

**Design Principles**:
- **Low Coupling (GRASP)**: Theater management module doesn't depend on booking logic directly
- **Controller (GRASP)**: TheaterManagementController coordinates between services (MovieService, TheaterService, ShowtimeService)

---

#### **Minor Use Case: Analytics Dashboard & Revenue Reports**

**Actors**: Theater Manager, System

**Preconditions**:
- User is logged in as Theater Manager/Admin

**Main Flow**:
1. Manager navigates to analytics dashboard
2. System displays key metrics:
   - Total revenue (today/week/month/year)
   - Total bookings count
   - Average ticket price
   - Occupancy rate percentage
3. Manager selects report type:
   - Revenue Report
   - Occupancy Report
   - Movie Performance Report
4. Manager selects date range and filters (theater, movie, screen)
5. System generates report with:
   - **Revenue Report**:
     - Total revenue breakdown (tickets + F&B)
     - Revenue by payment method
     - Revenue by theater/screen
     - Day-wise revenue trend (line chart)
     - Top revenue-generating movies (bar chart)
   - **Occupancy Report**:
     - Overall occupancy percentage
     - Peak time analysis (which hours have most bookings)
     - Day-wise occupancy trend
     - Screen-wise utilization
     - Heatmap of seat selection patterns
   - **Movie Performance Report**:
     - Total bookings per movie
     - Average rating per movie
     - Revenue per movie
     - Show count and average occupancy
6. Manager can export reports as PDF or Excel
7. Manager can schedule automated weekly reports via email

**Visualizations**:
- Line charts for revenue/occupancy trends
- Bar charts for comparisons (movies, theaters)
- Pie charts for revenue distribution
- Heatmaps for seat selection patterns

**Non-Functional Requirements**:
- Reports must generate within 5 seconds for date ranges up to 1 year
- Support concurrent report generation by multiple managers
- Export files should be well-formatted and professional

---

## **🎯 SYSTEM-WIDE FEATURES**

### **1. User Authentication & Authorization**

**Registration**:
- Email/phone-based registration
- Password strength validation
- Email verification link
- Profile completion (name, DOB, gender, location)

**Login**:
- Email + password authentication
- "Remember me" option (session persistence)
- "Forgot password" with email reset link
- Account lockout after 5 failed attempts

**Roles**:
- **Customer**: Can browse, book, manage bookings
- **Theater Manager**: Can manage movies, theaters, showtimes, pricing, view reports
- **Admin**: Full system access, user management, system configuration

**Security**:
- Passwords hashed using BCrypt
- JWT tokens for session management
- Role-based access control (RBAC)
- HTTPS for all communications

---

### **2. Real-time Seat Updates (WebSocket)**

**Technology**: Spring WebSocket with STOMP protocol

**Implementation**:
- Each showtime has a unique WebSocket topic: `/topic/seats/{showtimeId}`
- When user selects seats: Broadcast to all subscribers
- When payment confirms: Broadcast seat status change to "BOOKED"
- When lock expires: Broadcast seat release to "AVAILABLE"

**Message Format**:
```
{
  "showtimeId": 123,
  "updatedSeats": [
    {"seatId": 45, "status": "LOCKED", "userId": "user123"},
    {"seatId": 46, "status": "LOCKED", "userId": "user123"}
  ],
  "timestamp": "2026-02-09T10:30:00Z"
}
```

**Benefits**:
- No polling required
- Instant updates (< 500ms latency)
- Scalable to thousands of concurrent users
- Prevents double bookings

---

### **3. Seat Locking Mechanism**

**Purpose**: Temporarily reserve seats during booking process

**Flow**:
1. User selects seats → System creates SeatLock records with 10-minute expiry
2. Database table: `seat_locks (seat_id, user_id, showtime_id, locked_until)`
3. Background job runs every minute to release expired locks
4. When lock expires: Seats become available again, broadcast to all users

**Benefits**:
- Prevents race conditions
- Fair allocation (first to lock gets priority)
- Automatic cleanup

---

### **4. QR Code Generation**

**Purpose**: Contactless entry at theaters

**Contents**:
- Booking ID
- User name
- Movie title
- Theater, screen, showtime
- Seat numbers
- Verification hash (prevents tampering)

**Format**: QR code image (PNG) embedded in confirmation email and displayed on booking details page

**Verification**: Theater staff scan QR code → System validates booking exists and is confirmed → Allow entry

---

### **5. Email Notifications**

**Triggers**:
- Registration: Welcome email with verification link
- Booking confirmed: Ticket details + QR code + F&B order
- Booking cancelled: Cancellation confirmation + refund details
- Show reminder: 3 hours before showtime
- Refund processed: Refund success notification

**Content**: HTML emails with branding, booking details, actionable buttons

---

### **6. Promo Code System**

**Attributes**:
- Code (unique, e.g., "FIRST50", "WEEKEND20")
- Discount type (percentage or fixed amount)
- Discount value
- Minimum booking amount
- Valid from/until dates
- Maximum usage count
- Current usage count

**Validation** (via Chain of Responsibility):
- Code exists and is active
- Current date within validity period
- Usage limit not exceeded
- Booking amount meets minimum requirement

**Application**: Discount applied during payment calculation

---

## **🏛️ MVC ARCHITECTURE EXPLANATION**

### **Model Layer**:
- **JPA Entities**: User, Movie, Theater, Screen, Seat, Showtime, Booking, Payment, FoodItem, SeatLock, PromoCode
- **Repositories**: Spring Data JPA repositories for CRUD operations
- **Business Logic**: Separate from entities, resides in Service layer

### **View Layer**:
- **Thymeleaf Templates**: Server-side HTML rendering
- **Pages**: Homepage, movie details, seat selection, payment, booking history, admin dashboard
- **Frontend**: Bootstrap 5 for styling, JavaScript for interactivity, WebSocket client for real-time updates

### **Controller Layer**:
- **REST Controllers**: Handle HTTP requests, call services, return responses
- **WebSocket Controllers**: Handle WebSocket connections and messages
- **Examples**: MovieController, BookingController, PaymentController, AdminController

### **Flow Example** (User books a ticket):
1. **View**: User clicks "Book" on seat selection page
2. **Controller**: BookingController receives POST request
3. **Service**: BookingService validates (Chain of Responsibility), calculates price (Strategy), locks seats, creates booking (State Pattern)
4. **Repository**: Saves Booking entity to database
5. **Controller**: Returns success response
6. **View**: Redirects to payment page

---

## **✅ SUBMISSION DELIVERABLES SUMMARY**

### **Code Repository (GitHub)**:
- Clean folder structure (controllers, services, repositories, entities, config)
- README with setup instructions
- SQL schema files
- Sample data for testing
- .gitignore (exclude IDE files, logs)

### **PDF Report**:
- Title page (PESU template)
- Table of contents
- Problem statement (1-2 pages)
- UML diagrams (Use Case, Class, 4 Activity, 4 State)
- MVC architecture diagram
- Design patterns (8 patterns with explanations and mini-UML diagrams)
- Design principles (8 principles with code examples)
- Database schema (ER diagram + table descriptions)
- Screenshots (white background, labeled clearly)
- Individual contributions table
- GitHub link

### **Presentation**:
- Live demo of application
- Code walkthrough by each member
- Explanation of patterns and principles used
- Q&A handling

---