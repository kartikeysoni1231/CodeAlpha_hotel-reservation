import javax.swing.SwingUtilities;

/**
 * Main.java
 * 
 * The main entry point and launcher for the Java Swing Hotel Reservation System.
 * 
 * This class:
 * 1. Sets global properties to ensure Swing renders text and geometry anti-aliased (smooth).
 * 2. Instantiates the 'Hotel' business model controller which triggers text-file loading and seeding.
 * 3. Launches the graphical user interface 'MainFrame' on the Event Dispatch Thread (EDT) 
 *    to guarantee thread safety and smooth UI responsiveness.
 */
public class Main {
    public static void main(String[] args) {
        // Enable system font anti-aliasing and subpixel rendering properties for standard AWT/Swing components
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Initialize and launch the UI on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // 1. Initialize the hotel logic controller (loads or seeds data)
                    Hotel hotel = new Hotel();

                    // 2. Instantiate the dashboard container with the hotel business instance
                    MainFrame frame = new MainFrame(hotel);

                    // 3. Make the dashboard visible
                    frame.setVisible(true);

                    System.out.println("Hotel Reservation System started successfully.");

                } catch (Exception e) {
                    System.err.println("Fatal Error during application startup: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}
