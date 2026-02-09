# **FILE 3: UML DIAGRAM SPECIFICATIONS**

---

## **Overview**

This document provides detailed specifications for all 10 UML diagrams required for the project. Use these specifications to create diagrams in tools like Lucidchart, Draw.io, Visual Paradigm, or PlantUML.

**Required Diagrams**:
1. **1 Use Case Diagram** - System-wide view
2. **1 Class Diagram** - Complete system with all patterns
3. **4 Activity Diagrams** - One per team member
4. **4 State Diagrams** - One per team member

---

## **DIAGRAM 1: USE CASE DIAGRAM (SYSTEM-WIDE)**

### **Purpose**:
Show all system functionalities, actors, and their relationships in a single comprehensive diagram.

### **Actors**:

**Primary Actors**:
1. **Customer** (stick figure on left side)
   - Description: End user who books tickets
   
2. **Theater Manager** (stick figure on left side)
   - Description: Staff who manages theater operations
   - Inherits from: Admin (generalization relationship)
   
3. **Admin** (stick figure on left side)
   - Description: System administrator with full access
   - Parent of: Theater Manager

**Secondary Actors**:
4. **Payment Gateway** (stick figure on right side)
   - Description: External payment processing system
   
5. **Email Service** (stick figure on right side)
   - Description: External email notification service

### **System Boundary**:
- Draw a large rectangle labeled "Movie Ticket Booking System"
- All use cases should be inside this boundary
- Actors should be outside the boundary

### **Use Cases** (ovals inside system boundary):

**Customer Use Cases**:
1. Register Account
2. Login
3. Browse Movies
4. Search Movies
5. View Movie Details
6. View Showtimes
7. Select Seats
8. Add Food & Beverages
9. Apply Promo Code
10. Make Payment
11. View Booking History
12. Download Ticket
13. Cancel Booking
14. Write Review
15. Receive Notifications

**Theater Manager Use Cases**:
16. Add Movie
17. Edit Movie
18. Add Theater
19. Configure Screen
20. Schedule Showtime
21. Set Dynamic Pricing
22. View Analytics
23. Generate Reports
24. Manage Food Menu

**Admin Use Cases**:
25. Manage Users
26. Manage Promo Codes
27. System Configuration

### **Relationships**:

**Include Relationships** (dashed arrow with <<include>>):
- "Select Seats" includes "View Showtimes"
- "Make Payment" includes "Select Seats"
- "Make Payment" includes "Add Food & Beverages" (optional)
- "Make Payment" includes "Apply Promo Code" (optional)
- "Cancel Booking" includes "View Booking History"
- "Download Ticket" includes "View Booking History"

**Extend Relationships** (dashed arrow with <<extend>>):
- "Add Food & Beverages" extends "Select Seats"
- "Apply Promo Code" extends "Make Payment"
- "Write Review" extends "View Booking History"

**Generalization Relationships** (solid line with hollow triangle):
- Theater Manager → Admin (Theater Manager is a specialized Admin)

**Actor-Use Case Associations** (solid lines):
- Customer connects to: Register, Login, Browse Movies, Search Movies, View Movie Details, View Showtimes, Select Seats, Add F&B, Apply Promo Code, Make Payment, View Booking History, Download Ticket, Cancel Booking, Write Review, Receive Notifications
- Theater Manager connects to: All Customer use cases + Add Movie, Edit Movie, Add Theater, Configure Screen, Schedule Showtime, Set Dynamic Pricing, View Analytics, Generate Reports, Manage Food Menu
- Admin connects to: All use cases (inherits Manager's access + Manage Users, Manage Promo Codes, System Configuration)
- Payment Gateway connects to: Make Payment, Cancel Booking (for refunds)
- Email Service connects to: Receive Notifications

### **Layout Suggestions**:
- Place Actors on left and right sides
- Group related use cases together (Customer area, Manager area, Admin area)
- Use ellipses with clear, concise labels
- Show system boundary clearly
- Use different colors for different actor groups (optional but recommended)

### **Key Points to Highlight**:
- Customer's primary workflow: Browse → Select Seats → Payment
- Manager's configuration workflow: Add Movie → Schedule Showtime → Set Pricing
- Include/Extend relationships show dependencies
- Generalization shows role hierarchy

---

## **DIAGRAM 2: CLASS DIAGRAM (COMPLETE SYSTEM)**

### **Purpose**:
Show all major classes, their attributes, methods, relationships, and design pattern implementations.

### **Classes to Include**:

**Core Entity Classes** (database-backed):
1. User
2. Movie
3. Theater
4. Screen
5. Seat
6. Showtime
7. Booking
8. Payment
9. FoodItem
10. PromoCode
11. SeatLock

**Service/Controller Classes** (business logic):
12. MovieService
13. BookingService
14. SeatService
15. PaymentService
16. TheaterService
17. PricingService
18. ValidationService

**Design Pattern Classes** (show patterns visually):

**State Pattern (Shrish)**:
19. BookingState (interface)
20. PendingState
21. LockedState
22. ConfirmedState
23. CancelledState
24. RefundedState
25. BookingContext

**Observer Pattern (Shrish)**:
26. SeatAvailabilitySubject
27. SeatObserver (interface)
28. WebSocketClient

**Decorator Pattern (Vaishnav)**:
29. Booking (interface)
30. BaseBooking
31. BookingDecorator (abstract)
32. SnackDecorator
33. BeverageDecorator
34. ComboDecorator

**Facade Pattern (Vaishnav)**:
35. PaymentFacade
36. CardPaymentGateway
37. UPIPaymentGateway
38. NetBankingGateway
39. WalletGateway

**Abstract Factory Pattern (Saffiya)**:
40. TheaterFactory (interface)
41. RegularTheaterFactory
42. IMAXTheaterFactory
43. FourDXTheaterFactory
44. SeatConfiguration (interface)
45. PricingModel (interface)
46. TheaterEquipment (interface)

**Singleton Pattern (Saffiya)**:
47. DatabaseConnectionManager

**Chain of Responsibility (Rushad)**:
48. BookingValidationHandler (abstract)
49. SeatAvailabilityHandler
50. PaymentValidationHandler
51. UserEligibilityHandler
52. PromoCodeHandler
53. FinalBookingHandler

**Strategy Pattern (Rushad)**:
54. PricingStrategy (interface)
55. MatineePricingStrategy
56. WeekendPricingStrategy
57. HolidayPricingStrategy
58. SeatZonePricingStrategy

### **Class Details**:

**For Each Entity Class, Show**:
- Class name at top
- Visibility indicators: + (public), - (private), # (protected)
- Key attributes with types
- Key methods with return types

**Example - User Class**:
```
┌─────────────────────────┐
│        User             │
├─────────────────────────┤
│ - id: Long              │
│ - email: String         │
│ - passwordHash: String  │
│ - firstName: String     │
│ - lastName: String      │
│ - phone: String         │
│ - dateOfBirth: Date     │
│ - role: UserRole        │
├─────────────────────────┤
│ + register()            │
│ + login()               │
│ + updateProfile()       │
│ + getAge(): int         │
└─────────────────────────┘
```

**Example - Booking Class**:
```
┌─────────────────────────┐
│       Booking           │
├─────────────────────────┤
│ - id: Long              │
│ - bookingReference: Str │
│ - userId: Long          │
│ - showtimeId: Long      │
│ - status: BookingStatus │
│ - totalAmount: BigDec   │
│ - qrCodeUrl: String     │
├─────────────────────────┤
│ + createBooking()       │
│ + confirmBooking()      │
│ + cancelBooking()       │
│ + calculateTotal()      │
│ + generateQRCode()      │
└─────────────────────────┘
```

### **Relationships to Show**:

**Association** (solid line with arrow):
- User → Booking (1 user has many bookings) - "1" on User side, "*" on Booking side
- Movie → Showtime (1 movie has many showtimes) - "1" to "*"
- Theater → Screen (1 theater has many screens) - "1" to "*"
- Screen → Seat (1 screen has many seats) - "1" to "*"
- Showtime → Booking (1 showtime has many bookings) - "1" to "*"
- Booking → Payment (1 booking has 1 payment) - "1" to "1"

**Aggregation** (hollow diamond):
- Theater ◇─ Screen (Theater contains Screens, but Screens can exist independently)
- Booking ◇─ Seat (Booking aggregates Seats through junction table)

**Composition** (filled diamond):
- Booking ◆─ BookingSeat (BookingSeat cannot exist without Booking)
- Booking ◆─ BookingFood (BookingFood cannot exist without Booking)

**Inheritance/Generalization** (solid line with hollow triangle):
- PendingState △─ BookingState (PendingState implements BookingState)
- LockedState △─ BookingState
- ConfirmedState △─ BookingState
- CancelledState △─ BookingState
- RefundedState △─ BookingState

**Realization/Implementation** (dashed line with hollow triangle):
- RegularTheaterFactory ┆△─ TheaterFactory (implements interface)
- IMAXTheaterFactory ┆△─ TheaterFactory
- FourDXTheaterFactory ┆△─ TheaterFactory

**Dependency** (dashed arrow):
- BookingService ┆→ PaymentFacade (uses)
- PaymentFacade ┆→ CardPaymentGateway (depends on)
- ValidationService ┆→ BookingValidationHandler (uses)

### **Stereotypes to Include**:
- <<interface>> for interfaces (BookingState, TheaterFactory, PricingStrategy)
- <<abstract>> for abstract classes (BookingDecorator, BookingValidationHandler)
- <<singleton>> for Singleton classes (DatabaseConnectionManager)
- <<entity>> for JPA entities (User, Movie, Booking, etc.)
- <<service>> for service classes
- <<controller>> for controller classes

### **Pattern-Specific Groupings**:

**Group 1: State Pattern** (draw a box around these):
- BookingState (interface at top)
- All concrete states below (PendingState, LockedState, etc.)
- BookingContext (uses BookingState)

**Group 2: Observer Pattern** (draw a box):
- SeatAvailabilitySubject
- SeatObserver (interface)
- WebSocketClient (implements SeatObserver)

**Group 3: Decorator Pattern** (draw a box):
- Booking (interface at top)
- BaseBooking (implements Booking)
- BookingDecorator (abstract, implements Booking, wraps Booking)
- Concrete decorators (SnackDecorator, BeverageDecorator, ComboDecorator)

**Group 4: Facade Pattern** (draw a box):
- PaymentFacade (central)
- Multiple gateway classes behind it (CardPaymentGateway, UPIPaymentGateway, etc.)

**Group 5: Abstract Factory Pattern** (draw a box):
- TheaterFactory (interface at top)
- Three concrete factories (RegularTheaterFactory, IMAXTheaterFactory, FourDXTheaterFactory)
- Product interfaces (SeatConfiguration, PricingModel, TheaterEquipment)

**Group 6: Chain of Responsibility** (draw a box):
- BookingValidationHandler (abstract at top)
- Handler chain showing nextHandler relationship
- Concrete handlers in sequence

**Group 7: Strategy Pattern** (draw a box):
- PricingStrategy (interface at top)
- Concrete strategies (MatineePricingStrategy, WeekendPricingStrategy, etc.)
- PricingContext (uses PricingStrategy)

### **Layout Suggestions**:
- **Top Layer**: Controllers (entry points)
- **Middle Layer**: Services (business logic) + Design Pattern implementations
- **Bottom Layer**: Entities (data models)
- Group related classes together
- Show pattern implementations in separate sections/boxes
- Use colors to distinguish layers (Controllers = blue, Services = green, Entities = yellow)

### **Key Annotations**:
- Add notes explaining complex relationships
- Label multiplicities clearly (1, *, 0..1, 1..*)
- Add role names on associations where helpful
- Mark navigability with arrows

### **Critical Elements to Highlight**:
- Show how BookingService uses State Pattern via BookingContext
- Show how PaymentService uses Facade Pattern via PaymentFacade
- Show how TheaterService uses Abstract Factory to create theaters
- Show how BookingService uses Chain of Responsibility for validation
- Show how PricingService uses Strategy Pattern for dynamic pricing
- Show how SeatService uses Observer Pattern for real-time updates
- Show how BookingService uses Decorator Pattern for F&B items

---

## **DIAGRAM 3: ACTIVITY DIAGRAM - SHRISH (SEAT SELECTION & BOOKING)**

### **Purpose**:
Show the detailed flow of seat selection with real-time updates and booking creation.

### **Swimlanes** (vertical columns):
1. **Customer** (leftmost)
2. **Seat Selection System**
3. **WebSocket Server**
4. **Database**
5. **Other Connected Users** (rightmost)

### **Start and End**:
- **Start**: Filled circle at top of Customer lane
- **End**: Filled circle with outer ring at bottom

### **Activities/Actions** (rounded rectangles):

**Customer Lane**:
1. Navigate to seat selection page
2. View seat map
3. Click on available seats
4. Review selected seats and price
5. Click "Proceed to Payment"

**Seat Selection System Lane**:
1. Load showtime details
2. Fetch current seat availability
3. Display seat map with colors
4. Validate seat selection
5. Lock selected seats (10 min)
6. Calculate total price
7. Create booking record (PENDING status)
8. Redirect to payment page

**WebSocket Server Lane**:
1. Receive seat lock event
2. Broadcast seat status update
3. Send message to all connected clients

**Database Lane**:
1. Query seats for showtime
2. Check seat locks table
3. Insert seat locks
4. Update seat status
5. Insert booking record

**Other Connected Users Lane**:
1. Receive WebSocket message
2. Update seat map UI
3. Show seats as locked (yellow)

### **Decision Nodes** (diamonds):
1. **Is seat available?** (in Seat Selection System lane after Validate)
   - YES → Continue to Lock seats
   - NO → Show error message → Return to View seat map
   
2. **Are seats valid?** (check if seats not already booked/locked by others)
   - YES → Lock seats
   - NO → Show error → Return to View seat map

3. **Is user selecting <10 seats?**
   - YES → Continue
   - NO → Show error "Maximum 10 seats allowed"

### **Parallel Activities** (fork/join bars - thick horizontal lines):
- **Fork** after "Lock selected seats":
  - Branch 1: Insert seat locks in Database
  - Branch 2: Broadcast to WebSocket
  - Branch 3: Calculate total price
- **Join** before "Create booking record"

### **Object Nodes** (rectangles):
- Showtime object
- Seat list object
- SeatLock list object
- Booking object (PENDING state)

### **Signals**:
- **Send Signal** (pentagon pointing right): "Send WebSocket message"
- **Receive Signal** (pentagon pointing left): "Receive seat update" (in Other Users lane)

### **Notes/Comments** (dog-eared rectangle):
- Add note near "Lock selected seats": "Observer Pattern - broadcast to all observers"
- Add note near "Create booking record": "State Pattern - booking starts in PENDING state"
- Add note on seat lock: "Locks expire after 10 minutes"

### **Flow**:
1. Start in Customer lane
2. Flow moves to Seat Selection System for processing
3. Parallel branches to WebSocket and Database
4. WebSocket broadcasts to Other Users
5. Flow returns to Customer with redirect to payment
6. End

### **Color Coding**:
- Normal activities: Light blue
- Decision nodes: Yellow
- Database operations: Light green
- WebSocket operations: Orange
- Error paths: Red

### **Key Points to Highlight**:
- Show concurrent operations (database + WebSocket broadcast)
- Highlight Observer Pattern in action (broadcast mechanism)
- Show decision points clearly
- Emphasize real-time nature of updates

---

## **DIAGRAM 4: ACTIVITY DIAGRAM - VAISHNAV (PAYMENT PROCESSING & F&B)**

### **Purpose**:
Show the complete payment flow with food ordering and payment gateway integration.

### **Swimlanes**:
1. **Customer**
2. **F&B Module**
3. **Payment Module**
4. **Payment Facade**
5. **Payment Gateway** (external)
6. **Database**

### **Activities**:

**Customer Lane**:
1. View F&B menu
2. Add items to cart
3. Review order summary
4. Select payment method
5. Enter payment details
6. Confirm payment
7. View booking confirmation

**F&B Module Lane**:
1. Display menu by category
2. Calculate F&B subtotal
3. Apply Decorator pattern
4. Add items to booking

**Payment Module Lane**:
1. Calculate ticket cost
2. Calculate F&B cost
3. Calculate taxes (18% GST)
4. Apply promo code discount (if any)
5. Calculate final amount
6. Show order summary
7. Initiate payment
8. Wait for payment response
9. Generate QR code
10. Send confirmation email

**Payment Facade Lane**:
1. Receive payment request
2. Route to appropriate gateway
3. Process payment
4. Return transaction result

**Payment Gateway Lane**:
1. Validate payment details
2. Process transaction
3. Return success/failure

**Database Lane**:
1. Insert booking record
2. Insert booking_food records
3. Insert booking_seats records
4. Insert payment record
5. Update seat status to BOOKED
6. Update booking status to CONFIRMED
7. Update showtime available_seats

### **Decision Nodes**:
1. **Payment method selected?**
   - Card → Route to CardPaymentGateway
   - UPI → Route to UPIPaymentGateway
   - Net Banking → Route to NetBankingGateway
   - Wallet → Route to WalletGateway

2. **Payment successful?**
   - YES → Update booking to CONFIRMED → Generate QR code
   - NO → Show error → Keep booking in PENDING → Allow retry

3. **Promo code applied?**
   - YES → Validate promo code → Apply discount
   - NO → Skip discount

4. **F&B items added?**
   - YES → Apply Decorator pattern → Add to booking
   - NO → Skip F&B

### **Parallel Activities** (fork/join):
- **Fork** after "Payment successful":
  - Branch 1: Update database records
  - Branch 2: Generate QR code
  - Branch 3: Send confirmation email
  - Branch 4: Release seat locks
- **Join** before "View booking confirmation"

### **Object Nodes**:
- FoodItem list
- Booking object (with Decorators)
- Payment object
- Transaction result
- QR code image

### **Notes**:
- Add note near payment routing: "Facade Pattern - unified interface for multiple gateways"
- Add note near F&B: "Decorator Pattern - dynamically add items to booking"
- Add note on transaction: "Transaction ensures atomicity - all updates succeed or all fail"

### **Exception Handling** (dashed flow):
- From "Process transaction" if exception → "Payment failed" → "Show error message" → Return to "Select payment method"

### **Time Events**:
- Add time constraint: "Wait for payment response - timeout after 2 minutes"
- If timeout → Treat as payment failed

### **Key Points to Highlight**:
- Show Facade Pattern routing to different gateways
- Show Decorator Pattern adding F&B items
- Highlight transaction boundaries (database operations grouped)
- Show parallel operations after payment success

---

## **DIAGRAM 5: ACTIVITY DIAGRAM - SAFFIYA (MOVIE BROWSING & RECOMMENDATIONS)**

### **Purpose**:
Show movie browsing, search, filtering, and personalized recommendation flow.

### **Swimlanes**:
1. **Customer**
2. **Movie Browsing Module**
3. **Search Module**
4. **Recommendation Engine**
5. **Database**

### **Activities**:

**Customer Lane**:
1. Open homepage
2. Browse featured movies
3. Apply filters (genre, language, rating)
4. Enter search query
5. Click on movie
6. View movie details
7. View recommendations
8. Select showtime
9. Proceed to seat selection

**Movie Browsing Module Lane**:
1. Fetch movies from database
2. Categorize movies (Now Showing, Coming Soon, Trending)
3. Apply filters
4. Sort results
5. Display movie grid
6. Fetch movie details
7. Fetch showtimes
8. Fetch reviews

**Search Module Lane**:
1. Receive search query
2. Parse query
3. Search in movie titles
4. Search in actor names
5. Search in director names
6. Merge and rank results
7. Return search results

**Recommendation Engine Lane**:
1. Fetch user's booking history
2. Identify preferred genres
3. Identify preferred languages
4. Find similar movies (content-based)
5. Find movies liked by similar users (collaborative filtering)
6. Apply popularity ranking
7. Return top 10 recommendations

**Database Lane**:
1. Query movies table
2. Query showtimes table
3. Query user_preferences table
4. Query bookings table (for history)
5. Query reviews table

### **Decision Nodes**:
1. **Filters applied?**
   - YES → Apply genre, language, rating filters
   - NO → Show all movies

2. **Search query entered?**
   - YES → Execute search
   - NO → Show browse results

3. **User logged in?**
   - YES → Generate personalized recommendations
   - NO → Show trending/popular movies

4. **Search results found?**
   - YES → Display results
   - NO → Show "No results found" + suggestions

### **Parallel Activities**:
- **Fork** when "Fetch movie details":
  - Branch 1: Fetch movie metadata
  - Branch 2: Fetch showtimes
  - Branch 3: Fetch reviews
  - Branch 4: Fetch recommendations
- **Join** before "Display movie details page"

### **Object Nodes**:
- Movie list
- Filter criteria object
- Search query object
- Recommendation list
- Theater configuration (from Abstract Factory)

### **Notes**:
- Add note on recommendations: "Content-based + Collaborative filtering hybrid algorithm"
- Add note on theater types: "Abstract Factory creates appropriate theater configuration"
- Add note on search: "Full-text search with fuzzy matching"

### **Expansion Regions** (dashed boxes):
- Around search activities: "Concurrent search across multiple fields"
- Around recommendation generation: "Parallel recommendation algorithms"

### **Key Points to Highlight**:
- Show multiple data sources being queried simultaneously
- Highlight recommendation algorithm components
- Show Abstract Factory pattern creating theater configurations
- Emphasize personalization based on user history

---

## **DIAGRAM 6: ACTIVITY DIAGRAM - RUSHAD (THEATER MANAGEMENT & DYNAMIC PRICING)**

### **Purpose**:
Show theater configuration, showtime scheduling, and dynamic pricing setup.

### **Swimlanes**:
1. **Theater Manager**
2. **Theater Management Module**
3. **Showtime Scheduling Module**
4. **Pricing Engine**
5. **Validation Pipeline**
6. **Database**

### **Activities**:

**Theater Manager Lane**:
1. Login to admin dashboard
2. Navigate to theater management
3. Add new theater
4. Select theater type (Regular/IMAX/4DX)
5. Configure screens
6. Add movie
7. Schedule showtime
8. Select pricing strategy
9. View analytics reports

**Theater Management Module Lane**:
1. Display admin dashboard
2. Show theater list
3. Create theater form
4. Use Abstract Factory to create configuration
5. Generate seat layout
6. Save theater configuration

**Showtime Scheduling Module Lane**:
1. Display movie list
2. Display available screens
3. Check for scheduling conflicts
4. Create showtime record
5. Make showtime available for booking

**Pricing Engine Lane**:
1. Get base price
2. Identify applicable strategies
3. Apply time-based pricing
4. Apply day-based pricing
5. Apply seat-zone pricing
6. Calculate final price per seat type

**Validation Pipeline Lane**:
1. Start Chain of Responsibility
2. Validate theater configuration
3. Validate screen capacity
4. Validate showtime conflicts
5. Validate pricing rules
6. Return validation result

**Database Lane**:
1. Insert theater record
2. Insert screen records
3. Generate and insert seat records
4. Insert showtime record
5. Insert pricing rules

### **Decision Nodes**:
1. **Theater type selected?**
   - Regular → Use RegularTheaterFactory
   - IMAX → Use IMAXTheaterFactory
   - 4DX → Use FourDXTheaterFactory

2. **Validation passed?** (after each validation step in pipeline)
   - YES → Continue to next handler
   - NO → Show error → Return to input form

3. **Scheduling conflict detected?**
   - YES → Show error "Screen already booked for this time"
   - NO → Continue creating showtime

4. **Which pricing strategy?**
   - Matinee → Apply -20%
   - Weekend → Apply +25%
   - Holiday → Apply +40%
   - Seat Zone → Adjust by row position
   - Can combine multiple strategies

### **Parallel Activities**:
- **Fork** after "Use Abstract Factory":
  - Branch 1: Create seat configuration
  - Branch 2: Create pricing model
  - Branch 3: Create equipment specifications
- **Join** before "Save theater configuration"

### **Object Nodes**:
- Theater object
- TheaterFactory object
- Screen object
- Seat configuration object
- Showtime object
- PricingStrategy object
- ValidationResult object

### **Notes**:
- Add note on factory: "Abstract Factory Pattern - creates complete theater configuration"
- Add note on pricing: "Strategy Pattern - different algorithms selected at runtime"
- Add note on validation: "Chain of Responsibility - sequential validation steps"
- Add note on seat generation: "Seats generated based on factory configuration"

### **Interruptible Region**:
- Around scheduling activities: "Can be cancelled by manager at any point"

### **Expansion Region**:
- Around pricing strategy application: "Multiple strategies can be combined"

### **Signal Events**:
- Send signal after "Make showtime available": "Notify frontend to refresh showtime list"

### **Key Points to Highlight**:
- Show Abstract Factory creating theater components
- Show Strategy Pattern selecting pricing algorithms
- Show Chain of Responsibility validation flow
- Highlight concurrent creation of theater components

---

## **DIAGRAM 7: STATE DIAGRAM - SHRISH (BOOKING LIFECYCLE)**

### **Purpose**:
Show all possible states of a booking and transitions between them (State Pattern implementation).

### **States** (rounded rectangles):
1. **[Initial]** - Filled circle (start pseudostate)
2. **Pending** - Initial state when booking created
3. **Locked** - Seats temporarily reserved
4. **Confirmed** - Payment successful
5. **Cancelled** - User cancelled booking
6. **Refunded** - Refund processed
7. **[Final]** - Filled circle with outer ring (end pseudostate)

### **Transitions** (arrows with labels):

**From Initial**:
- → Pending [when user selects seats]

**From Pending**:
- → Locked [when seats locked] / action: "Lock seats for 10 minutes"
- → Cancelled [user abandons booking] / action: "Release any temporary locks"

**From Locked**:
- → Confirmed [payment successful] / action: "Mark seats as booked, send confirmation email, generate QR code"
- → Pending [lock expires after 10 min] / action: "Release seat locks, broadcast seat availability"
- → Cancelled [user cancels during payment] / action: "Release seat locks"

**From Confirmed**:
- → Cancelled [user cancels booking AND cancellation allowed] / guard: [showtime > 24 hours away]
- → [Final] [show completed] / action: "Archive booking"

**From Cancelled**:
- → Refunded [refund processed] / action: "Process refund to original payment method"
- → [Final] [no refund applicable] / guard: [cancellation < 6 hours before show]

**From Refunded**:
- → [Final] / action: "Archive booking with refund details"

### **Guard Conditions** (in square brackets on transitions):
- [showtime > 24 hours away] - Full refund allowed
- [6 hours < showtime < 24 hours] - Partial refund allowed
- [showtime < 6 hours] - No refund
- [payment successful]
- [payment failed]
- [lock expired]
- [user cancelled]

### **Actions** (after slash on transitions):
- / Lock seats
- / Release locks
- / Send confirmation email
- / Generate QR code
- / Process refund
- / Update seat availability
- / Broadcast to WebSocket clients
- / Archive booking

### **Internal Activities** (inside state boxes):

**Locked State**:
- entry / Start 10-minute timer
- exit / Clear timer
- do / Monitor timer
- Timer expires → self-transition back to Pending

**Confirmed State**:
- entry / Generate QR code
- entry / Send confirmation email
- entry / Mark seats as permanently booked

**Cancelled State**:
- entry / Calculate refund amount
- entry / Release seats back to available pool
- entry / Send cancellation email

**Refunded State**:
- entry / Initiate refund transaction
- entry / Send refund confirmation email

### **Composite States** (optional enhancement):
- **Active** superstate containing: Pending, Locked, Confirmed
- **Terminal** superstate containing: Cancelled, Refunded

### **History State** (circle with 'H'):
- Add shallow history state in Active superstate (if booking needs to be restored after system crash)

### **Choice Pseudostate** (diamond):
- After Cancelled state, add choice: 
  - If eligible for refund → Refunded
  - If not eligible → Final

### **Notes**:
- Add note: "State Pattern implementation - each state is a separate class"
- Add note on Locked: "Observer Pattern notifies all users when lock expires"
- Add note: "Transitions trigger WebSocket broadcasts for real-time updates"

### **Key Points to Highlight**:
- Show timer-based transitions (Locked → Pending after 10 min)
- Highlight different cancellation policies with guard conditions
- Show actions that update database and notify users
- Emphasize state-specific behavior

---

## **DIAGRAM 8: STATE DIAGRAM - VAISHNAV (PAYMENT LIFECYCLE)**

### **Purpose**:
Show payment processing states and transitions through different payment statuses.

### **States**:
1. **[Initial]** - Start
2. **Initiated** - Payment request created
3. **Processing** - Communicating with payment gateway
4. **Awaiting Confirmation** - Waiting for gateway response (for UPI/Net Banking)
5. **Success** - Payment completed successfully
6. **Failed** - Payment failed
7. **Refund Initiated** - Refund request created
8. **Refund Processing** - Refund in progress
9. **Refunded** - Refund completed
10. **[Final]** - End

### **Transitions**:

**From Initial**:
- → Initiated [user clicks "Pay Now"] / action: "Create payment record"

**From Initiated**:
- → Processing [payment details submitted] / action: "Send to Payment Facade"

**From Processing**:
- → Success [gateway returns success] / action: "Update booking to CONFIRMED, generate QR code, send email"
- → Failed [gateway returns failure] / action: "Log error, show error message"
- → Awaiting Confirmation [for UPI/Net Banking] / action: "Display QR code or redirect URL"

**From Awaiting Confirmation**:
- → Success [user completes payment] / action: "Verify with gateway, confirm booking"
- → Failed [timeout or user cancels] / action: "Mark payment failed"
- Self-loop: "Poll gateway for status every 5 seconds"

**From Success**:
- → Refund Initiated [user cancels booking] / guard: [refund policy allows] / action: "Calculate refund amount"
- → [Final] [booking completed successfully]

**From Failed**:
- → Initiated [user clicks "Retry Payment"] / action: "Create new payment attempt"
- → [Final] [user abandons payment]

**From Refund Initiated**:
- → Refund Processing / action: "Send refund request to gateway"

**From Refund Processing**:
- → Refunded [gateway confirms refund] / action: "Update payment record, send confirmation"
- → Failed [refund failed] / action: "Notify support team, retry"

**From Refunded**:
- → [Final] / action: "Archive payment record"

### **Guard Conditions**:
- [payment method == CARD]
- [payment method == UPI]
- [payment method == NET_BANKING]
- [amount > 0]
- [refund amount > 0]
- [retry count < 3]

### **Actions**:
- / Route to appropriate gateway (Facade Pattern)
- / Validate payment details
- / Encrypt sensitive data
- / Log transaction
- / Update booking status
- / Send notification
- / Generate receipt

### **Internal Activities**:

**Processing State**:
- entry / Route payment through Facade
- do / Wait for gateway response (timeout: 2 minutes)
- exit / Log transaction result

**Awaiting Confirmation State**:
- entry / Display QR/redirect URL
- do / Poll gateway every 5 seconds
- exit / Clear polling timer

**Success State**:
- entry / Update booking to CONFIRMED
- entry / Generate QR code
- entry / Send confirmation email
- entry / Release seat locks

**Refund Processing State**:
- entry / Calculate refund amount based on cancellation policy
- do / Monitor refund status
- exit / Update payment record

### **Time Events**:
- After 2 minutes in Processing → Failed
- After 15 minutes in Awaiting Confirmation → Failed
- Poll every 5 seconds in Awaiting Confirmation

### **Composite States**:
- **In Progress** superstate containing: Initiated, Processing, Awaiting Confirmation
- **Completed** superstate containing: Success, Failed, Refunded

### **Notes**:
- Add note: "Facade Pattern routes to appropriate payment gateway"
- Add note: "Different payment methods follow different flows"
- Add note on UPI/Net Banking: "Asynchronous payment flow with polling"
- Add note on security: "Sensitive data encrypted before transmission"

### **Key Points to Highlight**:
- Show async payment flow for UPI/Net Banking
- Highlight Facade Pattern routing
- Show retry mechanism for failed payments
- Emphasize timeout handling

---

## **DIAGRAM 9: STATE DIAGRAM - SAFFIYA (MOVIE LIFECYCLE)**

### **Purpose**:
Show movie status changes from announcement to archival.

### **States**:
1. **[Initial]** - Start
2. **Announced** - Movie announced, details added by manager
3. **Upcoming** - Release date set, marketing active
4. **Now Showing** - Movie currently in theaters
5. **Ending Soon** - Last week of theatrical run
6. **Ended** - Theatrical run completed
7. **Archived** - Moved to archives (not displayed)
8. **[Final]** - End

### **Transitions**:

**From Initial**:
- → Announced [manager adds movie] / action: "Create movie record with basic details"

**From Announced**:
- → Upcoming [release date within 30 days] / action: "Display in 'Coming Soon' section"
- → Archived [movie cancelled] / action: "Mark as cancelled"

**From Upcoming**:
- → Now Showing [release date reached] / action: "Display in 'Now Showing', make showtimes bookable"
- → Archived [release cancelled] / action: "Cancel all scheduled showtimes"

**From Now Showing**:
- → Ending Soon [30 days since release OR last showtime within 7 days] / action: "Add 'Last Chance' badge"
- Self-loop: "Manager schedules new showtimes"

**From Ending Soon**:
- → Ended [last showtime completed] / action: "Remove from 'Now Showing', keep in search"
- → Now Showing [new showtimes added] / action: "Remove 'Last Chance' badge"

**From Ended**:
- → Archived [90 days after last showtime] / action: "Remove from active catalog"
- → Now Showing [re-release scheduled] / action: "Restore to active catalog"

**From Archived**:
- → [Final] / action: "Move to cold storage"

### **Guard Conditions**:
- [current date >= release date]
- [days since release >= 30]
- [has active showtimes]
- [all showtimes completed]
- [manager initiates]

### **Actions**:
- / Update movie status in database
- / Notify marketing system
- / Update frontend displays
- / Cancel future showtimes (if applicable)
- / Send notifications to users with saved movies
- / Update search index

### **Internal Activities**:

**Announced State**:
- entry / Set status to ANNOUNCED
- do / Allow manager to edit details

**Upcoming State**:
- entry / Set status to UPCOMING
- entry / Enable trailer playback
- entry / Show on homepage carousel
- do / Accept showtime scheduling
- exit / Notify users who "saved" this movie

**Now Showing State**:
- entry / Set status to NOW_SHOWING
- entry / Enable ticket booking
- entry / Start collecting reviews
- do / Monitor showtime bookings
- do / Update popularity metrics

**Ended State**:
- entry / Set status to ENDED
- entry / Disable new bookings
- do / Maintain review history
- do / Keep searchable for 90 days

### **Time Events**:
- After 30 days in Upcoming → Now Showing (on release date)
- After all showtimes end → Ended
- After 90 days in Ended → Archived

### **Notes**:
- Add note: "Status changes trigger frontend updates"
- Add note: "Manager can manually override some transitions"
- Add note: "Ended movies remain searchable for historical data"

### **Key Points to Highlight**:
- Show automatic transitions based on dates
- Highlight manager-controlled transitions
- Show different visibility in each state

---

## **DIAGRAM 10: STATE DIAGRAM - RUSHAD (SEAT AVAILABILITY)**

### **Purpose**:
Show seat status changes through booking process and real-time updates.

### **States**:
1. **[Initial]** - Start
2. **Available** - Seat can be selected
3. **Locked** - Temporarily reserved by a user
4. **Booked** - Permanently booked
5. **Released** - Booking cancelled, returned to available
6. **Unavailable** - Permanently unavailable (broken seat, etc.)
7. **[Final]** - End (after show completed)

### **Transitions**:

**From Initial**:
- → Available [showtime created] / action: "Generate seat with AVAILABLE status"

**From Available**:
- → Locked [user selects seat] / guard: [no existing lock] / action: "Create seat_lock record, broadcast to all users"
- → Unavailable [manager marks unavailable] / action: "Update seat status"

**From Locked**:
- → Booked [payment successful] / action: "Update booking_seats, broadcast to all users, remove lock"
- → Available [lock expires after 10 min] / action: "Delete seat_lock, broadcast to all users"
- → Available [user deselects seat] / action: "Delete seat_lock, broadcast to all users"

**From Booked**:
- → Released [user cancels booking AND showtime not started] / action: "Delete booking_seats record, broadcast availability"
- → [Final] [show completed] / action: "Archive booking data"

**From Released**:
- → Available / action: "Broadcast seat availability"

**From Unavailable**:
- → Available [manager marks as fixed] / action: "Update status, broadcast"

### **Guard Conditions**:
- [no active lock for this seat-showtime]
- [lock timer not expired]
- [payment completed]
- [showtime not started]
- [user has permission]

### **Actions**:
- / Create seat_lock record with 10-min expiry
- / Broadcast WebSocket message to all connected clients
- / Update UI for all users viewing this showtime
- / Delete seat_lock record
- / Update booking_seats table
- / Update showtimes.available_seats counter
- / Log state change

### **Internal Activities**:

**Available State**:
- entry / Set color to green in UI
- do / Monitor for selection events

**Locked State**:
- entry / Set color to yellow in UI
- entry / Start 10-minute countdown timer
- do / Monitor timer
- exit / Clear timer
- Self-loop every second: "Decrement timer display"

**Booked State**:
- entry / Set color to red in UI
- entry / Update seat as non-selectable
- do / Display booked status

**Released State**:
- entry / Remove booking association
- entry / Send notification to waiting users (if any)

### **Concurrent Regions** (optional):
- **Visibility Region**: Controls color/appearance in UI
- **Data Region**: Controls database status
- Both regions synchronized via Observer Pattern

### **Time Events**:
- After 10 minutes in Locked → Available (timer expiry)
- After show completion in Booked → Final

### **Signals**:
- Send signal on state change: "SeatStatusChanged" event
- Receive signal from other processes: "UserSelectedSeat", "PaymentConfirmed"

### **Notes**:
- Add note: "Observer Pattern - all connected clients notified on state change"
- Add note on Locked: "10-minute timer prevents indefinite locking"
- Add note: "WebSocket broadcasts ensure real-time synchronization"
- Add note: "Race conditions prevented by database unique constraints"

### **Key Points to Highlight**:
- Show automatic lock expiry mechanism
- Highlight real-time broadcast to other users
- Show how seat returns to Available after cancellation
- Emphasize concurrency handling

---

## **GENERAL DIAGRAM GUIDELINES**

### **Tools Recommended**:
- **Lucidchart** - Web-based, easy collaboration
- **Draw.io** - Free, works offline
- **Visual Paradigm** - Professional UML tool
- **PlantUML** - Text-based, version-controllable
- **StarUML** - Desktop application

### **Formatting Standards**:
- Use standard UML notation (no custom symbols)
- Keep diagrams readable (not too cluttered)
- Use consistent colors across all diagrams
- Add legends for colors/symbols used
- Include diagram title, date, and author
- Use high-resolution export (300 DPI minimum)
- Export as PNG or SVG for report inclusion

### **Color Scheme Suggestions**:
- Entities/Classes: Light yellow (#FFFFCC)
- Interfaces: Light green (#CCFFCC)
- Controllers: Light blue (#CCE5FF)
- Services: Light orange (#FFE5CC)
- States: Light purple (#E5CCFF)
- Activities: Light cyan (#CCFFFF)
- Decision nodes: Yellow (#FFFF99)
- Start/End: Black
- Transitions: Black arrows
- Error paths: Red

### **Documentation**:
- Each diagram should have a brief description (1-2 paragraphs)
- Explain key elements and their significance
- Reference design patterns used
- Note any assumptions or constraints

---