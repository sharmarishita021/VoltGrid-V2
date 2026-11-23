package com.evstation.app;

import com.evstation.comparator.BillAmountComparator;
import com.evstation.concurrency.ChargingSimulationTask;
import com.evstation.enums.BayStatus;
import com.evstation.enums.ConnectorType;
import com.evstation.enums.MembershipTier;
import com.evstation.enums.PaymentMode;
import com.evstation.exception.BayUnavailableException;
import com.evstation.exception.DuplicateVehicleException;
import com.evstation.exception.InvalidBatteryLevelException;
import com.evstation.exception.ReservationConflictException;
import com.evstation.layout.StationGridManager;
import com.evstation.model.*;
import com.evstation.repository.ReservationRepository;
import com.evstation.repository.SessionHistory;
import com.evstation.repository.VehicleRegistry;
import com.evstation.service.BayAllocationService;
import com.evstation.service.BillingService;
import com.evstation.service.QueueManagerService;
import com.evstation.service.ReservationService;
import com.evstation.strategy.pricing.DynamicTariffStrategy;
import com.evstation.strategy.priority.UnifiedPriorityStrategy;
import com.evstation.validation.InputValidator;
import com.evstation.validation.ReceiptFormatter;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Console UI Controller.
 * Supports concurrent multi-bay background charging and time-slot advance reservations.
 */
public class MenuController {

    private final StationGridManager gridManager;
    private final VehicleRegistry vehicleRegistry;
    private final SessionHistory sessionHistory;
    private final ReservationRepository reservationRepository;

    private final QueueManagerService queueService;
    private final BayAllocationService allocationService;
    private final ReservationService reservationService;
    private final BillingService billingService;
    private final ReceiptFormatter receiptFormatter;

    private final Map<String, ChargingSimulationTask> activeTasksByBay;
    private final Map<String, Thread> activeThreadsByBay;
    private final List<Bill> billingLedger;
    private final Scanner scanner;

    public MenuController() {
        this.gridManager = new StationGridManager();
        this.vehicleRegistry = new VehicleRegistry();
        this.sessionHistory = new SessionHistory();
        this.reservationRepository = new ReservationRepository();

        UnifiedPriorityStrategy priorityStrategy = new UnifiedPriorityStrategy();
        this.queueService = new QueueManagerService(priorityStrategy, vehicleRegistry.getDriverMap());
        this.allocationService = new BayAllocationService(gridManager);
        this.reservationService = new ReservationService(reservationRepository, gridManager);
        this.billingService = new BillingService(new DynamicTariffStrategy());
        this.receiptFormatter = new ReceiptFormatter();

        this.activeTasksByBay = new HashMap<>();
        this.activeThreadsByBay = new HashMap<>();
        this.billingLedger = new ArrayList<>();
        this.scanner = new Scanner(System.in);

        seedInitialData();
    }

    private void seedInitialData() {
        try {
            Driver d1 = new Driver("DRV-01", "Alice Smith", "9876543210", MembershipTier.GOLD, 100.0);
            Driver d2 = new Driver("DRV-02", "Bob Jones", "9876543211", MembershipTier.STANDARD, 50.0);
            Driver d3 = new Driver("DRV-03", "Officer Davis", "9110000000", MembershipTier.VIP, 300.0);

            Vehicle v1 = new PersonalEV("1HGCR2F83HA000101", "EV-PASS-01", "Tesla Model 3", ConnectorType.CCS2, 75.0, 20.0);
            Vehicle v2 = new CommercialEV("2C4RC1CG5ER000102", "EV-TAXI-02", "Nissan Leaf Fleet", ConnectorType.TYPE_2, 40.0, 15.0, "MetroCabs");
            Vehicle v3 = new EmergencyEV("3VW2B7AJ4FM000103", "EV-AMB-911", "Police Patrol EV", ConnectorType.CCS2, 90.0, 10.0, "Metro Police");

            vehicleRegistry.registerVehicle(v1, d1);
            vehicleRegistry.registerVehicle(v2, d2);
            vehicleRegistry.registerVehicle(v3, d3);

            // Pre-seed into queue
            queueService.enqueue(v1);
            queueService.enqueue(v2);
            queueService.enqueue(v3);

            // Pre-seed an advance reservation on BAY-04 from now + 1 hour to now + 2 hours
            LocalDateTime resStart = LocalDateTime.now().plusHours(1);
            LocalDateTime resEnd = resStart.plusHours(1);
            reservationService.createReservation(d1, v1, "BAY-04", new TimeSlot(resStart, resEnd));

        } catch (Exception e) {
            System.err.println("Seed initialization: " + e.getMessage());
        }
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Select an option [0-11]: ");
            int choice = InputValidator.parseIntSafe(scanner.nextLine(), -1);

            switch (choice) {
                case 1 -> viewGridMap();
                case 2 -> registerVehicleAndDriver();
                case 3 -> viewWaitingQueue();
                case 4 -> enqueueVehicle();
                case 5 -> allocateBayAndDock();
                case 6 -> startConcurrentChargingAtBay();
                case 7 -> settleBillAndReleaseBay();
                case 8 -> bookTimeSlotReservation();
                case 9 -> viewAndCancelReservations();
                case 10 -> checkInReservation();
                case 11 -> viewLedgerAndAnalytics();
                case 0 -> {
                    System.out.println("Shutting down VoltGrid Hub. Drive safe!");
                    running = false;
                }
                default -> System.out.println("Invalid selection. Enter a number 0 to 11.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n=======================================================");
        System.out.println("     VOLTGRID V2 - CONCURRENT EV CHARGING & BOOKING    ");
        System.out.println("=======================================================");
        System.out.println("1.  View Physical Lot 2D Grid & Bay Status");
        System.out.println("2.  Register New Vehicle & Driver");
        System.out.println("3.  View Prioritized Waiting Line (Unified Engine)");
        System.out.println("4.  Enqueue Vehicle into Waiting Line");
        System.out.println("5.  Allocate Bay to Next Queued Vehicle");
        System.out.println("6.  Start Charging at a Docked Bay (Runs in Background)");
        System.out.println("7.  Settle Bill & Release Bay (Checkout)");
        System.out.println("8.  Book Advance Time-Slot Reservation (Conflict Guard)");
        System.out.println("9.  View & Cancel Active Reservations");
        System.out.println("10. Check-in Reserved Vehicle to its Bay");
        System.out.println("11. View Station Financials & Revenue Ledger");
        System.out.println("0.  Exit System");
        System.out.println("=======================================================");
    }

    private void viewGridMap() {
        System.out.println(gridManager.renderAsciiGridMap());
        System.out.printf("Total Lot Occupancy: %.1f%%\n", gridManager.calculateOccupancyPercentage());

        if (!activeTasksByBay.isEmpty()) {
            System.out.println("--- LIVE ACTIVE CHARGING BAYS ---");
            for (Map.Entry<String, ChargingSimulationTask> entry : activeTasksByBay.entrySet()) {
                ChargingSimulationTask task = entry.getValue();
                Vehicle v = task.getSession().getVehicle();
                String status = task.isFinished() ? "FINISHED (Ready for Checkout)" : "CHARGING IN PROGRESS";
                System.out.printf("  %s: %s | Battery: %.1f%% / %.1f%% | %s\n",
                        entry.getKey(), v.getLicensePlate(), v.getCurrentSocPercent(), task.getTargetSoc(), status);
            }
        }
    }

    private void registerVehicleAndDriver() {
        System.out.println("\n--- REGISTER NEW VEHICLE & DRIVER ---");
        System.out.print("Enter 17-char VIN (or press Enter for auto-gen): ");
        String vin = scanner.nextLine().trim();
        if (vin.isEmpty()) {
            vin = "1FA6P8CF4H5" + (System.currentTimeMillis() % 1000000);
            while (vin.length() < 17) vin += "0";
            System.out.println("Generated VIN: " + vin);
        }

        if (!InputValidator.isValidVin(vin)) {
            System.out.println("Error: Invalid VIN format. Must be 17 alphanumeric characters.");
            return;
        }

        System.out.print("Enter License Plate (e.g. EV-CAR-55): ");
        String plate = scanner.nextLine().trim();
        if (!InputValidator.isValidPlate(plate)) {
            System.out.println("Error: Invalid license plate.");
            return;
        }

        System.out.print("Enter Model Name: ");
        String model = scanner.nextLine().trim();

        System.out.print("Select Plug [1. CCS2, 2. TYPE_2, 3. CHADEMO, 4. GB_T]: ");
        int plugChoice = InputValidator.parseIntSafe(scanner.nextLine(), 1);
        ConnectorType connector = switch (plugChoice) {
            case 2 -> ConnectorType.TYPE_2;
            case 3 -> ConnectorType.CHADEMO;
            case 4 -> ConnectorType.GB_T;
            default -> ConnectorType.CCS2;
        };

        System.out.print("Enter Battery Capacity in kWh [e.g. 60.0]: ");
        double cap = InputValidator.parseDoubleSafe(scanner.nextLine(), 60.0);

        System.out.print("Enter Current Battery SoC % [0 - 100]: ");
        double soc = InputValidator.parseDoubleSafe(scanner.nextLine(), 25.0);

        try {
            InputValidator.validateBatterySoc(soc);

            System.out.print("Type [1. Personal, 2. Fleet, 3. Emergency]: ");
            int type = InputValidator.parseIntSafe(scanner.nextLine(), 1);

            Vehicle v = switch (type) {
                case 2 -> new CommercialEV(vin, plate, model, connector, cap, soc, "MetroLogistics");
                case 3 -> new EmergencyEV(vin, plate, model, connector, cap, soc, "Metro Ambulance");
                default -> new PersonalEV(vin, plate, model, connector, cap, soc);
            };

            Driver d = new Driver("DRV-" + plate, "Driver of " + plate, "555-0199", MembershipTier.STANDARD, 50.0);
            vehicleRegistry.registerVehicle(v, d);
            System.out.println("Successfully registered: " + v);

        } catch (InvalidBatteryLevelException | DuplicateVehicleException e) {
            System.err.println("Registration Rejected: " + e.getMessage());
        }
    }

    private void viewWaitingQueue() {
        System.out.println("\n--- PRIORITIZED WAITING LINE ---");
        List<Vehicle> queue = queueService.getQueueSnapshot();
        if (queue.isEmpty()) {
            System.out.println("Waiting line is empty.");
            return;
        }

        int rank = 1;
        UnifiedPriorityStrategy strategy = new UnifiedPriorityStrategy();
        for (Vehicle v : queue) {
            Driver d = vehicleRegistry.findDriverByVin(v.getVin());
            double score = strategy.computeScore(v, d, 0);
            System.out.printf("  Rank %d: %s | Priority Score: %.1f\n", rank++, v, score);
        }
    }

    private void enqueueVehicle() {
        System.out.print("Enter VIN to enqueue: ");
        String vin = scanner.nextLine().trim();
        Vehicle v = vehicleRegistry.findByVin(vin);
        if (v == null) {
            System.out.println("Error: Vehicle not found in registry. Register it first (Option 2).");
            return;
        }
        queueService.enqueue(v);
        System.out.println("Vehicle added to prioritized waiting line.");
    }

    private void allocateBayAndDock() {
        Vehicle v = queueService.dequeue();
        if (v == null) {
            System.out.println("No vehicles waiting in queue.");
            return;
        }

        try {
            ChargingBay bay = allocationService.allocateBayForVehicle(v);
            System.out.printf("SUCCESS: Docked %s into %s (%s, %.1f kW)\n",
                    v.getLicensePlate(), bay.getBayId(), bay.getConnectorType(), bay.getChargerUnit().getMaxPowerKw());
        } catch (BayUnavailableException e) {
            System.err.printf("Allocation Failed: %s\n", e.getMessage());
            System.out.println("Returning vehicle back to queue...");
            queueService.enqueue(v);
        }
    }

    private void startConcurrentChargingAtBay() {
        System.out.println("\n--- START CHARGING AT A DOCKED BAY ---");

        List<ChargingBay> dockedBays = new ArrayList<>();
        for (ChargingBay[] row : gridManager.getBayGrid()) {
            for (ChargingBay b : row) {
                // Bay is occupied with a docked car and not currently charging
                if (b.getDockedVehicle() != null && b.getStatus() == BayStatus.OCCUPIED) {
                    dockedBays.add(b);
                }
            }
        }

        if (dockedBays.isEmpty()) {
            System.out.println("No docked vehicles ready to start charging.");
            System.out.println("(Tip: Use Option 5 to dock a vehicle from the queue into a bay first!)");
            return;
        }

        System.out.println("Bays currently ready to begin charging:");
        for (ChargingBay b : dockedBays) {
            System.out.printf("  - %s: %s (Current SoC: %.1f%%)\n",
                    b.getBayId(), b.getDockedVehicle().getLicensePlate(), b.getDockedVehicle().getCurrentSocPercent());
        }

        System.out.print("Enter Bay ID to start (e.g. " + dockedBays.get(0).getBayId() + "): ");
        String targetBayId = scanner.nextLine().trim().toUpperCase();

        ChargingBay targetBay = gridManager.getBayById(targetBayId);
        if (targetBay == null || targetBay.getDockedVehicle() == null || targetBay.getStatus() != BayStatus.OCCUPIED) {
            System.out.println("Invalid selection or bay is not currently ready to start.");
            return;
        }

        Vehicle v = targetBay.getDockedVehicle();
        Driver d = vehicleRegistry.findDriverByVin(v.getVin());
        if (d == null) {
            d = new Driver("WALK-IN", "Guest Driver", "0000000000", MembershipTier.STANDARD, 100.0);
        }

        ChargingSession session = new ChargingSession(v, d, targetBay);
        sessionHistory.recordNewSession(session);

        System.out.print("Enter target battery % [e.g. 80.0]: ");
        double target = InputValidator.parseDoubleSafe(scanner.nextLine(), 80.0);

        // Start background thread concurrently
        ChargingSimulationTask task = new ChargingSimulationTask(session, target);
        Thread chargeThread = new Thread(task, "ChargeThread-" + targetBay.getBayId());

        activeTasksByBay.put(targetBay.getBayId(), task);
        activeThreadsByBay.put(targetBay.getBayId(), chargeThread);

        chargeThread.start(); // Non-blocking start!

        System.out.printf(">>> [SUCCESS] %s at %s is now charging in the background!\n", v.getLicensePlate(), targetBay.getBayId());
        System.out.println("You can immediately dock another car at another bay, start another bay concurrently, or view the grid!");
    }

    private void settleBillAndReleaseBay() {
        System.out.println("\n--- SETTLE BILL & RELEASE BAY ---");

        if (activeTasksByBay.isEmpty()) {
            System.out.println("No active charging sessions to settle.");
            return;
        }

        System.out.println("Active bays:");
        for (Map.Entry<String, ChargingSimulationTask> entry : activeTasksByBay.entrySet()) {
            ChargingSimulationTask task = entry.getValue();
            Vehicle v = task.getSession().getVehicle();
            String status = task.isFinished() ? "FINISHED" : "CHARGING IN PROGRESS";
            System.out.printf("  - %s: %s (SoC: %.1f%%) | %s\n",
                    entry.getKey(), v.getLicensePlate(), v.getCurrentSocPercent(), status);
        }

        System.out.print("Enter Bay ID to settle and checkout: ");
        String bayId = scanner.nextLine().trim().toUpperCase();

        ChargingSimulationTask task = activeTasksByBay.get(bayId);
        Thread thread = activeThreadsByBay.get(bayId);

        if (task == null || thread == null) {
            System.out.println("No active charging session found on bay: " + bayId);
            return;
        }

        // If still charging in background, give user the option to wait
        if (!task.isFinished()) {
            System.out.print("This bay is still charging in progress. Wait for it to complete? (y/n): ");
            String ans = scanner.nextLine().trim().toLowerCase();
            if (ans.startsWith("y")) {
                try {
                    System.out.println("Waiting for charging thread to finish...");
                    thread.join();
                } catch (InterruptedException e) {
                    System.err.println("Wait interrupted: " + e.getMessage());
                }
            } else {
                System.out.println("Session remains charging in the background.");
                return;
            }
        }

        ChargingSession session = task.getSession();
        sessionHistory.completeSession(session);

        // Generate and format receipt
        Bill bill = billingService.generateBill(session, PaymentMode.WALLET);
        billingLedger.add(bill);
        System.out.println("\n" + receiptFormatter.formatReceipt(bill));

        // Release the bay
        allocationService.releaseBay(session.getBay());
        activeTasksByBay.remove(bayId);
        activeThreadsByBay.remove(bayId);

        System.out.printf("Bay %s has been settled and is now set back to AVAILABLE!\n", bayId);
    }

    private void bookTimeSlotReservation() {
        System.out.println("\n--- BOOK ADVANCE TIME-SLOT RESERVATION ---");
        System.out.print("Enter Vehicle VIN: ");
        String vin = scanner.nextLine().trim();
        Vehicle v = vehicleRegistry.findByVin(vin);
        if (v == null) {
            System.out.println("Error: Vehicle not registered. Use Option 2 first.");
            return;
        }

        Driver d = vehicleRegistry.findDriverByVin(vin);
        if (d == null) {
            d = new Driver("DRV-" + v.getLicensePlate(), "Driver", "555-0000", MembershipTier.STANDARD, 50.0);
        }

        System.out.print("Enter Bay ID to reserve (e.g. BAY-01, BAY-02): ");
        String bayId = scanner.nextLine().trim().toUpperCase();

        System.out.print("Reservation starts in how many hours from now? [e.g. 2]: ");
        int hoursAhead = InputValidator.parseIntSafe(scanner.nextLine(), 1);

        System.out.print("Duration of slot in minutes [e.g. 60]: ");
        int durationMinutes = InputValidator.parseIntSafe(scanner.nextLine(), 60);

        LocalDateTime start = LocalDateTime.now().plusHours(hoursAhead);
        LocalDateTime end = start.plusMinutes(durationMinutes);
        TimeSlot slot = new TimeSlot(start, end);

        try {
            Reservation res = reservationService.createReservation(d, v, bayId, slot);
            System.out.println("\n>>> RESERVATION CONFIRMED! <<<");
            System.out.println("Reservation ID : " + res.getReservationId());
            System.out.println("Bay Reserved   : " + res.getBay().getBayId());
            System.out.println("Vehicle        : " + res.getVehicle().getLicensePlate());
            System.out.println("Scheduled Slot : " + res.getTimeSlot());

        } catch (ReservationConflictException e) {
            System.err.println("\n>>> BOOKING REJECTED (CONFLICT DETECTED) <<<");
            System.err.println(e.getMessage());
            System.err.println("Colliding Slot: " + e.getConflictingSlot());
        } catch (BayUnavailableException e) {
            System.err.println("\n>>> BOOKING REJECTED: " + e.getMessage());
        }
    }

    private void viewAndCancelReservations() {
        System.out.println("\n--- ADVANCE RESERVATIONS ---");
        Collection<Reservation> all = reservationRepository.getAllReservations();
        if (all.isEmpty()) {
            System.out.println("No reservations on record.");
            return;
        }

        for (Reservation r : all) {
            System.out.println("  " + r);
        }

        System.out.print("\nEnter Reservation ID to cancel (or press Enter to skip): ");
        String cancelId = scanner.nextLine().trim();
        if (!cancelId.isEmpty()) {
            boolean success = reservationService.cancelReservation(cancelId);
            if (success) {
                System.out.println("Reservation " + cancelId + " cancelled successfully. Slot freed.");
            } else {
                System.out.println("Reservation not found or already inactive.");
            }
        }
    }

    private void checkInReservation() {
        System.out.print("Enter Reservation ID to check in: ");
        String resId = scanner.nextLine().trim();
        boolean success = reservationService.checkInReservation(resId);
        if (success) {
            System.out.println("Check-in successful! Vehicle docked in its reserved bay. Proceed to Option 6 to start charging.");
        } else {
            System.out.println("Check-in failed. Verify reservation ID and that the bay is currently free.");
        }
    }

    private void viewLedgerAndAnalytics() {
        System.out.println("\n================ STATION REVENUE & INVOICE LEDGER ================");
        if (billingLedger.isEmpty()) {
            System.out.println("No completed bills yet.");
            return;
        }

        List<Bill> sorted = new ArrayList<>(billingLedger);
        sorted.sort(new BillAmountComparator());

        double totalRevenue = 0.0;
        double totalKwh = 0.0;
        for (Bill b : sorted) {
            System.out.println("  " + b);
            totalRevenue += b.getTotalPayable();
            totalKwh += b.getEnergyKwh();
        }

        System.out.println("------------------------------------------------------------------");
        System.out.printf("Total Sessions: %d | Total Energy: %.2f kWh | Total Revenue: $%.2f\n",
                sorted.size(), totalKwh, totalRevenue);
        System.out.println("==================================================================");
    }
}
