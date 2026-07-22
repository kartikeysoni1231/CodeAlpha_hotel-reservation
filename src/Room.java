public class Room {
    private String roomNumber;
    private String roomType;
    private double price;
    private boolean isAvailable;

    public Room(String roomNumber, String roomType, double price, boolean isAvailable) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.price = price;
        this.isAvailable = isAvailable;
    }

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

    public String toCsvString() {
        return roomNumber + "," + roomType + "," + price + "," + isAvailable;
    }

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
