import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Reservation.java
 * 
 * Represents a single guest booking in the hotel reservation system.
 * This class tracks booking details including guest credentials, room association,
 * check-in/check-out dates, dynamically calculated costs, payment confirmation, 
 * and selected payment method (Cash, Card, UPI). It also contains CSV serialization methods.
 */
public class Reservation {
    private String reservationId;
    private Room room; // Reference to the room being booked
    private String guestName;
    private String contactNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double totalPrice;
    private boolean isPaid;
    private String paymentMethod; // "None", "Cash", "Card", "UPI"

    /**
     * Constructor to initialize a Reservation.
     */
    public Reservation(String reservationId, Room room, String guestName, String contactNumber,
                       LocalDate checkInDate, LocalDate checkOutDate, double totalPrice,
                       boolean isPaid, String paymentMethod) {
        this.reservationId = reservationId;
        this.room = room;
        this.guestName = guestName;
        this.contactNumber = contactNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = totalPrice;
        this.isPaid = isPaid;
        this.paymentMethod = paymentMethod;
    }

    // --- Business Logic Methods ---

    /**
     * Calculates the total price of a booking based on the room rate and stay duration.
     * If check-in and check-out are on the same day (or invalid chronological order),
     * a minimum duration of 1 night is applied.
     * 
     * @param room     The room being booked
     * @param checkIn  The starting date of stay
     * @param checkOut The ending date of stay
     * @return The calculated total price
     */
    public static double calculateTotalPrice(Room room, LocalDate checkIn, LocalDate checkOut) {
        if (room == null || checkIn == null || checkOut == null) {
            return 0.0;
        }
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights <= 0) {
            nights = 1; // 1-night minimum stay policy
        }
        return nights * room.getPrice();
    }

    // --- Getters and Setters ---

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    // --- Persistence Methods ---

    /**
     * Converts reservation state to a CSV line.
     * Format: reservationId,roomNumber,guestName,contactNumber,checkInDate,checkOutDate,totalPrice,isPaid,paymentMethod
     */
    public String toCsvString() {
        return reservationId + "," +
               room.getRoomNumber() + "," +
               guestName.replace(",", ";") + "," + // Prevent breaking CSV split on comma
               contactNumber.replace(",", ";") + "," +
               checkInDate + "," +
               checkOutDate + "," +
               totalPrice + "," +
               isPaid + "," +
               paymentMethod;
    }

    /**
     * Generates a Reservation object from CSV string and a list of existing rooms to associate the proper Room object.
     * 
     * @param csvLine The CSV formatted record
     * @param rooms   The list of existing rooms in the hotel
     * @return The populated Reservation object, or null if invalid
     */
    public static Reservation fromCsvString(String csvLine, java.util.List<Room> rooms) {
        try {
            String[] parts = csvLine.split(",");
            if (parts.length >= 9) {
                String reservationId = parts[0].trim();
                String roomNumber = parts[1].trim();
                String guestName = parts[2].trim().replace(";", ",");
                String contactNumber = parts[3].trim().replace(";", ",");
                LocalDate checkInDate = LocalDate.parse(parts[4].trim());
                LocalDate checkOutDate = LocalDate.parse(parts[5].trim());
                double totalPrice = Double.parseDouble(parts[6].trim());
                boolean isPaid = Boolean.parseBoolean(parts[7].trim());
                String paymentMethod = parts[8].trim();

                // Find the associated Room object
                Room room = null;
                for (Room r : rooms) {
                    if (r.getRoomNumber().equals(roomNumber)) {
                        room = r;
                        break;
                    }
                }

                // If the room doesn't exist, we construct a dummy room or log error
                if (room == null) {
                    System.err.println("Warning: Room #" + roomNumber + " not found for reservation " + reservationId);
                    // Create a placeholder room to avoid NullPointerExceptions
                    room = new Room(roomNumber, "Unknown", 0.0, false);
                }

                return new Reservation(reservationId, room, guestName, contactNumber, 
                                       checkInDate, checkOutDate, totalPrice, isPaid, paymentMethod);
            }
        } catch (Exception e) {
            System.err.println("Error parsing reservation from CSV: " + e.getMessage());
        }
        return null;
    }
}
