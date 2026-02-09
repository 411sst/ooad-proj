# **FILE 4: IMPLEMENTATION GUIDE PER MODULE**

---

## **Overview**

This document provides detailed technical implementation guidance for each team member's module. It covers architecture decisions, API specifications, frontend requirements, integration points, and testing strategies.

**Important**: This guide describes WHAT to implement and HOW to structure it, without providing actual code. Each team member should implement their module following these specifications.

---

## **GENERAL PROJECT STRUCTURE**

### **Technology Stack**:
- **Backend**: Spring Boot 3.2.x with Java 17
- **Frontend**: Thymeleaf + Bootstrap 5 + JavaScript + jQuery
- **Database**: PostgreSQL 15+
- **Real-time**: WebSocket with STOMP protocol
- **Build Tool**: Maven 3.8+
- **Security**: Spring Security 6.x with JWT
- **ORM**: Spring Data JPA with Hibernate
- **Email**: Spring Mail
- **QR Code**: ZXing library

### **Project Directory Structure**:
```
movie-booking-system/
├── src/
│   ├── main/
│   │   ├── java/com/moviebooking/
│   │   │   ├── config/              # Configuration classes
│   │   │   │   ├── SecurityConfig
│   │   │   │   ├── WebSocketConfig
│   │   │   │   └── DatabaseConfig
│   │   │   ├── controller/          # REST Controllers
│   │   │   │   ├── MovieController
│   │   │   │   ├── BookingController
│   │   │   │   ├── SeatController
│   │   │   │   ├── PaymentController
│   │   │   │   └── AdminController
│   │   │   ├── entity/              # JPA Entities
│   │   │   │   ├── User
│   │   │   │   ├── Movie
│   │   │   │   ├── Booking
│   │   │   │   ├── Seat
│   │   │   │   └── ...
│   │   │   ├── repository/          # Data Access Layer
│   │   │   │   ├── UserRepository
│   │   │   │   ├── MovieRepository
│   │   │   │   └── ...
│   │   │   ├── service/             # Business Logic
│   │   │   │   ├── MovieService
│   │   │   │   ├── BookingService
│   │   │   │   └── ...
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── exception/           # Custom Exceptions
│   │   │   ├── patterns/            # Design Pattern Implementations
│   │   │   │   ├── state/           # State Pattern
│   │   │   │   ├── observer/        # Observer Pattern
│   │   │   │   ├── decorator/       # Decorator Pattern
│   │   │   │   ├── facade/          # Facade Pattern
│   │   │   │   ├── factory/         # Abstract Factory Pattern
│   │   │   │   ├── singleton/       # Singleton Pattern
│   │   │   │   ├── chain/           # Chain of Responsibility
│   │   │   │   └── strategy/        # Strategy Pattern
│   │   │   └── util/                # Utility Classes
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/              # CSS, JS, Images
│   │       └── templates/           # Thymeleaf Templates
│   └── test/                        # Unit and Integration Tests
├── pom.xml                          # Maven Dependencies
└── README.md                        # Setup Instructions
```

### **Key Maven Dependencies**:
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-security
- spring-boot-starter-websocket
- spring-boot-starter-thymeleaf
- spring-boot-starter-mail
- postgresql
- lombok
- zxing (QR code generation)
- junit and mockito (testing)

### **Application Properties Configuration**:
Must configure:
- Database connection (PostgreSQL)
- JWT secret key and expiration
- Email server settings (SMTP)
- WebSocket endpoint configuration
- File upload settings (for posters)
- Logging levels
- CORS settings

---

## **MODULE 1: SHRISH - SEAT SELECTION & REAL-TIME UPDATES**

### **Responsibilities**:
1. Interactive seat map display with color coding
2. Real-time seat availability updates via WebSocket
3. Seat locking mechanism (10-minute timer)
4. Booking lifecycle management with State Pattern
5. Observer Pattern implementation for seat notifications
6. User profile and booking history management

---

### **A. SEAT SELECTION SYSTEM**

#### **Database Tables Owned**:
- `seats` - Read operations
- `seat_locks` - Full CRUD operations
- `bookings` - Create PENDING/LOCKED bookings
- `booking_seats` - Junction table management

#### **API Endpoints to Implement**:

**1. GET /api/showtimes/{showtimeId}/seats**
- **Purpose**: Fetch seat map for a showtime
- **Request**: Path variable showtimeId
- **Response**: 
  - Seat layout (rows and columns)
  - List of all seats with current status
  - Seat types and prices
  - Screen information
- **Logic**:
  - Query seats table for given showtime's screen
  - Check seat_locks table for active locks
  - Check booking_seats for already booked seats
  - Mark each seat with status: AVAILABLE, LOCKED, BOOKED
  - Apply pricing strategy to calculate current prices
- **Design Principle**: Information Expert - Seat entity knows its own status

**2. POST /api/seats/lock**
- **Purpose**: Lock selected seats for 10 minutes
- **Request Body**:
  - showtimeId
  - List of seatIds
  - userId
- **Response**:
  - Success/failure status
  - Lock expiry timestamp
  - Total price calculation
- **Logic**:
  - Validate seats are available (not locked by others, not booked)
  - Check user not exceeding 10 seat limit
  - Create seat_lock records with locked_until = now + 10 minutes
  - Trigger Observer Pattern broadcast
  - Calculate total price using Strategy Pattern
- **Error Handling**:
  - 409 Conflict if seats already locked/booked
  - 400 Bad Request if invalid seat selection

**3. POST /api/seats/unlock**
- **Purpose**: Release user's locked seats
- **Request Body**:
  - showtimeId
  - userId
- **Response**: Success status
- **Logic**:
  - Delete or deactivate seat_locks for this user-showtime combination
  - Trigger Observer Pattern broadcast (seats now available)

**4. POST /api/bookings/create**
- **Purpose**: Create booking in PENDING state
- **Request Body**:
  - userId
  - showtimeId
  - List of seatIds
  - Promo code (optional)
- **Response**:
  - Booking object with PENDING status
  - Booking reference number
  - Total amount
- **Logic**:
  - Validate user has active seat locks for these seats
  - Create Booking entity with status = PENDING
  - Use State Pattern - BookingContext starts in PendingState
  - Generate unique booking reference (format: BK + YYYYMMDD + seq)
  - Do NOT mark seats as booked yet (happens after payment)
  - Keep seat locks active
- **Design Pattern**: State Pattern initialization

---

#### **State Pattern Implementation**:

**Classes to Create**:

1. **BookingState Interface**:
   - Methods: confirm(), cancel(), refund(), getStateName()

2. **Concrete State Classes**:
   - **PendingState**: 
     - confirm() → transitions to ConfirmedState (not allowed, must lock first)
     - cancel() → transitions to CancelledState
     - refund() → throws IllegalStateException
   - **LockedState**:
     - confirm() → transitions to ConfirmedState (when payment succeeds)
     - cancel() → transitions to CancelledState
     - refund() → throws IllegalStateException
     - Has internal 10-minute timer
   - **ConfirmedState**:
     - confirm() → throws IllegalStateException (already confirmed)
     - cancel() → transitions to CancelledState (with refund policy check)
     - refund() → transitions to RefundedState
   - **CancelledState**:
     - All operations throw IllegalStateException except refund()
     - refund() → transitions to RefundedState
   - **RefundedState**:
     - Terminal state, all operations throw IllegalStateException

3. **BookingContext Class**:
   - Holds reference to current BookingState
   - Holds reference to Booking entity
   - Delegates operations to current state
   - Provides setState() method for state transitions

**State Transition Triggers**:
- PENDING → LOCKED: When seats are locked
- LOCKED → CONFIRMED: When PaymentService confirms payment success
- LOCKED → PENDING: When 10-minute lock timer expires
- CONFIRMED → CANCELLED: When user cancels (if allowed by policy)
- CANCELLED → REFUNDED: When refund processing completes

**Integration with BookingService**:
- BookingService creates BookingContext for each booking
- Uses context methods (confirm(), cancel(), refund()) instead of direct status updates
- State transitions trigger database updates and Observer notifications

**Design Principle Applied**: Single Responsibility Principle - each state class handles behavior for one state only

---

#### **Observer Pattern Implementation**:

**Classes to Create**:

1. **Subject Interface**:
   - Methods: attach(observer), detach(observer), notifyObservers(event)

2. **SeatAvailabilitySubject Class** (implements Subject):
   - Maintains list of observers (WebSocket sessions)
   - Method: notifyObservers() sends WebSocket message to all connected clients
   - Method: updateSeatStatus() updates database and notifies observers

3. **SeatObserver Interface**:
   - Method: update(seatUpdateEvent)

4. **WebSocketSeatObserver Class** (implements SeatObserver):
   - Wraps WebSocket session
   - update() method sends message to specific client

5. **SeatUpdateEvent Class** (DTO):
   - Fields: showtimeId, List of updated seats with new statuses, timestamp

**WebSocket Configuration**:
- Endpoint: /ws (for initial connection)
- Message broker: /topic
- Application destination prefix: /app
- Topic for seat updates: /topic/seats/{showtimeId}

**WebSocket Message Flow**:
1. Client connects to /ws endpoint using SockJS + STOMP
2. Client subscribes to /topic/seats/{showtimeId}
3. When user locks/unlocks seats:
   - Backend updates database
   - SeatAvailabilitySubject.notifyObservers() called
   - WebSocket message broadcast to all subscribers
4. All connected clients receive update and refresh seat map

**Message Format** (JSON):
```
{
  "showtimeId": 123,
  "updatedSeats": [
    {"seatId": 45, "status": "LOCKED", "lockedBy": "user123"},
    {"seatId": 46, "status": "LOCKED", "lockedBy": "user123"}
  ],
  "timestamp": "2026-02-09T10:30:00Z"
}
```

**Integration Points**:
- SeatService calls SeatAvailabilitySubject.updateSeatStatus() after any seat status change
- BookingService calls subject when booking confirmed/cancelled
- Background job calls subject when locks expire

**Design Principle Applied**: Information Expert - SeatAvailabilitySubject knows which observers to notify

---

#### **Background Job for Lock Expiry**:

**Purpose**: Automatically release expired seat locks

**Implementation Approach**:
- Use Spring's @Scheduled annotation
- Run every 60 seconds (1 minute)
- Query: SELECT * FROM seat_locks WHERE locked_until < NOW() AND is_active = TRUE
- For each expired lock:
  - Update is_active = FALSE
  - Trigger Observer Pattern to broadcast seat availability
  - Log expiry event

**Considerations**:
- Use database transactions to prevent race conditions
- Consider distributed locking if running multiple server instances

---

#### **Frontend Implementation**:

**Page: Seat Selection (seat-selection.html)**

**Layout**:
- Header: Movie title, theater name, showtime
- Seat map: Grid layout showing all seats
- Legend: Color codes (Available=green, Locked=yellow, Booked=red, Selected=blue)
- Selection summary: Selected seats list, total price
- Action buttons: Clear selection, Proceed to payment

**Seat Map Rendering**:
- Use HTML table or CSS grid to display seats
- Each seat is a clickable div/button
- Color coding:
  - Green (#28a745): Available seats
  - Yellow (#ffc107): Locked by others
  - Red (#dc3545): Already booked
  - Blue (#007bff): Selected by current user
  - Gray (#6c757d): Unavailable (broken/removed)
- Screen indicator at top showing "SCREEN THIS WAY"
- Seat labels show row and number (A1, A2, etc.)

**JavaScript Requirements**:
1. **WebSocket Connection**:
   - Use SockJS and STOMP.js libraries
   - Connect to /ws endpoint on page load
   - Subscribe to /topic/seats/{showtimeId}
   - Handle incoming seat update messages
   - Update seat colors in real-time

2. **Seat Selection Logic**:
   - Click handler on available seats
   - Toggle selection (add/remove from selected list)
   - Maximum 10 seats validation
   - Update selection summary (seats + total price)
   - Disable already booked/locked seats

3. **Lock Seats on Selection**:
   - When user selects seats, immediately call POST /api/seats/lock
   - Show loading indicator during API call
   - On success: Update UI, start 10-minute countdown timer
   - On failure: Show error message, revert selection

4. **Countdown Timer**:
   - Display remaining time (10:00 countdown)
   - Update every second
   - When timer reaches 0:00, show "Seats released" message
   - Redirect to showtime selection or refresh page

5. **Proceed to Payment**:
   - Call POST /api/bookings/create
   - On success: Redirect to F&B/payment page with booking ID
   - On failure: Show error, allow retry

**Responsive Design**:
- Seat map should be scrollable on mobile
- Zoom controls for better visibility
- Touch-friendly seat selection (larger tap targets)

---

### **B. USER PROFILE & BOOKING HISTORY**

#### **API Endpoints to Implement**:

**1. GET /api/users/profile**
- **Purpose**: Get current user's profile
- **Response**: User object (exclude password)
- **Logic**: Fetch from users table, include preferences

**2. PUT /api/users/profile**
- **Purpose**: Update user profile
- **Request Body**: Updated user fields
- **Response**: Updated user object
- **Validation**: Email format, phone format, date of birth

**3. GET /api/users/bookings**
- **Purpose**: Get user's booking history
- **Query Parameters**: 
  - status (filter: ALL, CONFIRMED, CANCELLED)
  - page, size (pagination)
- **Response**: Paginated list of bookings with movie/theater details
- **Logic**: 
  - Query bookings table filtered by userId
  - Join with movies, theaters, showtimes for complete info
  - Order by booking_datetime DESC

**4. GET /api/users/bookings/{bookingId}**
- **Purpose**: Get detailed booking information
- **Response**:
  - Full booking details
  - Seat numbers and types
  - Food items ordered
  - Payment details
  - QR code URL
- **Authorization**: Verify booking belongs to current user

**5. GET /api/users/bookings/{bookingId}/receipt**
- **Purpose**: Download booking receipt as PDF
- **Response**: PDF file
- **Logic**:
  - Generate PDF with booking details
  - Include QR code
  - Format professionally with company branding

**6. POST /api/users/password-change**
- **Purpose**: Change user password
- **Request Body**: Old password, new password
- **Logic**:
  - Verify old password
  - Hash new password with BCrypt
  - Update database
- **Security**: Require re-authentication

---

#### **Frontend Implementation**:

**Page: User Profile (profile.html)**

**Sections**:
1. **Personal Information**:
   - Display: Name, email, phone, date of birth, gender
   - Edit button → Enable editing
   - Save/Cancel buttons when editing

2. **Booking History**:
   - Tab navigation: Upcoming, Past, Cancelled
   - List of bookings with:
     - Movie poster thumbnail
     - Movie title, theater name
     - Showtime, screen number
     - Seats booked
     - Booking status badge
     - Action buttons (View Details, Cancel, Download Receipt)
   - Pagination controls

3. **Password Change**:
   - Form: Current password, new password, confirm password
   - Validation: Password strength meter
   - Submit button

**Interactions**:
- Edit profile: Enable form fields, show Save/Cancel
- View booking details: Modal or navigate to booking details page
- Cancel booking: Confirmation dialog, then redirect to Vaishnav's cancellation flow
- Download receipt: Trigger PDF download

---

### **C. TESTING STRATEGY FOR SHRISH'S MODULE**

**Unit Tests**:
1. Test each State class in isolation:
   - PendingState.confirm() should throw exception
   - LockedState.confirm() should transition to ConfirmedState
   - Test all invalid state transitions
2. Test SeatService methods:
   - lockSeats() with available seats → success
   - lockSeats() with already locked seats → failure
   - unlockSeats() removes locks correctly
3. Test Observer Pattern:
   - Mock observers, verify notifyObservers() calls update() on all

**Integration Tests**:
1. Test seat locking flow end-to-end:
   - Call lock API → Check database seat_locks created
   - Verify WebSocket message broadcast
2. Test lock expiry:
   - Create lock with past locked_until timestamp
   - Run scheduled job
   - Verify lock deactivated and broadcast sent
3. Test booking state transitions:
   - Create PENDING booking → Lock seats → Confirm payment → Verify CONFIRMED state

**WebSocket Tests**:
1. Test client connection and subscription
2. Test message sending and receiving
3. Test concurrent clients receiving same updates

**Manual Testing Checklist**:
- [ ] Open seat map, verify colors correct
- [ ] Select seats, verify lock API called
- [ ] Open same showtime in another browser, verify seats show as locked
- [ ] Wait 10 minutes, verify seats auto-release
- [ ] Complete payment, verify seats show as booked in all browsers
- [ ] Cancel booking, verify seats released

---

## **MODULE 2: VAISHNAV - PAYMENT PROCESSING & F&B INTEGRATION**

### **Responsibilities**:
1. Food & Beverage menu display and ordering
2. Decorator Pattern for dynamically adding F&B to bookings
3. Payment gateway integration with Facade Pattern
4. Multiple payment method support
5. QR code generation
6. Booking cancellation and refund processing

---

### **A. FOOD & BEVERAGE ORDERING SYSTEM**

#### **Database Tables Owned**:
- `food_items` - Read operations
- `booking_food` - Create operations
- `payments` - Full CRUD operations

#### **API Endpoints to Implement**:

**1. GET /api/food/menu**
- **Purpose**: Fetch F&B menu
- **Response**:
  - List of food items grouped by category
  - Each item: id, name, description, price, image URL, vegetarian flag
- **Logic**: Query food_items where is_available = TRUE, group by category

**2. POST /api/bookings/{bookingId}/add-food**
- **Purpose**: Add F&B items to booking using Decorator Pattern
- **Request Body**:
  - List of {foodItemId, quantity}
- **Response**:
  - Updated booking with F&B items
  - Updated total amount
- **Logic**:
  - Fetch booking (must be in PENDING or LOCKED state)
  - Apply Decorator Pattern:
    - Start with BaseBooking
    - For each food item, wrap with appropriate decorator (SnackDecorator, BeverageDecorator, etc.)
  - Insert records into booking_food table
  - Recalculate booking.food_amount
  - Recalculate booking.total_amount (tickets + food + tax - discount)
  - Return updated booking
- **Design Pattern**: Decorator Pattern application

**3. DELETE /api/bookings/{bookingId}/remove-food/{foodItemId}**
- **Purpose**: Remove F&B item from booking
- **Logic**: Delete from booking_food, recalculate totals

**4. GET /api/bookings/{bookingId}/summary**
- **Purpose**: Get order summary before payment
- **Response**:
  - Ticket details (seats, prices)
  - F&B items (name, quantity, subtotal)
  - Tax breakdown (GST 18%)
  - Discount (if promo applied)
  - Final total amount
- **Logic**: Aggregate from booking, booking_seats, booking_food tables

---

#### **Decorator Pattern Implementation**:

**Purpose**: Dynamically add F&B items to bookings without modifying base Booking class

**Classes to Create**:

1. **Booking Interface**:
   - Methods: getCost(), getDescription(), getItems()

2. **BaseBooking Class** (implements Booking):
   - Contains: List of seats, showtime reference
   - getCost(): Returns sum of ticket prices
   - getDescription(): Returns "X tickets for [movie title]"
   - getItems(): Returns empty list (no F&B items)

3. **BookingDecorator Abstract Class** (implements Booking):
   - Has a field: Booking wrappedBooking
   - Constructor accepts Booking object
   - Methods delegate to wrappedBooking by default

4. **Concrete Decorator Classes**:
   - **SnackDecorator**:
     - Additional fields: FoodItem snackItem, int quantity
     - getCost(): wrappedBooking.getCost() + (snackItem.price × quantity)
     - getDescription(): wrappedBooking.getDescription() + ", " + quantity + "x " + snackItem.name
     - getItems(): wrappedBooking.getItems() + new Item(snackItem, quantity)
   - **BeverageDecorator**:
     - Similar structure for beverages
   - **ComboDecorator**:
     - Contains: FoodCombo combo (which has multiple items)
     - getCost(): wrappedBooking.getCost() + combo.discountedPrice
     - Combos have special pricing (e.g., Couple Combo cheaper than buying items separately)

**Usage Pattern**:
```
// Start with base booking
Booking booking = new BaseBooking(seats, showtime);

// User adds large popcorn (quantity: 2)
booking = new SnackDecorator(booking, popcorn, 2);

// User adds coke (quantity: 2)
booking = new BeverageDecorator(booking, coke, 2);

// User adds couple combo
booking = new ComboDecorator(booking, coupleCombo);

// Get final cost
double totalCost = booking.getCost();
// Output: ticket_cost + popcorn_cost + coke_cost + combo_cost
```

**Integration with BookingService**:
- When F&B items added via API, construct decorator chain
- Store final cost in booking.food_amount
- Store items in booking_food table
- Decorator pattern used for calculation, not persisted directly

**Design Principles Applied**:
- **Open-Closed Principle**: Can add new decorator types without modifying existing classes
- **Polymorphism (GRASP)**: All decorators treated uniformly through Booking interface

---

### **B. PAYMENT PROCESSING SYSTEM**

#### **API Endpoints to Implement**:

**1. POST /api/payments/process**
- **Purpose**: Process payment through appropriate gateway
- **Request Body**:
  - bookingId
  - paymentMethod (CREDIT_CARD, DEBIT_CARD, UPI, NET_BANKING, WALLET)
  - paymentDetails (card number, CVV, expiry, UPI ID, bank code, wallet ID)
  - amount
- **Response**:
  - Payment ID
  - Transaction ID (from gateway)
  - Status (SUCCESS, FAILED, PENDING)
  - Additional data (QR code for UPI, redirect URL for net banking)
- **Logic**:
  - Validate booking exists and is in LOCKED state
  - Validate amount matches booking.total_amount
  - Create Payment record with status = INITIATED
  - Use PaymentFacade to route to appropriate gateway
  - Based on gateway response:
    - SUCCESS: 
      - Update Payment.status = SUCCESS
      - Call BookingService to confirm booking (State Pattern: LOCKED → CONFIRMED)
      - Generate QR code
      - Send confirmation email
      - Release seat locks (seats now permanently booked)
    - FAILED:
      - Update Payment.status = FAILED
      - Log error reason
      - Return error to user
    - PENDING (for UPI/Net Banking):
      - Update Payment.status = PROCESSING
      - Return QR code or redirect URL
      - Set up polling mechanism
- **Error Handling**:
  - 400 Bad Request: Invalid payment details
  - 402 Payment Required: Insufficient funds (if applicable)
  - 500 Internal Server Error: Gateway communication failure

**2. GET /api/payments/{transactionId}/status**
- **Purpose**: Check payment status (for UPI/Net Banking)
- **Response**: Payment status
- **Logic**:
  - Query payments table
  - If status = PROCESSING, call gateway to verify
  - Update status based on gateway response
  - If SUCCESS, trigger booking confirmation

**3. POST /api/payments/{transactionId}/retry**
- **Purpose**: Retry failed payment
- **Logic**: Create new payment attempt with same booking

**4. POST /api/payments/{transactionId}/refund**
- **Purpose**: Process refund (called by cancellation flow)
- **Request Body**: refund amount
- **Response**: Refund transaction ID, status
- **Logic**:
  - Fetch original payment
  - Use PaymentFacade to initiate refund through original gateway
  - Create new Payment record with type = REFUND
  - Update original Payment.refund_amount and refund_transaction_id
  - Update Booking state to REFUNDED

---

#### **Facade Pattern Implementation**:

**Purpose**: Provide unified interface for multiple payment gateways, hiding complexity

**Classes to Create**:

1. **PaymentFacade Class**:
   - Central interface for all payment operations
   - Methods:
     - processPayment(PaymentRequest) → PaymentResponse
     - verifyPayment(transactionId) → PaymentStatus
     - processRefund(transactionId, amount) → RefundResponse
   - Routing logic:
     - Based on paymentMethod, delegates to appropriate gateway
     - Handles common operations (logging, error handling, retry logic)
     - Returns unified response format

2. **Gateway Interface**:
   - Methods: authorize(), capture(), verify(), refund()

3. **Concrete Gateway Classes**:
   - **CardPaymentGateway** (implements Gateway):
     - Simulates Stripe/Razorpay integration
     - Methods:
       - authorize(): Validates card details, checks 3D Secure
       - capture(): Captures authorized payment
       - verify(): Checks transaction status
       - refund(): Initiates refund to original card
   - **UPIPaymentGateway**:
     - Simulates UPI payment flow
     - Methods:
       - generateQRCode(): Creates UPI payment QR code
       - pollStatus(): Checks if user completed payment
       - verify(): Confirms payment received
       - refund(): Refunds to UPI ID
   - **NetBankingGateway**:
     - Simulates net banking redirect flow
     - Methods:
       - generateRedirectURL(): Creates bank login URL
       - verifyCallback(): Validates bank's callback
       - refund(): Initiates bank transfer refund
   - **WalletGateway**:
     - Simulates digital wallet (Paytm, PhonePe, etc.)
     - Methods:
       - deductBalance(): Deducts from wallet
       - checkBalance(): Verifies sufficient funds
       - refund(): Adds refund to wallet

**Facade Method Example - processPayment()**:
```
Logic flow:
1. Validate payment request (amount, booking exists, etc.)
2. Based on paymentMethod, select appropriate gateway:
   - CARD → CardPaymentGateway
   - UPI → UPIPaymentGateway
   - NET_BANKING → NetBankingGateway
   - WALLET → WalletGateway
3. Call gateway.authorize()
4. If authorized, call gateway.capture()
5. Handle gateway-specific responses:
   - Card: Immediate success/failure
   - UPI: Return QR code, set status to PENDING
   - Net Banking: Return redirect URL, set status to PENDING
   - Wallet: Immediate success/failure
6. Log transaction details
7. Return unified PaymentResponse
```

**Error Handling in Facade**:
- Catch gateway-specific exceptions
- Convert to application-level exceptions
- Log detailed error information
- Return user-friendly error messages

**Benefits**:
- **Client code simplicity**: PaymentController only interacts with PaymentFacade, not individual gateways
- **Easy to add new gateways**: Just create new gateway class, add routing logic in facade
- **Centralized logging**: All payment operations logged in one place
- **Unified error handling**: Consistent error responses regardless of gateway

**Design Principles Applied**:
- **Open-Closed Principle**: Can add new payment methods without modifying existing code
- **Polymorphism (GRASP)**: All gateways treated uniformly through Gateway interface

---

#### **QR Code Generation**:

**Library**: ZXing (Zebra Crossing)

**Implementation**:
- QR code generated after successful booking confirmation
- Content encoded in QR code:
  ```
  {
    "bookingId": "BK20260209001",
    "userName": "Shrish Kumar",
    "movieTitle": "Avengers: Endgame",
    "theater": "PVR Phoenix Mall",
    "screen": "Audi 3",
    "showtime": "2026-02-15 19:30",
    "seats": ["A5", "A6"],
    "verificationHash": "abc123def456..."
  }
  ```
- Verification hash: HMAC-SHA256 of booking data with secret key
- QR code image saved to file system or cloud storage
- URL stored in booking.qr_code_url
- QR code included in confirmation email

**Verification at Theater**:
- Theater staff scan QR code
- Backend API endpoint: POST /api/bookings/verify-qr
- Request body: QR code content
- Validates:
  - Booking exists
  - Booking status is CONFIRMED
  - Verification hash matches
  - Showtime hasn't passed
- Response: Valid/Invalid with booking details

---

### **C. BOOKING CANCELLATION & REFUND PROCESSING**

#### **API Endpoints to Implement**:

**1. POST /api/bookings/{bookingId}/cancel**
- **Purpose**: Cancel confirmed booking
- **Request Body**: Cancellation reason (optional)
- **Response**:
  - Cancellation confirmation
  - Refund amount
  - Refund processing timeline
- **Logic**:
  - Fetch booking (must be in CONFIRMED state)
  - Use BookingContext.cancel() (State Pattern: CONFIRMED → CANCELLED)
  - Calculate refund amount based on cancellation policy:
    - >24 hours before show: 100% refund
    - 6-24 hours before show: 50% refund
    - <6 hours before show: No refund (or 25% platform credit)
  - Call PaymentFacade.processRefund()
  - Update booking status
  - Release seats back to available pool
  - Trigger Observer Pattern broadcast (seats now available)
  - Send cancellation email
  - Update showtime.available_seats counter
- **Authorization**: Verify user owns this booking

**2. GET /api/bookings/{bookingId}/refund-policy**
- **Purpose**: Show refund amount before cancellation
- **Response**:
  - Hours until showtime
  - Refund percentage
  - Refund amount
  - Non-refundable amount
- **Logic**: Calculate based on showtime date-time vs current time

**3. GET /api/payments/{paymentId}/refund-status**
- **Purpose**: Check refund processing status
- **Response**: Refund status, expected completion date
- **Logic**: Query payment gateway for refund status

---

#### **Frontend Implementation**:

**Page: Food & Beverage Selection (food-beverage.html)**

**Layout**:
- Header: Booking reference, selected seats, movie info
- F&B Menu: Grid of food items with images, names, prices
- Category tabs: Popcorn, Beverages, Snacks, Combos
- Cart summary (sidebar): Selected items, quantities, subtotal
- Action buttons: Skip F&B, Continue to payment

**JavaScript Requirements**:
1. Display menu from GET /api/food/menu
2. Add to cart: Click handler on food items
   - Show quantity selector
   - Update cart summary
   - Calculate running subtotal
3. Remove from cart: Click handler on cart items
4. Skip F&B: Proceed directly to payment page
5. Continue: Call POST /api/bookings/{id}/add-food, then redirect to payment

**Page: Payment (payment.html)**

**Layout**:
- Order summary:
  - Tickets: List of seats with prices
  - F&B: List of items with quantities and prices
  - Subtotal
  - GST (18%): Amount
  - Discount (if promo applied): Amount
  - **Total Payable**: Final amount (prominent)
- Payment method selection:
  - Radio buttons: Credit/Debit Card, UPI, Net Banking, Wallet
- Payment details form (dynamic based on method):
  - Card: Number, expiry, CVV, name
  - UPI: UPI ID
  - Net Banking: Bank dropdown
  - Wallet: Wallet ID
- Promo code section: Input field, Apply button
- Pay Now button

**JavaScript Requirements**:
1. Fetch order summary from GET /api/bookings/{id}/summary
2. Display total breakdown clearly
3. Payment method selection: Show/hide appropriate form fields
4. Form validation:
   - Card: Luhn algorithm validation, expiry date check
   - UPI: Regex for UPI ID format
5. Apply promo code:
   - Call validation API
   - If valid, update discount and total
   - Show success/error message
6. Pay Now:
   - Call POST /api/payments/process
   - Show loading indicator
   - Handle responses:
     - SUCCESS: Show success page with QR code, booking details
     - FAILED: Show error, allow retry
     - PENDING (UPI): Show QR code, start polling for status
     - PENDING (Net Banking): Redirect to bank URL

**Page: Payment Success (payment-success.html)**

**Layout**:
- Success icon/animation
- Booking confirmation message
- Booking reference number (large, copyable)
- QR code (display prominently)
- Booking details: Movie, theater, screen, showtime, seats
- F&B items (if any)
- Total amount paid
- Action buttons:
  - Download ticket (PDF)
  - View booking details
  - Home

**Page: Booking Cancellation (cancel-booking.html)**

**Layout**:
- Booking details: Movie, showtime, seats, amount paid
- Cancellation policy explanation
- Refund calculation:
  - Original amount
  - Refund percentage based on policy
  - Refund amount (highlighted)
  - Non-refundable amount
- Cancellation reason (optional text area)
- Confirmation checkbox: "I understand the cancellation policy"
- Buttons: Cancel Booking (red), Go Back

**JavaScript Requirements**:
1. Fetch booking details and refund policy
2. Show refund calculation prominently
3. Disable "Cancel Booking" button until checkbox checked
4. Cancel Booking click:
   - Confirmation dialog
   - Call POST /api/bookings/{id}/cancel
   - Show loading
   - On success: Show cancellation confirmation with refund details
   - On error: Show error message

---

### **D. TESTING STRATEGY FOR VAISHNAV'S MODULE**

**Unit Tests**:
1. Test Decorator Pattern:
   - BaseBooking.getCost() returns correct ticket total
   - SnackDecorator correctly adds snack cost
   - BeverageDecorator correctly adds beverage cost
   - Combo decorator applies discount correctly
   - Multiple decorators stack correctly
2. Test PaymentFacade routing:
   - CARD payment routes to CardPaymentGateway
   - UPI payment routes to UPIPaymentGateway
   - Verify each gateway method called
3. Test refund calculation:
   - >24 hours: 100% refund
   - 6-24 hours: 50% refund
   - <6 hours: 0% refund

**Integration Tests**:
1. F&B ordering flow:
   - Add items → Check booking_food records created
   - Remove items → Check records deleted
   - Verify total_amount recalculated
2. Payment processing flow:
   - Lock seats → Add F&B → Process payment → Verify booking confirmed
   - Failed payment → Verify booking still locked, seats not released
3. Cancellation flow:
   - Confirm booking → Cancel → Verify refund initiated → Verify seats released

**Mock Gateway Tests**:
1. Mock CardPaymentGateway responses (success, failure, timeout)
2. Verify PaymentFacade handles each response correctly
3. Test retry logic on failure

**Manual Testing Checklist**:
- [ ] Add multiple F&B items, verify cart updates
- [ ] Remove items, verify cart updates
- [ ] Apply invalid promo code, verify error shown
- [ ] Apply valid promo code, verify discount applied
- [ ] Pay with card, verify success flow
- [ ] Simulate payment failure, verify error handling
- [ ] Cancel booking, verify refund calculated correctly
- [ ] Verify QR code generated and displayed

---

## **MODULE 3: SAFFIYA - MOVIE CATALOG & RECOMMENDATIONS**

### **Responsibilities**:
1. Movie browsing and display
2. Advanced search and filtering
3. Personalized recommendation engine
4. Theater configuration using Abstract Factory Pattern
5. Singleton Pattern for database connection management
6. Movie details page with showtimes

---

### **A. MOVIE CATALOG SYSTEM**

#### **Database Tables Owned**:
- `movies` - Full CRUD (admin operations)
- `movie_cast` - Full CRUD
- `user_preferences` - Read and update
- `reviews` - Create and read

#### **API Endpoints to Implement**:

**1. GET /api/movies**
- **Purpose**: Fetch movies with filtering and sorting
- **Query Parameters**:
  - status (NOW_SHOWING, UPCOMING, ENDED, ALL)
  - genre (comma-separated)
  - language (comma-separated)
  - certification (U, UA, A)
  - theaterType (REGULAR, IMAX, FOUR_DX)
  - page, size (pagination)
  - sort (popularity, release_date, rating, title)
- **Response**: Paginated list of movies
- **Logic**:
  - Build dynamic query based on filters
  - Apply sorting
  - Return page of results
- **Design Principle**: High Cohesion - all movie browsing logic in one place

**2. GET /api/movies/{movieId}**
- **Purpose**: Get detailed movie information
- **Response**:
  - Complete movie details
  - Cast and crew list
  - Average rating
  - Recent reviews (top 10)
  - Available showtimes (grouped by theater and date)
- **Logic**:
  - Fetch movie entity
  - Join with movie_cast for cast list
  - Aggregate reviews for average rating
  - Fetch showtimes where movie_id = {movieId} AND show_date >= today

**3. GET /api/movies/search**
- **Purpose**: Search movies by keyword
- **Query Parameters**:
  - q (search query)
  - searchIn (title, actor, director, all)
- **Response**: List of matching movies
- **Logic**:
  - Perform full-text search on movies table
  - If searchIn includes actor/director, search movie_cast table
  - Rank results by relevance
  - Support fuzzy matching for typos

**4. GET /api/movies/recommendations**
- **Purpose**: Get personalized movie recommendations
- **Authorization**: Requires logged-in user
- **Response**: List of recommended movies
- **Logic**:
  - Fetch user's booking history
  - Identify preferred genres from history
  - Identify preferred languages
  - Apply recommendation algorithms (detailed below)
  - Return top 10 recommendations

**5. GET /api/movies/trending**
- **Purpose**: Get trending movies
- **Response**: List of movies sorted by popularity
- **Logic**:
  - Calculate popularity score = (bookings in last 7 days × 0.7) + (average rating × 0.3)
  - Order by popularity score DESC
  - Return top 20

---

#### **Recommendation Engine**:

**Algorithms to Implement**:

1. **Content-Based Filtering**:
   - Find movies with similar genres to user's watch history
   - Scoring: Count genre overlap
   - Example: User watched Action & Sci-Fi → Recommend other Action/Sci-Fi movies

2. **Collaborative Filtering** (Simplified):
   - Find users with similar booking patterns
   - Recommend movies those similar users watched
   - Similarity metric: Jaccard similarity on movie sets
   - Example: User A and User B both watched Movie1, Movie2 → If User B also watched Movie3, recommend Movie3 to User A

3. **Popularity-Based**:
   - Recommend trending movies in user's preferred genres
   - Consider location (popular in same city)

4. **Hybrid Approach**:
   - Combine all three algorithms with weights:
     - Content-based: 40%
     - Collaborative: 30%
     - Popularity: 30%
   - Calculate combined score for each candidate movie
   - Sort by score DESC
   - Return top 10

**Implementation Steps**:
1. Fetch user's booking history (last 10 bookings)
2. Extract genres and languages from watched movies
3. For content-based:
   - Query movies with matching genres
   - Filter by preferred languages
   - Exclude already watched movies
4. For collaborative:
   - Find users with overlapping bookings (>2 common movies)
   - Get movies those users watched but current user hasn't
5. For popularity:
   - Get trending movies filtered by user's genre preferences
6. Combine scores and sort
7. Return recommendations

**Caching Strategy**:
- Cache recommendations per user for 1 hour
- Refresh when user makes new booking
- Use Singleton cache manager

---

### **B. ABSTRACT FACTORY PATTERN IMPLEMENTATION**

**Purpose**: Create different theater types with their specific configurations

**Classes to Create**:

1. **TheaterFactory Interface**:
   - Methods:
     - createSeatConfiguration() → SeatConfiguration
     - createPricingModel() → PricingModel
     - createEquipment() → TheaterEquipment

2. **Product Interfaces**:
   - **SeatConfiguration**:
     - Methods: getTotalSeats(), getSeatLayout(), getSeatTypes()
   - **PricingModel**:
     - Methods: getBasePrice(), calculatePrice(seatType, showtime)
   - **TheaterEquipment**:
     - Methods: getScreenType(), getSoundSystem(), getSpecialFeatures()

3. **Concrete Factory - RegularTheaterFactory**:
   - createSeatConfiguration() returns RegularSeatConfiguration:
     - 150 seats
     - 10 rows × 15 seats per row
     - Seat types: REGULAR (rows A-D, H-J), PREMIUM (rows E-G)
   - createPricingModel() returns RegularPricingModel:
     - Base price: ₹150
     - PREMIUM seats: 1.5× multiplier
     - Simple time-based adjustment (matinee: 0.8×)
   - createEquipment() returns RegularTheaterEquipment:
     - Screen: Standard 40 ft
     - Sound: Dolby Digital 5.1
     - Features: AC, Fire Safety

4. **Concrete Factory - IMAXTheaterFactory**:
   - createSeatConfiguration() returns IMAXSeatConfiguration:
     - 300 seats
     - 15 rows × 20 seats per row
     - Seat types: REGULAR (rows A-E, L-O), PREMIUM (rows F-K), RECLINER (row K)
   - createPricingModel() returns IMAXPricingModel:
     - Base price: ₹400
     - PREMIUM seats: 1.3× multiplier
     - RECLINER seats: 1.8× multiplier
     - Minimal time discount (matinee: 0.9×)
   - createEquipment() returns IMAXTheaterEquipment:
     - Screen: IMAX 72 ft
     - Sound: IMAX 12-channel
     - Features: Laser Projection, Dual 4K Projectors, AC

5. **Concrete Factory - FourDXTheaterFactory**:
   - createSeatConfiguration() returns FourDXSeatConfiguration:
     - 100 seats
     - 10 rows × 10 seats per row
     - All seats: MOTION_SEAT type
   - createPricingModel() returns FourDXPricingModel:
     - Base price: ₹500 (uniform for all motion seats)
     - No seat type variation (all same)
   - createEquipment() returns FourDXTheaterEquipment:
     - Screen: 4DX 50 ft
     - Sound: Dolby Atmos 7.1
     - Features: Motion Seats, Wind Effects, Water Sprays, Scent Generators, Fog Machines, Strobe Lights, AC

**Integration with TheaterService**:
- When admin creates new theater:
  - Select theater type (REGULAR, IMAX, FOUR_DX)
  - TheaterService.createTheater(name, location, type):
    - Get appropriate factory: getFactory(type)
    - Call factory.createSeatConfiguration()
    - Call factory.createPricingModel()
    - Call factory.createEquipment()
    - Save theater entity with configurations
    - Generate seat records based on configuration
- Ensures all components (seats, pricing, equipment) are compatible for given theater type

**Design Principles Applied**:
- **High Cohesion (GRASP)**: Each factory creates all related components for one theater type
- **Creator (GRASP)**: Factory creates objects it's responsible for

---

### **C. SINGLETON PATTERN IMPLEMENTATION**

**Purpose**: Ensure single instance of resource-intensive or globally-shared objects

**Options to Implement**:

**Option 1: DatabaseConnectionManager Singleton**

**Why**: Database connection pools should be shared across application

**Implementation Approach**:
- Private constructor prevents external instantiation
- Static getInstance() method provides access
- Thread-safe lazy initialization using double-checked locking
- Manages HikariCP connection pool
- Provides getConnection() method
- Provides shutdown() method for graceful cleanup

**Usage**:
- Services call DatabaseConnectionManager.getInstance().getConnection()
- Single pool shared by all services
- Prevents resource wastage from multiple pools

**Option 2: ApplicationCacheManager Singleton** (Alternative)

**Why**: Cache should be shared across application

**Implementation Approach**:
- Similar singleton structure
- Manages in-memory cache for frequently accessed data:
  - Movie catalog
  - Theater configurations
  - User preferences
- Methods: put(key, value), get(key), clear(), evict(key)
- Cache eviction policy: LRU (Least Recently Used)
- TTL (Time To Live) for each entry

**Spring Note**: In Spring Boot, beans are singletons by default when annotated with @Service, @Component, etc. This is an alternative to manual singleton implementation. For the project, you can:
- Implement manual singleton (as described above) to explicitly demonstrate pattern knowledge
- OR annotate a class with @Component and explain that Spring manages it as a singleton

**Design Principle**: Singleton ensures controlled access to shared resource

---

### **D. FRONTEND IMPLEMENTATION**

**Page: Homepage (index.html)**

**Sections**:
1. **Hero Section**:
   - Carousel of featured movies (large banners)
   - Auto-rotating every 5 seconds
   - CTA button: "Book Now"

2. **Now Showing**:
   - Grid of movie cards (4 per row on desktop, responsive on mobile)
   - Each card: Poster, title, genre tags, rating badge, "Book" button
   - Horizontal scrollable on mobile

3. **Coming Soon**:
   - Similar grid layout
   - "Notify Me" button instead of "Book"

4. **Recommended For You** (if logged in):
   - Personalized recommendations from API
   - "Because you watched [Movie X]" heading

5. **Trending This Week**:
   - Grid of popular movies
   - "Trending" badge overlay

**JavaScript Requirements**:
1. Fetch movies for each section from appropriate APIs
2. Render movie cards dynamically
3. Lazy loading for images (load as user scrolls)
4. Carousel auto-rotation
5. Smooth scrolling for horizontal grids

**Page: Movie Details (movie-details.html)**

**Layout**:
- **Header Section**:
  - Large movie poster (left)
  - Movie info (right):
    - Title, language, certification, duration
    - Genre tags
    - Synopsis
    - Cast & crew
    - Average rating (star display)
    - "Write Review" button
- **Trailer Section**:
  - Embedded YouTube video player
- **Showtimes Section**:
  - Grouped by date (tabs: Today, Tomorrow, This Weekend)
  - Within each date, grouped by theater
  - Each showtime: Time, theater name, screen, available seats, "Book" button
- **Reviews Section**:
  - List of recent reviews
  - User name, rating, review text, timestamp
  - Pagination or "Load More"

**JavaScript Requirements**:
1. Fetch movie details from GET /api/movies/{id}
2. Render all sections
3. Date tab switching (fetch showtimes for selected date)
4. Theater grouping logic
5. "Book" button: Navigate to seat selection with showtime ID

**Page: Search & Filter (search.html)**

**Layout**:
- **Search Bar** (prominent):
  - Text input with auto-suggest
  - Search button
- **Filter Sidebar**:
  - Genre checkboxes (multi-select)
  - Language checkboxes
  - Certification radio buttons
  - Theater type checkboxes
  - "Apply Filters" and "Clear Filters" buttons
- **Sort Dropdown**:
  - Options: Popularity, Release Date, Rating, Title A-Z
- **Results Grid**:
  - Movie cards (same as homepage)
  - Pagination controls
  - Result count: "Showing X movies"

**JavaScript Requirements**:
1. Real-time search with debouncing (300ms delay)
2. Auto-suggest: Show matching movie titles as user types
3. Filter changes: Update URL parameters, fetch filtered results
4. Sort changes: Re-fetch with new sort parameter
5. Pagination: Fetch next/previous page
6. Deep linking: URL reflects current filters/search (shareable)

---

### **E. TESTING STRATEGY FOR SAFFIYA'S MODULE**

**Unit Tests**:
1. Test Abstract Factory:
   - RegularTheaterFactory.createSeatConfiguration() returns 150 seats
   - IMAXTheaterFactory.createPricingModel() returns ₹400 base price
   - FourDXTheaterFactory.createEquipment() includes Motion Seats
2. Test Singleton:
   - Multiple calls to getInstance() return same instance
   - Verify thread-safety with concurrent tests
3. Test Recommendation algorithms:
   - Content-based: User likes Action → Recommends Action movies
   - Collaborative: Users with similar taste get similar recommendations
   - Verify no duplicate recommendations

**Integration Tests**:
1. Movie search:
   - Search "Avengers" → Returns Avengers movies
   - Search "Chris Evans" (actor) → Returns movies with Chris Evans
   - Fuzzy search "Avangers" → Returns Avengers movies
2. Theater creation:
   - Create IMAX theater → Verify 300 seats generated
   - Create 4DX theater → Verify all MOTION_SEAT types
3. Recommendation API:
   - User with Action movie history → Recommendations include Action movies
   - New user (no history) → Returns trending movies

**Manual Testing Checklist**:
- [ ] Browse movies, verify filters work
- [ ] Search for movie by title, actor, director
- [ ] View movie details, verify all data displays
- [ ] Check recommendations (need user with booking history)
- [ ] Create theater via admin panel, verify seat layout generated correctly
- [ ] Verify Singleton - check connection pool stats, should show single pool

---

## **MODULE 4: RUSHAD - THEATER MANAGEMENT & DYNAMIC PRICING**

### **Responsibilities**:
1. Theater and screen configuration
2. Movie management (add/edit)
3. Showtime scheduling with conflict detection
4. Dynamic pricing using Strategy Pattern
5. Booking validation using Chain of Responsibility Pattern
6. Analytics dashboard and reporting

---

### **A. THEATER MANAGEMENT SYSTEM**

#### **Database Tables Owned**:
- `theaters` - Full CRUD
- `screens` - Full CRUD
- `showtimes` - Full CRUD
- `promo_codes` - Full CRUD

#### **API Endpoints to Implement**:

**1. POST /api/admin/theaters**
- **Purpose**: Create new theater
- **Request Body**:
  - name, location, city, state, pincode
  - theater_type (REGULAR, IMAX, FOUR_DX)
  - facilities (array)
- **Response**: Created theater with generated ID
- **Logic**:
  - Use Abstract Factory to get appropriate factory
  - Call factory to create seat configuration, pricing model, equipment
  - Insert theater record
  - Return theater object
- **Authorization**: Requires MANAGER or ADMIN role

**2. POST /api/admin/theaters/{theaterId}/screens**
- **Purpose**: Add screen to theater
- **Request Body**:
  - screen_number
  - screen_name (optional)
  - rows, columns
  - screen_type (must match or be REGULAR)
- **Response**: Created screen with generated seat layout
- **Logic**:
  - Validate screen_number is unique within theater
  - Create screen record
  - Generate seat records based on rows × columns
  - Assign seat types based on position (front: REGULAR, middle: PREMIUM, back: REGULAR)
  - Use Abstract Factory's SeatConfiguration for guidance
  - Return screen object

**3. POST /api/admin/movies**
- **Purpose**: Add new movie
- **Request Body**:
  - title, description, synopsis
  - genre, language, duration, certification
  - release_date
  - poster_url, trailer_url
  - director, producer
  - cast (array of {name, role, character})
- **Response**: Created movie
- **Logic**:
  - Create movie record
  - Insert cast records in movie_cast table
  - Set status = UPCOMING if release_date > today, else NOW_SHOWING
  - Return movie object

**4. PUT /api/admin/movies/{movieId}**
- **Purpose**: Edit existing movie
- **Request Body**: Updated fields
- **Response**: Updated movie
- **Logic**: Update movie and movie_cast records

**5. POST /api/admin/showtimes**
- **Purpose**: Schedule new showtime
- **Request Body**:
  - movie_id
  - screen_id
  - show_date, show_time
  - base_price
  - pricing_strategy (MATINEE, WEEKEND, HOLIDAY, STANDARD)
- **Response**: Created showtime
- **Logic**:
  - Validate movie exists and is available for booking
  - Validate screen exists
  - Check for scheduling conflicts:
    - Query existing showtimes for same screen
    - Calculate end_time = show_time + movie.duration + 15 min buffer
    - Ensure no overlap with existing showtimes
  - Create showtime record
  - Calculate show_datetime and end_datetime
  - Set available_seats = screen.total_seats
  - Return showtime object
- **Validation**: UNIQUE constraint on (screen_id, show_datetime) prevents double-booking

**6. DELETE /api/admin/showtimes/{showtimeId}**
- **Purpose**: Cancel showtime
- **Logic**:
  - Check if any bookings exist for this showtime
  - If yes: Prevent deletion, suggest rescheduling bookings first
  - If no: Delete showtime

---

### **B. DYNAMIC PRICING WITH STRATEGY PATTERN**

**Purpose**: Apply different pricing algorithms based on showtime conditions

**Classes to Create**:

1. **PricingStrategy Interface**:
   - Method: calculatePrice(baseprice, seatType, showtime) → double

2. **PricingContext Class**:
   - Holds reference to current strategy
   - Holds showtime and seat information
   - Method: setStrategy(PricingStrategy)
   - Method: calculateFinalPrice() → calls strategy.calculatePrice()

3. **Concrete Strategy Classes**:

**MatineePricingStrategy** (implements PricingStrategy):
- Logic: If showtime.hour < 12, apply 20% discount
- Formula: basePrice × seatTypeMultiplier × 0.8

**WeekendPricingStrategy**:
- Logic: If showtime is Saturday or Sunday, apply 25% markup
- Formula: basePrice × seatTypeMultiplier × 1.25

**HolidayPricingStrategy**:
- Logic: If showtime.date is in holiday list, apply 40% markup
- Formula: basePrice × seatTypeMultiplier × 1.4
- Holiday list: Store in database or configuration (New Year, Diwali, Christmas, etc.)

**SeatZonePricingStrategy**:
- Logic: Adjust price based on seat row position
  - Front rows (A-C): -10%
  - Middle rows (D-G): Standard (best view)
  - Back rows (H-J): -5%
- Formula: basePrice × seatTypeMultiplier × zoneMultiplier

**DemandBasedPricingStrategy** (Optional Enhancement):
- Logic: Adjust price based on current occupancy
  - <30% booked: -15% (encourage bookings)
  - 30-70% booked: Standard
  - >70% booked: +15% (high demand)
- Formula: basePrice × seatTypeMultiplier × demandMultiplier

4. **CombinedPricingStrategy** (Wrapper):
- Holds multiple strategies
- Applies all in sequence
- Example: WeekendPricingStrategy + SeatZonePricingStrategy
- Formula: Apply each multiplier cumulatively

**Strategy Selection Logic**:
- ShowtimeService.calculateSeatPrice(showtime, seat):
  1. Start with base price from showtime
  2. Determine applicable strategies:
     - Check showtime time → MatineeStrategy if morning
     - Check showtime day → WeekendStrategy if Sat/Sun
     - Check showtime date → HolidayStrategy if holiday
     - Always apply SeatZoneStrategy for seat position
  3. Create CombinedPricingStrategy with all applicable strategies
  4. Call strategy.calculatePrice()
  5. Return final price
  
**Storage**:
- Store selected strategy name in showtime.pricing_strategy field
- Manager can manually override strategy selection

**Design Principles Applied**:
- **Low Coupling (GRASP)**: Pricing logic decoupled from showtime/booking logic
- **Controller (GRASP)**: PricingService coordinates strategy selection and application

---

### **C. BOOKING VALIDATION WITH CHAIN OF RESPONSIBILITY**

**Purpose**: Validate booking through sequential checks before confirmation

**Classes to Create**:

1. **BookingValidationHandler Abstract Class**:
   - Field: BookingValidationHandler nextHandler
   - Method: setNext(handler) → returns handler (for fluent chaining)
   - Method: handle(request) → ValidationResult:
     - Calls validate(request)
     - If validation fails, returns failure result
     - If validation passes, calls nextHandler.handle() if exists
     - If no next handler, returns success
   - Abstract Method: validate(request) → ValidationResult

2. **ValidationResult Class** (DTO):
   - Fields: boolean isValid, String message, Object data
   - Static methods: success(), success(message, data), failure(message)

3. **BookingRequest Class** (DTO):
   - Fields: userId, showtimeId, seatIds, paymentDetails, promoCode, totalAmount

4. **Concrete Handler Classes**:

**SeatAvailabilityHandler** (extends BookingValidationHandler):
- Purpose: Check if requested seats are available
- validate() logic:
  - Query seat_locks: Check if seats locked by another user
  - Query booking_seats: Check if seats already booked for this showtime
  - If any seat unavailable: return failure("Seat X is not available")
  - If all available: return success()

**PaymentValidationHandler**:
- Purpose: Validate payment details
- validate() logic:
  - Check payment method is valid (CARD, UPI, NET_BANKING, WALLET)
  - Check amount matches expected booking total
  - If method = CARD:
    - Validate card number format (Luhn algorithm)
    - Validate CVV length (3-4 digits)
    - Validate expiry date not in past
  - If method = UPI:
    - Validate UPI ID format (regex: username@bankname)
  - If method = NET_BANKING:
    - Validate bank code exists in allowed list
  - If method = WALLET:
    - Validate wallet ID format
  - If any validation fails: return failure with specific error
  - If all pass: return success()

**UserEligibilityHandler**:
- Purpose: Check user meets requirements for booking
- validate() logic:
  - Fetch user by userId
  - Fetch movie certification (U, UA, A)
  - If certification = A and user.age < 18:
    - return failure("You must be 18+ to book this movie")
  - If certification = UA and user.age < 12:
    - return failure("You must be 12+ to book this movie")
  - Check number of seats <= 10:
    - If > 10: return failure("Maximum 10 seats allowed per booking")
  - Check if user already booked this showtime:
    - If duplicate: return failure("You have already booked this show")
  - If all checks pass: return success()

**PromoCodeHandler**:
- Purpose: Validate and apply promo code (if provided)
- validate() logic:
  - If no promo code in request: return success() (skip validation)
  - Fetch promo code from database
  - If not found: return failure("Invalid promo code")
  - Check validity:
    - If current_date < valid_from: return failure("Promo code not yet active")
    - If current_date > valid_until: return failure("Promo code expired")
    - If current_usage >= max_usage: return failure("Promo code usage limit reached")
    - If booking amount < minimum_amount: return failure("Minimum amount not met")
  - Calculate discount:
    - If discount_type = PERCENTAGE: discount = amount × (discount_value / 100)
    - If discount_type = FIXED: discount = discount_value
    - Apply max_discount cap if exists
  - Update request with discount amount
  - Return success()

**FinalBookingHandler**:
- Purpose: Create confirmed booking (end of chain)
- validate() logic:
  - All validations passed
  - Call BookingService.createConfirmedBooking()
  - Return success with booking object

**Chain Setup**:
- ValidationService.validateAndCreateBooking(request):
  ```
  Create handlers:
  - SeatAvailabilityHandler seatHandler
  - PaymentValidationHandler paymentHandler
  - UserEligibilityHandler userHandler
  - PromoCodeHandler promoHandler
  - FinalBookingHandler finalHandler
  
  Chain them:
  seatHandler.setNext(paymentHandler)
              .setNext(userHandler)
              .setNext(promoHandler)
              .setNext(finalHandler)
  
  Start chain:
  return seatHandler.handle(request)
  ```

**Error Handling**:
- If any handler returns failure, chain stops immediately
- Controller returns failure message to client
- Client shows specific error message

**Design Principles Applied**:
- **Low Coupling (GRASP)**: Each handler independent, doesn't know about others
- **Controller (GRASP)**: ValidationService coordinates the chain

---

### **D. ANALYTICS & REPORTING SYSTEM**

#### **API Endpoints to Implement**:

**1. GET /api/admin/analytics/revenue**
- **Purpose**: Get revenue report
- **Query Parameters**:
  - startDate, endDate
  - groupBy (day, week, month)
  - theaterId (optional filter)
  - movieId (optional filter)
- **Response**:
  - Total revenue
  - Ticket revenue
  - F&B revenue
  - Revenue breakdown by period
  - Top revenue-generating movies/theaters
- **Logic**:
  - Query bookings table with date range filter
  - SUM(total_amount) for total revenue
  - SUM(ticket_amount) for ticket revenue
  - SUM(food_amount) for F&B revenue
  - GROUP BY based on groupBy parameter
  - Include theater/movie details with JOINs

**2. GET /api/admin/analytics/occupancy**
- **Purpose**: Get seat occupancy report
- **Query Parameters**:
  - startDate, endDate
  - theaterId, screenId (optional filters)
- **Response**:
  - Overall occupancy percentage
  - Occupancy by theater/screen
  - Occupancy by day of week
  - Occupancy by time slot (morning, afternoon, evening, night)
  - Peak hours analysis
- **Logic**:
  - Query showtimes with booking data
  - Calculate: (booked_seats / total_seats) × 100 for each showtime
  - Aggregate by requested dimensions
  - Identify patterns (weekends vs weekdays, time slots)

**3. GET /api/admin/analytics/movies**
- **Purpose**: Get movie performance report
- **Query Parameters**:
  - startDate, endDate
- **Response**:
  - Top-grossing movies
  - Total bookings per movie
  - Average rating per movie
  - Average occupancy per movie
  - Showtimes count per movie
- **Logic**:
  - Query bookings joined with movies
  - Aggregate by movie_id
  - Calculate metrics

**4. GET /api/admin/analytics/dashboard**
- **Purpose**: Get summary dashboard metrics
- **Response**:
  - Today's metrics:
    - Total bookings
    - Total revenue
    - Occupancy rate
  - This week/month comparisons
  - Trending movies
  - Low-performing showtimes
  - Upcoming showtimes needing attention
- **Logic**: Aggregate queries for various metrics

---

#### **Frontend Implementation**:

**Page: Admin Dashboard (admin-dashboard.html)**

**Layout**:
- **Sidebar Navigation**:
  - Dashboard (home)
  - Movies (add/edit)
  - Theaters (manage)
  - Showtimes (schedule)
  - Pricing (strategies)
  - Analytics (reports)
  - Promo Codes
  - User Management

- **Main Content Area**:
  Varies based on selected section

**Dashboard Section**:
- **Key Metrics Cards** (top row):
  - Today's Revenue (large number, % change from yesterday)
  - Today's Bookings (count, % change)
  - Current Occupancy (percentage)
  - Active Showtimes (count)
- **Charts**:
  - Revenue Trend: Line chart (last 30 days)
  - Occupancy by Theater: Bar chart
  - Top Movies: Horizontal bar chart (by revenue)
  - Booking Distribution: Pie chart (by time slot)
- **Tables**:
  - Recent Bookings: List with details
  - Upcoming Showtimes: Today's schedule

**Movies Section**:
- **Movie List Table**:
  - Columns: Poster, Title, Genre, Language, Status, Actions
  - Actions: Edit, View Details, Delete
  - "Add Movie" button (top right)
- **Add/Edit Movie Form** (modal or separate page):
  - All movie fields
  - File upload for poster image
  - Multi-select for genres
  - Dynamic cast/crew fields (add/remove rows)
  - Submit button

**Theaters Section**:
- **Theater List**:
  - Cards showing theater name, location, type, screens count
  - Actions: View Screens, Edit, Delete
  - "Add Theater" button
- **Add Theater Form**:
  - Theater details form
  - Theater type selection (triggers Abstract Factory)
  - After creation: Automatically shows screen management

**Screens Section**:
- **Screen List** (within theater):
  - Table: Screen Number, Name, Total Seats, Status
  - Actions: View Seats, Edit, Delete
  - "Add Screen" button
- **Add Screen Form**:
  - Screen details
  - Seat layout preview (visual grid)

**Showtimes Section**:
- **Calendar View**:
  - Week/Month view toggle
  - Color-coded by occupancy (green: >70%, yellow: 40-70%, red: <40%)
  - Click on date to add showtime
- **Showtime List View** (alternative):
  - Table: Movie, Theater, Screen, Date, Time, Occupancy, Actions
  - Filters: Date range, movie, theater
  - "Schedule Showtime" button
- **Schedule Showtime Form**:
  - Movie dropdown (searchable)
  - Theater dropdown
  - Screen dropdown (filtered by selected theater)
  - Date and time pickers
  - Base price input
  - Pricing strategy dropdown (STANDARD, MATINEE, WEEKEND, HOLIDAY)
  - Conflict check (shows error if overlap detected)
  - Submit button

**Pricing Strategies Section**:
- **Strategy List**:
  - Table: Strategy Name, Type, Multiplier, Conditions, Active
  - Actions: Edit, Enable/Disable
  - "Create Strategy" button
- **Strategy Form**:
  - Strategy type dropdown (Matinee, Weekend, Holiday, Seat Zone, Demand-Based)
  - Configuration fields (vary by type)
  - Conditions (time ranges, days, dates)
  - Multiplier value
  - Active toggle

**Analytics Section**:
- **Report Type Tabs**:
  - Revenue
  - Occupancy
  - Movie Performance
- **Filters Panel**:
  - Date range picker
  - Theater/Movie filters
  - Group by dropdown (Day, Week, Month)
  - "Generate Report" button
- **Charts Area**:
  - Dynamic charts based on selected report
  - Use Chart.js library
  - Interactive tooltips
- **Export Options**:
  - Download as PDF
  - Download as Excel
  - Schedule automated email

**JavaScript Requirements**:
1. Dashboard: Fetch summary metrics, render charts
2. Movies: CRUD operations with form validation
3. Theaters: Abstract Factory demonstration (different configurations based on type)
4. Showtimes: Calendar interaction, conflict detection
5. Pricing: Strategy preview (show example calculation)
6. Analytics: Dynamic chart rendering, export functionality

**Chart.js Implementation**:
- Line charts for trends
- Bar charts for comparisons
- Pie charts for distributions
- Responsive design (adapt to screen size)
- Interactive legends (click to show/hide data series)

---

### **E. TESTING STRATEGY FOR RUSHAD'S MODULE**

**Unit Tests**:
1. Test Strategy Pattern:
   - MatineePricingStrategy: Morning show returns 0.8× price
   - WeekendPricingStrategy: Saturday returns 1.25× price
   - SeatZonePricingStrategy: Front row returns 0.9× price
   - CombinedStrategy: Correctly applies multiple strategies
2. Test Chain of Responsibility:
   - SeatAvailabilityHandler: Detects already booked seats
   - PaymentValidationHandler: Rejects invalid card details
   - UserEligibilityHandler: Blocks underage users for A-rated movies
   - PromoCodeHandler: Correctly applies discount
   - Test early termination when handler fails
3. Test showtime conflict detection:
   - Overlapping showtimes return error
   - Non-overlapping showtimes succeed

**Integration Tests**:
1. Theater creation with Abstract Factory:
   - Create IMAX theater → Verify 300 seats, premium pricing
   - Create 4DX theater → Verify motion seats
2. Showtime scheduling:
   - Schedule showtime → Verify no conflicts → Create successfully
   - Attempt conflicting showtime → Verify error
3. Validation chain:
   - Valid booking request → Passes all handlers → Booking created
   - Invalid request → Stops at appropriate handler → Error returned
4. Dynamic pricing:
   - Calculate price for weekend + evening → Verify markup applied
   - Calculate price for matinee + front row → Verify discount applied

**Manual Testing Checklist**:
- [ ] Add theater via admin panel, verify correct seat layout generated
- [ ] Schedule showtime, verify conflict detection works
- [ ] Set pricing strategy, verify correct prices calculated
- [ ] Generate revenue report, verify calculations correct
- [ ] Test validation chain with invalid data at each step
- [ ] Verify occupancy dashboard shows correct percentages

---

## **INTEGRATION BETWEEN MODULES**

### **Key Integration Points**:

1. **Shrish ↔ Vaishnav**:
   - Shrish creates PENDING/LOCKED booking
   - Vaishnav adds F&B items using Decorator Pattern
   - Vaishnav processes payment and triggers Shrish's State Pattern to confirm booking

2. **Shrish ↔ Saffiya**:
   - Saffiya's movie catalog provides movies and showtimes
   - Shrish's seat selection loads seat map for selected showtime

3. **Shrish ↔ Rushad**:
   - Rushad schedules showtimes
   - Shrish displays seat map for those showtimes
   - Rushad's pricing strategies calculate seat prices Shrish displays

4. **Vaishnav ↔ Rushad**:
   - Rushad's validation chain includes payment validation
   - Vaishnav uses validation before processing payment

5. **All Modules ↔ Database**:
   - All modules interact with shared database
   - Use transactions to maintain consistency
   - Foreign key constraints prevent orphaned records

### **API Gateway / Controller Structure**:
- Each module has dedicated controllers
- Controllers are thin layers delegating to services
- Services contain business logic and pattern implementations
- Repositories handle database operations

### **Shared Services**:
- **EmailService**: Used by multiple modules for notifications
- **QRCodeService**: Used for ticket generation (Vaishnav's module)
- **ValidationService**: Coordinates Chain of Responsibility (Rushad's module)
- **WebSocketService**: Manages real-time updates (Shrish's module)

---

## **DEPLOYMENT & CONFIGURATION**

### **Local Development Setup**:
1. Install PostgreSQL 15+
2. Create database: `moviebooking_db`
3. Run SQL schema scripts (create tables in correct order)
4. Insert sample data (movies, theaters, users)
5. Configure application.properties with database credentials
6. Run Spring Boot application: `mvn spring-boot:run`
7. Access at: http://localhost:8080

### **Environment Variables** (for production):
- DB_HOST, DB_PORT, DB_NAME
- DB_USERNAME, DB_PASSWORD
- JWT_SECRET
- EMAIL_HOST, EMAIL_USERNAME, EMAIL_PASSWORD
- FILE_UPLOAD_PATH (for posters)

### **Database Connection Pooling** (HikariCP):
- Maximum pool size: 20 connections
- Minimum idle: 5 connections
- Connection timeout: 30 seconds
- Idle timeout: 10 minutes
- Max lifetime: 30 minutes

### **Logging Configuration**:
- Log level: INFO for production, DEBUG for development
- Log file rotation: Daily, keep 30 days
- Log patterns: Include timestamp, log level, class name, message
- Separate logs for:
  - Application logs
  - Payment transactions
  - WebSocket events
  - Error logs

---

## **SECURITY CONSIDERATIONS**

### **Authentication**:
- Use Spring Security with JWT tokens
- Login endpoint: POST /api/auth/login (returns JWT)
- Protected endpoints require Authorization header: Bearer {token}
- Token expiration: 24 hours
- Refresh token mechanism

### **Authorization**:
- Role-based access control (CUSTOMER, MANAGER, ADMIN)
- Customer: Can only access their own bookings
- Manager: Can access admin panel, manage theaters/movies
- Admin: Full access

### **Password Security**:
- Use BCrypt hashing (strength: 10)
- Never store plain text passwords
- Enforce password complexity: Min 8 chars, 1 uppercase, 1 number

### **SQL Injection Prevention**:
- Use parameterized queries (JPA automatically handles)
- Never concatenate user input into SQL strings

### **XSS Prevention**:
- Sanitize all user inputs
- Use Thymeleaf's escaping by default
- Set Content-Security-Policy headers

### **CORS Configuration**:
- Allow only trusted origins
- For local development: Allow localhost:3000, localhost:8080

### **Rate Limiting**:
- Prevent abuse of APIs
- Limit: 100 requests per minute per IP
- Payment endpoints: 10 requests per minute

### **Payment Security**:
- Never store full card numbers (if handling real payments)
- Use PCI-DSS compliant gateways
- Encrypt sensitive data in transit (HTTPS)
- Log all payment transactions

---

## **PERFORMANCE OPTIMIZATION**

### **Database Optimization**:
- **Indexes**: Ensure all foreign keys indexed
- **Query Optimization**: 
  - Use JOIN instead of N+1 queries
  - Use pagination for large result sets
  - Use database views for complex aggregations
- **Connection Pooling**: HikariCP configured correctly
- **Caching**:
  - Cache movie catalog (refresh every hour)
  - Cache theater configurations
  - Cache user sessions

### **Frontend Optimization**:
- **Lazy Loading**: Load images as user scrolls
- **Minification**: Minify CSS and JavaScript
- **CDN**: Serve static assets (Bootstrap, jQuery) from CDN
- **Compression**: Enable gzip compression for text resources

### **WebSocket Optimization**:
- Use message compression
- Batch multiple seat updates into single message
- Limit broadcast frequency (max 1 message per second per topic)

### **Caching Strategy**:
- Use application-level caching (e.g., Caffeine cache)
- Cache frequently accessed data:
  - Movie list
  - Theater list
  - User preferences
- Invalidate cache on data updates

---

## **ERROR HANDLING & LOGGING**

### **Global Exception Handling**:
- Use @ControllerAdvice to handle exceptions globally
- Return consistent error response format:
  ```json
  {
    "error": "Error message",
    "status": 400,
    "timestamp": "2026-02-09T10:30:00Z",
    "path": "/api/bookings/create"
  }
  ```

### **Custom Exceptions**:
- BookingNotFoundException
- SeatNotAvailableException
- PaymentFailedException
- InvalidPromoCodeException
- ShowtimeConflictException

### **Logging Best Practices**:
- Log all state transitions (State Pattern)
- Log all payment transactions
- Log WebSocket connections/disconnections
- Log validation failures
- Use structured logging (JSON format)
- Include correlation IDs for request tracing

---

## **TESTING OVERVIEW**

### **Test Pyramid**:
1. **Unit Tests** (70%):
   - Test individual classes in isolation
   - Mock dependencies
   - Fast execution
   - Examples: Test each State, Strategy, Handler independently

2. **Integration Tests** (20%):
   - Test interaction between components
   - Use in-memory database (H2)
   - Test API endpoints
   - Examples: Test booking flow end-to-end

3. **End-to-End Tests** (10%):
   - Test complete user workflows
   - Use Selenium or similar
   - Test in staging environment
   - Examples: User books ticket from search to payment

### **Test Coverage Goal**:
- Minimum 70% code coverage
- 100% coverage for critical paths (payment, booking confirmation)

---

## **DOCUMENTATION REQUIREMENTS**

### **Code Documentation**:
- Javadoc comments for all public methods
- Explain design pattern implementations clearly
- Include usage examples in comments

### **API Documentation**:
- Use Swagger/OpenAPI annotations
- Document all endpoints, parameters, responses
- Include example requests and responses

### **README.md**:
- Project description
- Setup instructions
- How to run locally
- Environment variables needed
- Team member contributions
- Design patterns used and where

---

**END OF FILE 4: IMPLEMENTATION GUIDE PER MODULE**

---