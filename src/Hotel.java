import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Hotel {
    private List<Room> rooms;
    private List<Reservation> reservations;

    private static final String ROOMS_FILE = "rooms.txt";
    private static final String RESERVATIONS_FILE = "reservations.txt";

    public Hotel() {
        rooms = new ArrayList<>();
        reservations = new ArrayList<>();
        loadData();
    }

    public List<Room> getAllRooms() {
        return rooms;
    }

    public List<Reservation> getAllReservations() {
        return reservations;
    }

    public List<Room> getAvailableRooms() {
        List<Room> availableRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (room.isAvailable()) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }

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

    public Reservation bookRoom(Room room, String guestName, String contactNumber, LocalDate checkIn, LocalDate checkOut) {
        if (!room.isAvailable()) {
            throw new IllegalStateException("Room " + room.getRoomNumber() + " is already booked.");
        }

        String reservationId = "RES-" + (10000 + (int) (Math.random() * 90000));
        double totalCost = Reservation.calculateTotalPrice(room, checkIn, checkOut);
        Reservation reservation = new Reservation(
                reservationId, room, guestName, contactNumber, checkIn, checkOut, totalCost, false, "None"
        );

        room.setAvailable(false);
        reservations.add(reservation);
        saveData();
        return reservation;
    }

    public boolean cancelReservation(String reservationId) {
        Reservation target = null;
        for (Reservation res : reservations) {
            if (res.getReservationId().equalsIgnoreCase(reservationId)) {
                target = res;
                break;
            }
        }

        if (target != null) {
            target.getRoom().setAvailable(true);
            reservations.remove(target);
            saveData();
            return true;
        }
        return false;
    }

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

    public synchronized void saveData() {
        saveRooms();
        saveReservations();
    }

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
            return;
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
