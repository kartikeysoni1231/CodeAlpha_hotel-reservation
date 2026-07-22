/**
 * Room.java
 * 
 * Represents a single room in the hotel.
 * This class holds the attributes of a room: its number, type, nightly price, 
 * and whether it is currently available for booking. It also includes helper methods
 * to convert the Room object to and from a comma-separated values (CSV) format 
 * for text-file storage.
 */
public class Room {
    private String roomNumber;
    private String roomType; // Expected types: "Standard", "Deluxe", "Suite"
    private double price;
    private boolean isAvailable;

    /**
     * Constructor to initialize a Room object.
     * 
     * @param roomNumber  The unique number/identifier of the room (e.g., "101")
     * @param roomType    The category of the room ("Standard", "Deluxe", "Suite")
     * @param price       The nightly rate of the room
     * @param isAvailable The current availability status of the room
     */
    public Room(String roomNumber, String roomType, double price, boolean isAvailable) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.price = price;
        this.isAvailable = isAvailable;
    }

    // --- Getters and Setters ---

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    /**
     * Converts the Room object details into a CSV line format.
     * Used for saving room records to a text file.
     * 
     * @return CSV formatted string representation of the room
     */
    public String toCsvString() {
        return roomNumber + "," + roomType + "," + price + "," + isAvailable;
    }

    /**
     * Creates a Room object from a CSV line.
     * Used when reading room records from the saved text file.
     * 
     * @param csvLine The CSV formatted string from the text file
     * @return A Room object populated with the CSV data, or null if invalid
     */
    public static Room fromCsvString(String csvLine) {
        try {
            String[] parts = csvLine.split(",");
            if (parts.length >= 4) {
                String roomNumber = parts[0].trim();
                String roomType = parts[1].trim();
                double price = Double.parseDouble(parts[2].trim());
                boolean isAvailable = Boolean.parseBoolean(parts[3].trim());
                return new Room(roomNumber, roomType, price, isAvailable);
            }
        } catch (Exception e) {
            System.err.println("Error parsing room from CSV: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + roomType + ") - $" + price + "/night";
    }
}
