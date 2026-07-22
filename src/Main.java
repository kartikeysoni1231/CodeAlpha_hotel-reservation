import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Hotel hotel = new Hotel();
                    MainFrame frame = new MainFrame(hotel);
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
