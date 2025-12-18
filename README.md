# Member Club - JavaFX Rental System

A complete rental management system for outdoor equipment built with JavaFX, demonstrating advanced object-oriented programming, modern UI design, and robust architecture.

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.org/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21.0.6-orange.svg)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-red.svg)](https://maven.apache.org/)

## Table of Contents

- [About the Project](#about-the-project)
- [Features](#features)
- [Project Structure](#project-structure)
- [Architecture & Design](#architecture--design)
- [How to Run](#how-to-run)
- [Usage](#usage)
- [Technical Implementation](#technical-implementation)
- [Requirements Met](#requirements-met)

## About the Project

Built as the final OOP assignment for YH education in Java System Development. This project evolved from a console-based rental system into a full-featured JavaFX desktop application with a modern, professional interface.

The application manages outdoor equipment rentals for "Wigells Friluftsuthyrning" - a fictional member club offering camping gear, fishing equipment, and water vehicles. It demonstrates proper OOP architecture, design patterns, JavaFX UI development, threading, persistence, and real-world software engineering practices.

## Features

### Core Functionality

- **User Authentication** - Secure login system for staff members
- **Dashboard** - Real-time overview with KPI cards and charts (revenue trends, inventory distribution)
- **Member Management** - Add, edit, search, and filter members with different membership levels
- **Inventory Management** - Dynamic form system adapting to item type (tent, boat, fishing rod, etc.)
- **Rental Operations** - Shopping cart system with automatic discount calculation
- **Rental History** - Track active and completed rentals with status indicators
- **Return Processing** - Handle returns with automatic late fee calculation
- **Receipt Generation** - Professional receipts showing original price, discounts, and final cost

### Modern UI Features

- **Custom Window Frame** - Borderless window with custom title bar and controls
- **Collapsible Sidebar** - Smooth animations for navigation menu
- **Live Search & Filtering** - Real-time filtering using FilteredList
- **Interactive Charts** - Revenue over time (7 days, 30 days, yearly) and inventory distribution
- **Toast Notifications** - Non-intrusive feedback for user actions
- **Modal Dialogs** - Context-aware forms with blur effects
- **Status Badges** - Color-coded visual indicators for membership levels and rental status
- **Responsive Layout** - Adapts to window size with maximum content width constraints

### Background Services

- **Auto-save** - Automatic data persistence every minute
- **Uptime Tracking** - Real-time display of application runtime
- **Thread Safety** - Proper JavaFX threading with Platform.runLater()

### Data Persistence

- **JSON Storage** - All data saved to JSON files (users, members, items, rentals)
- **Polymorphic Serialization** - Handles complex inheritance hierarchy with RuntimeTypeAdapterFactory
- **Automatic Loading** - Data loads on startup with fallback to sample data

## Project Structure

```
src/main/java/
└── org.example.memberclubjavafx_assignment5/
    ├── Main.java                                    # JavaFX Application entry point
    │
    ├── model/                                       # Domain models
    │   ├── Item.java                                # Abstract base for all items
    │   ├── Member.java                              # Club member
    │   ├── Rental.java                              # Rental transaction (with domain logic)
    │   ├── User.java                                # System user (staff)
    │   ├── camping/                                 # Camping equipment
    │   │   ├── CampingEquipment.java                # Abstract base
    │   │   ├── Tent.java, SleepingBag.java
    │   │   ├── Backpack.java, Lantern.java
    │   │   └── TrangiaKitchen.java
    │   ├── fishing/                                 # Fishing equipment
    │   │   ├── FishingEquipment.java                # Abstract base
    │   │   ├── FishingRod.java, FishingNet.java
    │   │   └── FishingBait.java
    │   ├── vehicles/                                # Water vehicles
    │   │   ├── WaterVehicle.java                    # Abstract base
    │   │   ├── Boat.java                            # Abstract intermediate
    │   │   ├── Kayak.java
    │   │   ├── MotorBoat.java, ElectricBoat.java
    │   │   └── RowBoat.java
    │   └── enums/                                   # Type-safe enumerations
    │       ├── ItemType.java, ItemStatus.java
    │       ├── MembershipLevel.java, RentalStatus.java
    │       ├── RentalPeriod.java
    │       └── [15+ domain-specific enums]
    │
    ├── service/                                     # Business logic layer
    │   ├── Inventory.java                           # Item repository
    │   ├── MemberRegistry.java                      # Member repository
    │   ├── RentalService.java                       # Rental operations
    │   ├── MembershipService.java                   # Member operations
    │   ├── MemberValidator.java                     # Input validation
    │   ├── RevenueService.java                      # Financial tracking
    │   ├── StorageService.java                      # File I/O with JSON
    │   ├── GsonConfig.java                          # Gson configuration
    │   ├── LocalDateTimeAdapter.java                # Custom type adapter
    │   └── UptimeService.java                       # Uptime tracking thread
    │
    ├── pricing/                                     # Strategy pattern
    │   ├── PricePolicy.java                         # Interface
    │   ├── PricingFactory.java                      # Factory
    │   ├── StandardPricing.java                     # No discount
    │   ├── StudentPricing.java                      # 20% discount
    │   └── PremiumPricing.java                      # 30% discount
    │
    ├── system/                                      # System infrastructure
    │   ├── ClubSystem.java                          # Central coordinator
    │   ├── AutoSaveManager.java                     # Background save thread
    │   ├── ItemIdGenerator.java                     # ID generation
    │   ├── MemberIdGenerator.java                   # ID generation
    │   └── SampleDataLoader.java                    # Demo data
    │
    ├── view/                                        # JavaFX views
    │   ├── MainLayout.java                          # Main application layout
    │   ├── LoginView.java                           # Authentication screen
    │   ├── DashboardView.java                       # Analytics overview
    │   ├── MemberView.java                          # Member management
    │   ├── ItemView.java                            # Inventory management
    │   ├── RentalView.java                          # Rental operations
    │   ├── UserView.java                            # Staff management
    │   ├── SidebarView.java                         # Navigation menu
    │   ├── CustomWindowFrame.java                   # Custom title bar
    │   ├── WindowResizeHandler.java                 # Window resize logic
    │   ├── NotificationFactory.java                 # Toast notifications
    │   ├── ViewUtils.java                           # UI utilities
    │   ├── ItemFormDialog.java                      # Item creation/editing
    │   ├── BookingFormDialog.java                   # Rental dialog
    │   ├── ReceiptDialog.java                       # Receipt display
    │   ├── components/                              # Reusable UI components
    │   │   ├── ItemTableComponent.java              # Item table
    │   │   ├── ItemFormComponent.java               # Dynamic item form
    │   │   ├── RentalBookingComponent.java          # Booking interface
    │   │   └── RentalHistoryComponent.java          # Rental list
    │   └── strategy/                                # Form strategies
    │       ├── ItemFormStrategy.java                # Interface
    │       ├── BaseStrategy.java                    # Abstract base
    │       ├── ItemStrategyFactory.java             # Factory
    │       └── [13 concrete strategies for each item type]
    │
    └── exceptions/                                  # Custom exceptions
        ├── ItemNotFoundException.java
        ├── ItemNotAvailableException.java
        ├── MemberNotFoundException.java
        └── RentalNotFoundException.java

src/main/resources/
└── org.example.memberclubjavafx_assignment5/
    ├── styles.css                                   # Custom styling
    └── icon.png                                     # Application icon

data/                                                # JSON persistence (gitignored)
├── users.json                                       # Staff accounts
├── members.json                                     # Club members
├── items.json                                       # Inventory
└── rentals.json                                     # Rental history
```

**Total:** 96 Java files, ~12,400 lines of code, organized into logical packages with clear separation of concerns.

## Architecture & Design

### Multi-Layered Architecture

**Presentation Layer (View)**
- JavaFX views and components
- User interaction handling
- Data binding and formatting
- No business logic

**Service Layer**
- Business logic and validation
- Coordinates between repositories
- Exception handling
- Transaction management

**Data Layer (Model)**
- Domain entities
- Repositories (Inventory, MemberRegistry)
- Data persistence (StorageService)

**System Layer**
- Application lifecycle
- Background services (AutoSave, Uptime)
- ID generation

### Design Patterns Implemented

**Strategy Pattern** (2 uses)
- **Pricing:** Different pricing strategies for membership levels
- **Item Forms:** Dynamic form generation based on item type

**Factory Pattern** (2 uses)
- **PricingFactory:** Selects correct pricing strategy
- **ItemStrategyFactory:** Selects correct form strategy

**Observer Pattern**
- JavaFX Properties for reactive UI updates
- Callbacks for component communication

**MVC/MVP Pattern**
- Clear separation between Model, View, and Controller logic
- Views depend on models, not vice versa

**Singleton-ish Pattern**
- ClubSystem acts as central coordinator
- Single source of truth for application state

**Component Pattern**
- Reusable, self-contained UI components
- Composition over inheritance in views

### Rich Domain Model

Domain objects contain their own business logic:
- `Rental.isLate()` - calculates if rental is overdue
- `Rental.calculatePenaltyFee()` - computes late fees
- `Item.isAvailable()` - checks availability status
- `User.validatePassword()` - authentication logic

### SOLID Principles

**Single Responsibility**
- Each class has one reason to change
- Validators separated from services
- UI components have focused responsibilities

**Open/Closed**
- New item types added without modifying existing code
- Strategy pattern enables extension without modification

**Liskov Substitution**
- All `Item` subclasses are interchangeable
- `PricePolicy` implementations are substitutable

**Interface Segregation**
- Small, focused interfaces (`PricePolicy`, `ItemFormStrategy`)
- Clients depend only on methods they use

**Dependency Inversion**
- High-level modules depend on abstractions
- Services depend on interfaces, not concrete classes

## How to Run

### Prerequisites

- **Java 21** or higher
- **Maven 3.8+** (included via Maven Wrapper)
- **JavaFX 21** (automatically downloaded by Maven)

### Using Maven (Recommended)

```bash
# Clone the repository
git clone https://github.com/yourusername/memberclub-javafx.git
cd memberclub-javafx

# Run with Maven (Unix/Mac/Git Bash)
./mvnw clean javafx:run

# Run with Maven (Windows)
mvnw.cmd clean javafx:run
```

### Using IntelliJ IDEA

1. Open project in IntelliJ IDEA
2. Wait for Maven to download dependencies
3. Right-click `Main.java`
4. Select "Run 'Main.main()'"

### First Run

Default login credentials:
- **Username:** `danieleriksson` **Password:** `0000`
- **Username:** `tomaswigell` **Password:** `5555`

Sample data (members, items) loads automatically on first startup.

## Usage

### Typical Workflow

1. **Login** - Authenticate as staff member
2. **Dashboard** - View system overview and analytics
3. **Manage Members** - Add/edit members, search by name, filter by level
4. **Manage Inventory** - Add items (forms adapt to item type), search, filter by category
5. **Create Rental** - Select member, add items to cart, choose period (hourly/daily), checkout
6. **View History** - Monitor active rentals, search by member or item
7. **Process Return** - Return items, system calculates late fees if applicable
8. **View Receipt** - Professional receipt showing discounts and costs

### Key Features to Try

- **Dashboard Charts** - Switch between 7 days, 30 days, and yearly revenue views
- **Member Levels** - Create STUDENT member and see 20% discount applied at checkout
- **Item Types** - Add different item types and see form adapt (tent vs boat vs fishing rod)
- **Late Fees** - Rent an item, manually edit JSON to make it overdue, then return it
- **Live Search** - Type in search fields to see real-time filtering
- **Sidebar** - Click collapse button to minimize navigation menu
- **Custom Window** - Drag title bar to move, drag edges to resize, double-click to maximize

### Data Persistence

All changes are automatically saved:
- **Auto-save** runs every minute in background
- **Manual save** on application shutdown
- **Files stored** in `data/` folder as JSON

To reset data: delete `data/` folder and restart application.

## Technical Implementation

### JavaFX UI

**Custom Styling**
- AtlantaFX NordDark theme as base
- 958 lines of custom CSS for professional look
- CSS variables for consistent colors and sizing
- Glassmorphism effects for modern aesthetics

**Animations**
- Sidebar collapse/expand with Timeline
- View transitions with FadeTransition and TranslateTransition
- Toast notification slide-in with easing

**Custom Controls**
- Borderless window with StageStyle.TRANSPARENT
- Custom title bar with window controls (minimize, maximize, close)
- WindowResizeHandler for edge dragging with proper anchor logic
- Rounded corners enforced via clipping mask

**Advanced TableView Usage**
- Custom CellFactory for badges and formatting
- Context menus on right-click
- Double-click to edit
- FilteredList for live search
- Observable collections for automatic updates

### Threading

**Background Threads**
- **AutoSaveManager:** ScheduledExecutorService saves every 60 seconds
- **UptimeService:** Tracks application runtime, updates UI via Platform.runLater()
- All threads set as daemon threads for clean shutdown

**Thread Safety**
- UI updates always wrapped in Platform.runLater()
- Synchronized saveAll() method prevents concurrent writes
- Proper thread lifecycle (start/stop)

### Data Persistence

**JSON with Gson**
- RuntimeTypeAdapterFactory handles polymorphic serialization
- Custom LocalDateTimeAdapter for proper date formatting
- Pretty printing enabled for human-readable files
- Graceful handling of missing/corrupted files

**ID Generation**
- Persistent counters that survive application restarts
- Scans existing data on load to set correct next ID
- Prefixed IDs (TENT-001, RENT-042) for readability

### Validation & Error Handling

**Input Validation**
- MemberValidator ensures valid member data
- Form validation before allowing save
- Try-catch blocks with user-friendly error messages
- Toast notifications for feedback

**Custom Exceptions**
- ItemNotFoundException, ItemNotAvailableException
- MemberNotFoundException, RentalNotFoundException
- Descriptive messages for debugging

### Performance Optimizations

- HashMap for O(1) lookups in registries
- FilteredList for efficient in-memory filtering
- Lazy loading of components
- Chart animations disabled for smoother updates

## Requirements Met

### Course Requirements

**Runnable application** - No crashes, controlled shutdown  
**Classes & objects** - 96 classes with proper OOP  
**Abstract Item class** - With 12 concrete subclasses  
**PricePolicy interface** - With 3 implementations  
**Private attributes** - With getters/setters  
**Collections & streams** - FilteredList, lambda expressions  
**File persistence** - Load on start, save on demand and auto-save  
**Separate thread** - AutoSave and Uptime services  
**High code quality** - Well-named, organized, documented  
**Professional system** - Not just a school exercise  
**OOP throughout** - Clear object-oriented design  
**JavaFX UI** - Complete graphical interface  
**TableView/ListView** - Multiple tables for data display  
**User feedback** - Toast notifications and dialogs  
**Robust error handling** - Try-catch, custom exceptions  

### Additional Features (Beyond Requirements)

- Custom window frame with resize handling
- Dashboard with interactive charts (AreaChart, BarChart)
- Strategy pattern for dynamic UI forms
- Shopping cart system for better UX
- Real-time search and filtering
- Status badges with color coding
- Receipt generation with discount display
- Late fee calculation with business logic in domain model
- Professional CSS styling with animations
- Component-based architecture
- Factory patterns for extensibility
- Rich domain models with business logic


---

**Author:** Daniel Eriksson  
**Course:** Java System Development (YH)  
**Assignment:** JavaFX Application Development  
**Date:** December 2025 

