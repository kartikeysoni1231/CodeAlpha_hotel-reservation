import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Hotel.java
 * 
 * The business logic controller of the Hotel Reservation System.
 * It manages the collection of rooms and reservations in ArrayLists.
 * It is responsible for:
 * 1. Loading data from text files (rooms.txt, reservations.txt) on startup.
 * 2. Seeding initial rooms if no rooms file is found.
 * 3. Saving data back to text files upon any changes (booking, payment, cancellation).
 * 4. Booking rooms and calculating totals.
 * 5. Processing simulated payments (Cash, Card, UPI).
 * 6. Canceling reservations and restoring room availability.
 */
public class Hotel {
    private List<Room> rooms;
    private List<Reservation> reservations;

    private static final String ROOMS_FILE = "rooms.txt";
    private static final String RESERVATIONS_FILE = "reservations.txt";

    /**
     * Constructor. Initializes lists and loads data from files.
     */
    public Hotel() {
        rooms = new ArrayList<>();
        reservations = new ArrayList<>();
        loadData();
    }

    // --- Core Features / Business Logic ---

    /**
     * Retrieves all rooms in the hotel.
     */
    public List<Room> getAllRooms() {
        return rooms;
    }

    /**
     * Retrieves all reservations.
     */
    public List<Reservation> getAllReservations() {
        return reservations;
    }

    /**
     * Returns a list of rooms that are currently marked as available.
     */
    public List<Room> getAvailableRooms() {
        List<Room> availableRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (room.isAvailable()) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }

    /**
     * Searches and filters rooms by type and/or availability.
     * 
     * @param type           The room type to search for ("All", "Standard", "Deluxe", "Suite")
     * @param onlyAvailable If true, returns only available rooms
     * @return A list of rooms matching the search criteria
     */
    public List<Room> searchRooms(String type, boolean onlyAvailable) {
        List<Room> result = new ArrayList<>();
        for (Room room : rooms) {
            boolean typeMatches = type.equalsIgnoreCase("All") || room.getRoomType().equalsIgnoreCase(type);
            boolean availabilityMatches = !onlyAvailable || room.isAvailable();

            if (typeMatches && availabilityMatches) {
                result.add(room);
            }
        }
        return result;
    }

    /**
     * Creates and records a new booking reservation.
     * Marks the chosen room as unavailable (isAvailable = false).
     * Saves changes immediately to files.
     * 
     * @param room          The room to book
     * @param guestName     Name of the guest
     * @param contactNumber Contact phone number
     * @param checkIn       Check-in date
     * @param checkOut      Check-out date
     * @return The created Reservation object
     */
    public Reservation bookRoom(Room room, String guestName, String contactNumber, LocalDate checkIn, LocalDate checkOut) {
        // Double check room availability
        if (!room.isAvailable()) {
            throw new IllegalStateException("Room " + room.getRoomNumber() + " is already booked.");
        }

        // Generate a unique reservation ID: e.g., RES-84729
        String reservationId = "RES-" + (10000 + (int) (Math.random() * 90000));

        // Calculate cost
        double totalCost = Reservation.calculateTotalPrice(room, checkIn, checkOut);

        // Create the reservation (unpaid by default)
        Reservation reservation = new Reservation(
                reservationId, room, guestName, contactNumber, checkIn, checkOut, totalCost, false, "None"
        );

        // Mark room as occupied
        room.setAvailable(false);

        // Save in memory
        reservations.add(reservation);

        // Persist to files
        saveData();

        return reservation;
    }

    /**
     * Cancels an existing reservation.
     * Removes the reservation record, marks its associated room as available again,
     * and saves changes.
     * 
     * @param reservationId The ID of the reservation to cancel
     * @return true if canceled successfully, false if reservation was not found
     */
    public boolean cancelReservation(String reservationId) {
        Reservation target = null;
        for (Reservation res : reservations) {
            if (res.getReservationId().equalsIgnoreCase(reservationId)) {
                target = res;
                break;
            }
        }

        if (target != null) {
            // Restore room availability
            target.getRoom().setAvailable(true);

            // Remove from memory
            reservations.remove(target);

            // Persist changes
            saveData();
            return true;
        }

        return false;
    }

    /**
     * Simulates receiving payment for a booking.
     * Marks the booking as paid and records the payment method.
     * 
     * @param reservationId The reservation ID
     * @param paymentMethod The method used ("Cash", "Card", "UPI")
     * @return true if payment succeeded, false if reservation not found
     */
    public boolean processPayment(String reservationId, String paymentMethod) {
        for (Reservation res : reservations) {
            if (res.getReservationId().equalsIgnoreCase(reservationId)) {
                res.setPaid(true);
                res.setPaymentMethod(paymentMethod);
                saveData();
                return true;
            }
        }
        return false;
    }

    // --- Persistence & File I/O ---

    /**
     * Saves both rooms and reservations lists to their respective text files.
     */
    public synchronized void saveData() {
        saveRooms();
        saveReservations();
    }

    /**
     * Loads both rooms and reservations lists from their text files.
     */
    public synchronized void loadData() {
        loadRooms();
        loadReservations();
    }

    private void saveRooms() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ROOMS_FILE))) {
            for (Room room : rooms) {
                writer.println(room.toCsvString());
            }
        } catch (IOException e) {
            System.err.println("Error saving rooms to file: " + e.getMessage());
        }
    }

    private void loadRooms() {
        rooms.clear();
        File file = new File(ROOMS_FILE);

        if (!file.exists()) {
            System.out.println("Rooms file not found. Seeding initial rooms data...");
            seedRooms();
            saveRooms();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Room room = Room.fromCsvString(line);
                if (room != null) {
                    rooms.add(room);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading rooms from file: " + e.getMessage());
        }
    }

    private void saveReservations() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RESERVATIONS_FILE))) {
            for (Reservation res : reservations) {
                writer.println(res.toCsvString());
            }
        } catch (IOException e) {
            System.err.println("Error saving reservations to file: " + e.getMessage());
        }
    }

    private void loadReservations() {
        reservations.clear();
        File file = new File(RESERVATIONS_FILE);

        if (!file.exists()) {
            return; // No reservations saved yet, start with empty list
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Reservation res = Reservation.fromCsvString(line, rooms);
                if (res != null) {
                    reservations.add(res);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading reservations from file: " + e.getMessage());
        }
    }

    /**
     * Seeds the system with 10 default rooms if the rooms.txt file is missing.
     * Provides a healthy mix of Standard, Deluxe, and Suite rooms.
     */
    private void seedRooms() {
        rooms.add(new Room("101", "Standard", 80.0, true));
        rooms.add(new Room("102", "Standard", 80.0, true));
        rooms.add(new Room("103", "Standard", 80.0, true));
        rooms.add(new Room("104", "Deluxe", 130.0, true));
        rooms.add(new Room("105", "Deluxe", 130.0, true));
        rooms.add(new Room("201", "Deluxe", 140.0, true));
        rooms.add(new Room("202", "Suite", 220.0, true));
        rooms.add(new Room("203", "Suite", 220.0, true));
        rooms.add(new Room("301", "Suite", 300.0, true));
        rooms.add(new Room("302", "Suite", 350.0, true));
    }
}
