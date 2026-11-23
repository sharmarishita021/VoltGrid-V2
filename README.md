# VoltGrid V2: Concurrent EV Charging, Unified Priority & Reservation System

VoltGrid V2 is a streamlined, production-style Java application designed for Electric Vehicle (EV) smart charging stations. It provides **concurrent multi-bay background charging**, **unified priority-based queue scheduling**, and an **advance time-slot reservation system** with conflict detection and cancellation.

---

## 📂 Exact 43 Classes Breakdown

```
com.evstation
├── config
│   └── StationConfig.java                     [1]  - System configuration and tariff thresholds
├── enums
│   ├── ConnectorType.java                     [2]  - Supported plugs (CCS2, TYPE_2, CHADEMO, GB_T)
│   ├── BayStatus.java                         [3]  - Bay states (AVAILABLE, CHARGING, OCCUPIED, etc.)
│   ├── MembershipTier.java                    [4]  - Loyalty discounts (STANDARD, GOLD, VIP)
│   ├── PaymentMode.java                       [5]  - Payment options (WALLET, CARD, UPI, CASH)
│   └── ReservationStatus.java                 [6]  - Booking states (CONFIRMED, CHECKED_IN, CANCELLED)
├── model
│   ├── Vehicle.java                           [7]  - Abstract base entity; Comparable<Vehicle>
│   ├── PersonalEV.java                        [8]  - Private passenger EV subclass
│   ├── CommercialEV.java                      [9]  - Commercial fleet EV subclass
│   ├── EmergencyEV.java                       [10] - Emergency vehicle subclass (top priority)
│   ├── Driver.java                            [11] - Customer profile & wallet balance
│   ├── ChargingBay.java                       [12] - Physical charging slot model
│   ├── ChargingSession.java                   [13] - Active & completed charging session tracker
│   ├── Bill.java                              [14] - Itemized invoice entity
│   ├── TimeSlot.java                          [15] - Value object: [start, end] & overlap detection
│   └── Reservation.java                       [16] - Advance time-slot booking record
├── hardware
│   ├── ChargerUnit.java                       [17] - Dispenser hardware (kW power rating)
│   └── EnergyMeter.java                       [18] - Digital meter recording kWh consumed
├── layout
│   └── StationGridManager.java                [19] - 2D Array matrix (ChargingBay[][])
├── validation
│   ├── InputValidator.java                    [20] - Regex matching and numeric validation
│   └── ReceiptFormatter.java                  [21] - Invoice generation via StringBuilder
├── strategy.priority
│   ├── IPriorityStrategy.java                 [22] - Strategy interface for queue scoring
│   └── UnifiedPriorityStrategy.java           [23] - Policy: Emergency (+1000) + Battery + Wait + Member
├── strategy.pricing
│   ├── IBillingStrategy.java                  [24] - Strategy interface for tariff calculation
│   └── DynamicTariffStrategy.java             [25] - Peak-hour vs. standard tariff calculation
├── comparator
│   ├── VehiclePriorityComparator.java         [26] - Sorts by priority score; arrival time tie-breaker
│   ├── ReservationTimeComparator.java         [27] - Sorts reservations by starting time
│   └── BillAmountComparator.java              [28] - Sorts invoices by total revenue
├── exception
│   ├── EVStationException.java                [29] - Base custom checked exception
│   ├── BayUnavailableException.java           [30] - Checked: no compatible bay free
│   ├── DuplicateVehicleException.java         [31] - Checked: duplicate VIN registration
│   ├── ReservationConflictException.java      [32] - Checked: time-slot collision detected
│   └── InvalidBatteryLevelException.java      [33] - Unchecked: battery SoC outside [0, 100]
├── repository
│   ├── VehicleRegistry.java                   [34] - Fast storage via HashMap and HashSet
│   ├── SessionHistory.java                    [35] - History storage via ArrayList and LinkedHashMap
│   └── ReservationRepository.java             [36] - Stores reservations and checks bay interval overlaps
├── concurrency
│   └── ChargingSimulationTask.java            [37] - Concurrent Runnable task running per bay
├── service
│   ├── QueueManagerService.java               [38] - Unified PriorityQueue management & safe iteration
│   ├── BayAllocationService.java              [39] - Bay matching & exception propagation
│   ├── ReservationService.java                [40] - Booking, collision detection & check-ins
│   └── BillingService.java                    [41] - Itemized tariff, discount, and tax calculation
└── app
    ├── MenuController.java                    [42] - Operational console interface (No demo clutter)
    └── EVStationApp.java                      [43] - Main application entry point
```

---

## ⚡ Key Real-World Features

1. **Concurrent Multi-Bay Background Charging:**
   * Starting a car's charge (**Option 6**) spawns an independent thread that runs in the background without freezing the console.
   * You can immediately dock another car at Bay 2 and start charging it as well.
   * Multiple cars charge **simultaneously in parallel** across different bays!
   * Checking **Option 1 (View 2D Grid)** shows live statuses: e.g. `BAY-01:[CHRG ]  BAY-02:[CHRG ]`.
   * When any car finishes charging, use **Option 7** to settle its bill, print the itemized receipt, and free that bay.

2. **Unified Priority Scheduling Engine:**
   * One `PriorityQueue<Vehicle>` driven by `UnifiedPriorityStrategy` and `VehiclePriorityComparator`.
   * Emergency vehicles automatically receive top priority (+1000 pts).
   * Regular vehicles are scored via battery depletion $(100 - \text{SoC})$, wait time, and membership.
   * If two cars have identical priority scores, **arrival timestamp acts as the strict tie-breaker** (FIFO order).

3. **Time-Slot Advance Reservations:**
   * Customers can reserve a specific bay for a future time window (e.g. 2 hours from now for 60 minutes).
   * **Collision Detection:** If another booking collides (`overlapsWith()`), `ReservationConflictException` is thrown.
   * **Cancellations:** Releasing a reservation frees that time slot for other customers.
   * **Check-In:** When the driver arrives, the reservation checks in and immediately docks their EV into the bay.

---

## 🚀 How to Run

* **1-Click Run:** Double-click `run.bat` in `C:\Users\raman\Downloads\ev-machines-v2`.
* **In VS Code:** Open folder `ev-machines-v2` and press **F5**.
