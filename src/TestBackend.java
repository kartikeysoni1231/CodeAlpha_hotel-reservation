import java.time.LocalDate;

public class TestBackend {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("Starting Hotel Reservation Backend Integration Test");
        System.out.println("==================================================");

        System.out.println("\n[Test 1] Initializing Hotel service...");
        Hotel hotel = new Hotel();
        int roomCount = hotel.getAllRooms().size();
        System.out.println("Total rooms loaded: " + roomCount);
        if (roomCount == 10) {
            System.out.println("-> SUCCESS: 10 default rooms seeded successfully.");
        } else {
            System.out.println("-> FAILURE: Room count is " + roomCount + " (expected 10).");
        }

        System.out.println("\n[Test 2] Viewing available rooms...");
        int initialAvailCount = hotel.getAvailableRooms().size();
        System.out.println("Available rooms: " + initialAvailCount);
        if (initialAvailCount == 10) {
            System.out.println("-> SUCCESS: All 10 rooms initially available.");
        } else {
            System.out.println("-> FAILURE: Available room count is " + initialAvailCount);
        }

        System.out.println("\n[Test 3] Searching standard and suite rooms...");
        int standardRooms = hotel.searchRooms("Standard", false).size();
        int suites = hotel.searchRooms("Suite", false).size();
        System.out.println("Standard rooms count: " + standardRooms + ", Suites count: " + suites);
        if (standardRooms == 3 && suites == 4) {
            System.out.println("-> SUCCESS: Correct room counts searched by type.");
        } else {
            System.out.println("-> FAILURE: Search counts standard=" + standardRooms + ", suites=" + suites);
        }

        System.out.println("\n[Test 4] Booking Room 101...");
        Room room101 = hotel.searchRooms("Standard", true).get(0);
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(3);
        
        Reservation res = hotel.bookRoom(room101, "Jane Doe", "+1-555-0199", checkIn, checkOut);
        System.out.println("Booking created with ID: " + res.getReservationId());
        System.out.println("Room 101 availability after booking: " + room101.isAvailable());
        System.out.println("Stay Duration: " + java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut) + " nights");
        System.out.println("Total Price calculated: $" + res.getTotalPrice());

        if (!room101.isAvailable() && res.getTotalPrice() == 160.0) {
            System.out.println("-> SUCCESS: Room marked booked and pricing calculates correctly.");
        } else {
            System.out.println("-> FAILURE: Booking logic error. Price=" + res.getTotalPrice() + ", Avail=" + room101.isAvailable());
        }

        System.out.println("\n[Test 5] Simulating Payment (UPI)...");
        System.out.println("Initial paid status: " + res.isPaid() + " (Method: " + res.getPaymentMethod() + ")");
        hotel.processPayment(res.getReservationId(), "UPI");
        System.out.println("Post-payment status: " + res.isPaid() + " (Method: " + res.getPaymentMethod() + ")");
        if (res.isPaid() && res.getPaymentMethod().equals("UPI")) {
            System.out.println("-> SUCCESS: Payment marked complete via UPI.");
        } else {
            System.out.println("-> FAILURE: Payment processing failed.");
        }

        System.out.println("\n[Test 6] Reloading database from text files to test persistence...");
        Hotel reloadedHotel = new Hotel();
        int loadedBookings = reloadedHotel.getAllReservations().size();
        System.out.println("Active bookings loaded: " + loadedBookings);
        
        if (loadedBookings == 1) {
            Reservation loadedRes = reloadedHotel.getAllReservations().get(0);
            System.out.println("Loaded guest: " + loadedRes.getGuestName());
            System.out.println("Loaded payment: " + loadedRes.isPaid() + " (Method: " + loadedRes.getPaymentMethod() + ")");
            System.out.println("Associated room 101 availability: " + loadedRes.getRoom().isAvailable());
            
            if (loadedRes.getGuestName().equals("Jane Doe") && loadedRes.isPaid() && !loadedRes.getRoom().isAvailable()) {
                System.out.println("-> SUCCESS: Persistent state correctly reloaded.");
            } else {
                System.out.println("-> FAILURE: Loaded reservation values are incorrect.");
            }
        } else {
            System.out.println("-> FAILURE: Reloaded bookings size is " + loadedBookings + " (expected 1)");
        }

        System.out.println("\n[Test 7] Canceling booking to restore availability...");
        boolean cancelSuccess = reloadedHotel.cancelReservation(res.getReservationId());
        System.out.println("Cancellation result: " + cancelSuccess);
        System.out.println("Active bookings size: " + reloadedHotel.getAllReservations().size());
        
        Room reloaded101 = null;
        for (Room r : reloadedHotel.getAllRooms()) {
            if (r.getRoomNumber().equals("101")) {
                reloaded101 = r;
                break;
            }
        }
        System.out.println("Room 101 availability after cancel: " + (reloaded101 != null ? reloaded101.isAvailable() : "null"));

        if (cancelSuccess && reloadedHotel.getAllReservations().isEmpty() && reloaded101 != null && reloaded101.isAvailable()) {
            System.out.println("-> SUCCESS: Reservation canceled and room availability restored.");
        } else {
            System.out.println("-> FAILURE: Cancellation logic error.");
        }

        System.out.println("\n==================================================");
        System.out.println("Hotel Reservation Backend Test Suite Complete");
        System.out.println("==================================================");
    }
}
