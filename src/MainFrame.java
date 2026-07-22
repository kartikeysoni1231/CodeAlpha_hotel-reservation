import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * MainFrame.java
 * 
 * The central User Interface window of the Hotel Reservation System.
 * Built using custom-designed light mode widgets to ensure a premium look and feel.
 * Implements a sidebar navigation control coupled with a CardLayout main display.
 * 
 * Separates GUI layout code from the Hotel business logic models.
 */
public class MainFrame extends JFrame {
    private Hotel hotel;

    // Layout
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel dashboardPanel;
    private JPanel roomsPanel;
    private JPanel bookFormPanel;
    private JPanel bookingsPanel;

    // Navigation Buttons
    private SidebarButton dashboardNavBtn;
    private SidebarButton roomsNavBtn;
    private SidebarButton bookNavBtn;
    private SidebarButton bookingsNavBtn;

    // 1. Dashboard Tab Components
    private JLabel totalRoomsLabel;
    private JLabel availableRoomsLabel;
    private JLabel bookedRoomsLabel;
    private JLabel revenueLabel;
    private OccupancyChart occupancyChart;
    private RevenueChart revenueChart;

    // 2. Rooms Tab Components
    private JPanel roomsGridPanel;
    private JTextField roomSearchField;
    private JComboBox<String> roomTypeFilter;
    private JCheckBox onlyAvailableCheck;

    // 3. New Booking Form Components
    private JComboBox<Room> roomDropdown;
    private JTextField guestNameField;
    private JTextField contactField;
    private JSpinner checkInSpinner;
    private JSpinner checkOutSpinner;
    private JLabel pricePreviewLabel;

    // 4. Bookings Tab Components
    private JTable bookingsTable;
    private DefaultTableModel tableModel;

    // Premium Color Palette (Agoda-inspired Light Blue & White Theme)
    public static final Color BG_DARK = new Color(240, 244, 249);    // Very soft blue-gray canvas background
    public static final Color BG_SIDEBAR = new Color(255, 255, 255); // Pure white sidebar
    public static final Color BG_CARD = new Color(255, 255, 255);    // Pure white cards and forms
    public static final Color TEXT_LIGHT = new Color(15, 23, 42);    // Slate 900: Dark gray/black for primary headings
    public static final Color TEXT_MUTED = new Color(71, 85, 105);    // Slate 600: Cool gray for details/secondary text
    public static final Color ACCENT_INDIGO = new Color(0, 98, 227);  // Agoda brand blue
    public static final Color ACCENT_HOVER = new Color(0, 80, 190);   // Darker hover blue
    public static final Color ACCENT_PRESSED = new Color(0, 60, 150); // Deep pressed blue
    public static final Color COLOR_GREEN = new Color(34, 197, 94);   // Bright emerald green
    public static final Color COLOR_RED = new Color(239, 68, 68);     // Bright rose red
    public static final Color BORDER_COLOR = new Color(222, 231, 242); // Clean light blue-gray borders

    /**
     * MainFrame Constructor. Sets up components and window sizing.
     * 
     * @param hotel The instance of Hotel managing business logic and data
     */
    public MainFrame(Hotel hotel) {
        this.hotel = hotel;
        
        // Window Configuration
        setTitle("Grand Vista Hotel - Administrator Dashboard");
        setSize(1150, 750);
        setMinimumSize(new Dimension(950, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        
        // Root layout
        setLayout(new BorderLayout());

        // Initialize Panels
        initSidebar();
        initContentArea();

        // Load active data to UI components
        refreshData();
        
        // Start in Dashboard
        selectTab("Dashboard", dashboardNavBtn);
    }

    // --- Core UI Initializations ---

    /**
     * Creates and styles the sidebar on the left side of the window.
     */
    private void initSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setBackground(BG_SIDEBAR);
        sidebarPanel.setPreferredSize(new Dimension(240, getHeight()));
        sidebarPanel.setLayout(new BorderLayout());
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOR));

        // Sidebar Logo Header
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 20));
        logoPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ACCENT_INDIGO);
                // Draw a sleek building vector silhouette
                int[] xPoints = {4, 12, 20, 20, 4};
                int[] yPoints = {8, 2, 8, 22, 22};
                g2.fillPolygon(xPoints, yPoints, 5);
                g2.setColor(COLOR_GREEN);
                g2.fillRect(10, 12, 4, 10); // Center doorway
            }
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(24, 24);
            }
        };
        
        JLabel logoText = new JLabel("GRAND VISTA");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoText.setForeground(TEXT_LIGHT);
        
        logoPanel.add(iconLabel);
        logoPanel.add(logoText);
        sidebarPanel.add(logoPanel, BorderLayout.NORTH);

        // Sidebar Navigation Links
        JPanel navListPanel = new JPanel();
        navListPanel.setOpaque(false);
        navListPanel.setLayout(new BoxLayout(navListPanel, BoxLayout.Y_AXIS));
        navListPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        dashboardNavBtn = new SidebarButton("Dashboard");
        roomsNavBtn = new SidebarButton("Rooms Grid");
        bookNavBtn = new SidebarButton("Book Room");
        bookingsNavBtn = new SidebarButton("Reservations");

        // Action listeners to change cards
        dashboardNavBtn.addActionListener(e -> selectTab("Dashboard", dashboardNavBtn));
        roomsNavBtn.addActionListener(e -> selectTab("Rooms", roomsNavBtn));
        bookNavBtn.addActionListener(e -> selectTab("BookForm", bookNavBtn));
        bookingsNavBtn.addActionListener(e -> selectTab("Bookings", bookingsNavBtn));

        navListPanel.add(dashboardNavBtn);
        navListPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navListPanel.add(roomsNavBtn);
        navListPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navListPanel.add(bookNavBtn);
        navListPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navListPanel.add(bookingsNavBtn);

        sidebarPanel.add(navListPanel, BorderLayout.CENTER);

        // Footer Brand Label
        JLabel footerLabel = new JLabel("v1.0.0 Admin Portal");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLabel.setForeground(TEXT_MUTED);
        footerLabel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        sidebarPanel.add(footerLabel, BorderLayout.SOUTH);

        add(sidebarPanel, BorderLayout.WEST);
    }

    /**
     * Initializes the CardLayout content panel that holds each of the 4 tabs.
     */
    private void initContentArea() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG_DARK);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Create the individual content views
        createDashboardPanel();
        createRoomsPanel();
        createBookFormPanel();
        createBookingsPanel();

        // Add them to the layout stack
        contentPanel.add(dashboardPanel, "Dashboard");
        contentPanel.add(roomsPanel, "Rooms");
        contentPanel.add(bookFormPanel, "BookForm");
        contentPanel.add(bookingsPanel, "Bookings");

        add(contentPanel, BorderLayout.CENTER);
    }

    // --- View Panels Setup ---

    /**
     * 1. Creates the overview dashboard view containing statistics metrics and graphics.
     */
    private void createDashboardPanel() {
        dashboardPanel = new JPanel(new GridBagLayout());
        dashboardPanel.setBackground(BG_DARK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;

        // Welcome Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Welcome Back, Administrator");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_LIGHT);
        JLabel dateLabel = new JLabel(LocalDate.now().toString());
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(TEXT_MUTED);
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(dateLabel, BorderLayout.SOUTH);

        gbc.gridwidth = 4;
        gbc.weighty = 0.05;
        dashboardPanel.add(headerPanel, gbc);

        // Metrics Labels
        totalRoomsLabel = new JLabel("0");
        availableRoomsLabel = new JLabel("0");
        bookedRoomsLabel = new JLabel("0");
        revenueLabel = new JLabel("$0.00");

        // Row of 4 Metric Cards
        JPanel metricsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        metricsPanel.setOpaque(false);
        metricsPanel.add(createMetricCard("Total Rooms", totalRoomsLabel, ACCENT_INDIGO));
        metricsPanel.add(createMetricCard("Available Rooms", availableRoomsLabel, COLOR_GREEN));
        metricsPanel.add(createMetricCard("Booked Rooms", bookedRoomsLabel, COLOR_RED));
        metricsPanel.add(createMetricCard("Total Revenue", revenueLabel, new Color(245, 158, 11))); // Amber

        gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.weighty = 0.2;
        dashboardPanel.add(metricsPanel, gbc);

        // Graphical Charts Row
        JPanel chartsContainer = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsContainer.setOpaque(false);

        // Chart Card A: Occupancy
        RoundedPanel occPanel = new RoundedPanel(12, BG_CARD);
        occPanel.setLayout(new BorderLayout());
        occPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel occTitle = new JLabel("OCCUPANCY ANALYSIS");
        occTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        occTitle.setForeground(TEXT_MUTED);
        occTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        occPanel.add(occTitle, BorderLayout.NORTH);
        
        occupancyChart = new OccupancyChart();
        occPanel.add(occupancyChart, BorderLayout.CENTER);
        chartsContainer.add(occPanel);

        // Chart Card B: Revenue
        RoundedPanel revPanel = new RoundedPanel(12, BG_CARD);
        revPanel.setLayout(new BorderLayout());
        revPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel revTitle = new JLabel("REVENUE BY ROOM TYPE");
        revTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        revTitle.setForeground(TEXT_MUTED);
        revTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        revPanel.add(revTitle, BorderLayout.NORTH);

        revenueChart = new RevenueChart();
        revPanel.add(revenueChart, BorderLayout.CENTER);
        chartsContainer.add(revPanel);

        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.weighty = 0.75;
        dashboardPanel.add(chartsContainer, gbc);
    }

    /**
     * 2. Creates the Rooms visual grid tab containing searching, filtering, and room cards.
     */
    private void createRoomsPanel() {
        roomsPanel = new JPanel(new BorderLayout(0, 15));
        roomsPanel.setBackground(BG_DARK);

        // Header controls (Search & Filter)
        JPanel filterBar = new JPanel(new GridBagLayout());
        filterBar.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Search text field
        gbc.gridx = 0;
        gbc.weightx = 0.4;
        roomSearchField = new JTextField();
        styleTextField(roomSearchField);
        // Add a placeholder label mechanism (simple tooltip or document listener)
        roomSearchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateRoomsGrid(); }
            public void removeUpdate(DocumentEvent e) { updateRoomsGrid(); }
            public void changedUpdate(DocumentEvent e) { updateRoomsGrid(); }
        });
        JPanel searchBox = new JPanel(new BorderLayout(5, 0));
        searchBox.setOpaque(false);
        JLabel sLabel = new JLabel("Search Room #:");
        sLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sLabel.setForeground(TEXT_MUTED);
        searchBox.add(sLabel, BorderLayout.NORTH);
        searchBox.add(roomSearchField, BorderLayout.CENTER);
        filterBar.add(searchBox, gbc);

        // Combo Box filter
        gbc.gridx = 1;
        gbc.weightx = 0.3;
        String[] types = {"All", "Standard", "Deluxe", "Suite"};
        roomTypeFilter = new JComboBox<>(types);
        styleComboBox(roomTypeFilter);
        roomTypeFilter.addActionListener(e -> updateRoomsGrid());
        JPanel typeBox = new JPanel(new BorderLayout(5, 0));
        typeBox.setOpaque(false);
        JLabel tLabel = new JLabel("Filter Type:");
        tLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tLabel.setForeground(TEXT_MUTED);
        typeBox.add(tLabel, BorderLayout.NORTH);
        typeBox.add(roomTypeFilter, BorderLayout.CENTER);
        filterBar.add(typeBox, gbc);

        // Checkbox only available
        gbc.gridx = 2;
        gbc.weightx = 0.3;
        onlyAvailableCheck = new JCheckBox("Show Available Only");
        onlyAvailableCheck.setOpaque(false);
        onlyAvailableCheck.setForeground(TEXT_LIGHT);
        onlyAvailableCheck.setFont(new Font("Segoe UI", Font.BOLD, 12));
        onlyAvailableCheck.setCursor(new Cursor(Cursor.HAND_CURSOR));
        onlyAvailableCheck.addActionListener(e -> updateRoomsGrid());
        filterBar.add(onlyAvailableCheck, gbc);

        roomsPanel.add(filterBar, BorderLayout.NORTH);

        // Grid display of room cards
        roomsGridPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        roomsGridPanel.setBackground(BG_DARK);

        JScrollPane scrollPane = new JScrollPane(roomsGridPanel);
        scrollPane.setBorder(null);
        scrollPane.setViewportBorder(null);
        scrollPane.setBackground(BG_DARK);
        scrollPane.getViewport().setBackground(BG_DARK);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);

        roomsPanel.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * 3. Creates the Booking Form panel for submitting reservation entries.
     */
    private void createBookFormPanel() {
        bookFormPanel = new JPanel(new GridBagLayout());
        bookFormPanel.setBackground(BG_DARK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Centered panel container
        RoundedPanel formContainer = new RoundedPanel(16, BG_CARD);
        formContainer.setLayout(new GridBagLayout());
        formContainer.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        formContainer.setPreferredSize(new Dimension(500, 560));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(8, 8, 8, 8);
        c.gridx = 0;
        c.gridy = 0;

        // Form Title
        JLabel formTitle = new JLabel("Book a Room");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        formTitle.setForeground(TEXT_LIGHT);
        c.gridwidth = 2;
        formContainer.add(formTitle, c);
        c.gridwidth = 1;

        // Divider
        c.gridy = 1;
        c.gridwidth = 2;
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COLOR);
        sep.setBackground(BG_DARK);
        formContainer.add(sep, c);
        c.gridwidth = 1;

        // Room Selection
        c.gridy = 2;
        c.gridx = 0;
        JLabel lblRoom = new JLabel("Select Room:");
        lblRoom.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRoom.setForeground(TEXT_MUTED);
        formContainer.add(lblRoom, c);

        c.gridx = 1;
        roomDropdown = new JComboBox<>();
        styleComboBox(roomDropdown);
        roomDropdown.addActionListener(e -> updatePricePreview());
        formContainer.add(roomDropdown, c);

        // Guest Name
        c.gridy = 3;
        c.gridx = 0;
        JLabel lblName = new JLabel("Guest Full Name:");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblName.setForeground(TEXT_MUTED);
        formContainer.add(lblName, c);

        c.gridx = 1;
        guestNameField = new JTextField();
        styleTextField(guestNameField);
        formContainer.add(guestNameField, c);

        // Contact Number
        c.gridy = 4;
        c.gridx = 0;
        JLabel lblContact = new JLabel("Contact Number:");
        lblContact.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblContact.setForeground(TEXT_MUTED);
        formContainer.add(lblContact, c);

        c.gridx = 1;
        contactField = new JTextField();
        styleTextField(contactField);
        formContainer.add(contactField, c);

        // Check-In Date (JSpinner Date Selector)
        c.gridy = 5;
        c.gridx = 0;
        JLabel lblCheckIn = new JLabel("Check-in Date:");
        lblCheckIn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCheckIn.setForeground(TEXT_MUTED);
        formContainer.add(lblCheckIn, c);

        c.gridx = 1;
        SpinnerDateModel spinIn = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        checkInSpinner = new JSpinner(spinIn);
        JSpinner.DateEditor editIn = new JSpinner.DateEditor(checkInSpinner, "yyyy-MM-dd");
        checkInSpinner.setEditor(editIn);
        styleSpinner(checkInSpinner);
        checkInSpinner.addChangeListener(e -> updatePricePreview());
        formContainer.add(checkInSpinner, c);

        // Check-Out Date
        c.gridy = 6;
        c.gridx = 0;
        JLabel lblCheckOut = new JLabel("Check-out Date:");
        lblCheckOut.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCheckOut.setForeground(TEXT_MUTED);
        formContainer.add(lblCheckOut, c);

        c.gridx = 1;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1); // Defaults check-out to tomorrow
        SpinnerDateModel spinOut = new SpinnerDateModel(cal.getTime(), null, null, Calendar.DAY_OF_MONTH);
        checkOutSpinner = new JSpinner(spinOut);
        JSpinner.DateEditor editOut = new JSpinner.DateEditor(checkOutSpinner, "yyyy-MM-dd");
        checkOutSpinner.setEditor(editOut);
        styleSpinner(checkOutSpinner);
        checkOutSpinner.addChangeListener(e -> updatePricePreview());
        formContainer.add(checkOutSpinner, c);

        // Price Preview Banner
        c.gridy = 7;
        c.gridx = 0;
        c.gridwidth = 2;
        pricePreviewLabel = new JLabel("Stay duration: 0 nights | Total Cost: $0.00");
        pricePreviewLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pricePreviewLabel.setForeground(COLOR_GREEN);
        pricePreviewLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        formContainer.add(pricePreviewLabel, c);
        c.gridwidth = 1;

        // Action Buttons Row
        c.gridy = 8;
        c.gridx = 0;
        c.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        FlatButton cancelBtn = new FlatButton("Reset Form", BORDER_COLOR, new Color(203, 213, 225), new Color(186, 200, 218), TEXT_LIGHT);
        cancelBtn.addActionListener(e -> {
            guestNameField.setText("");
            contactField.setText("");
            roomDropdown.setSelectedIndex(roomDropdown.getItemCount() > 0 ? 0 : -1);
            checkInSpinner.setValue(new Date());
            Calendar resetCal = Calendar.getInstance();
            resetCal.add(Calendar.DAY_OF_MONTH, 1);
            checkOutSpinner.setValue(resetCal.getTime());
            updatePricePreview();
        });
        
        FlatButton submitBtn = new FlatButton("Confirm Booking", ACCENT_INDIGO, ACCENT_HOVER, ACCENT_PRESSED);
        submitBtn.addActionListener(e -> handleFormBookingSubmit());

        btnPanel.add(cancelBtn);
        btnPanel.add(submitBtn);
        formContainer.add(btnPanel, c);

        // Center formContainer in bookFormPanel using GridBag
        gbc.gridx = 0;
        gbc.gridy = 0;
        bookFormPanel.add(formContainer, gbc);
    }

    /**
     * 4. Creates the bookings listing grid displaying the custom-themed reservations table.
     */
    private void createBookingsPanel() {
        bookingsPanel = new JPanel(new BorderLayout(0, 15));
        bookingsPanel.setBackground(BG_DARK);

        // Title and table filters row
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel label = new JLabel("Reservations Register");
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setForeground(TEXT_LIGHT);
        headerPanel.add(label, BorderLayout.WEST);

        bookingsPanel.add(headerPanel, BorderLayout.NORTH);

        // Table initialization
        String[] columns = {"ID", "Room #", "Room Type", "Guest Name", "Contact", "Check In", "Check Out", "Price", "Payment Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false; // Lock editing columns directly inside table rows
            }
        };

        bookingsTable = new JTable(tableModel);
        bookingsTable.setBackground(BG_DARK);
        bookingsTable.setForeground(TEXT_LIGHT);
        bookingsTable.setRowHeight(35);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingsTable.setShowGrid(false);
        bookingsTable.setIntercellSpacing(new Dimension(0, 0));
        bookingsTable.setSelectionBackground(new Color(99, 102, 241, 60)); // Translucent selected row
        bookingsTable.setSelectionForeground(Color.WHITE);

        // Headers Styling
        JTableHeader tableHeader = bookingsTable.getTableHeader();
        tableHeader.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(BG_SIDEBAR);
                setForeground(TEXT_LIGHT);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));
                setHorizontalAlignment(JLabel.CENTER);
                return this;
            }
        });
        tableHeader.setReorderingAllowed(false);

        // Custom Cell Styling & Rendering
        CustomTableCellRenderer cellRenderer = new CustomTableCellRenderer();
        for (int i = 0; i < bookingsTable.getColumnCount() - 1; i++) {
            bookingsTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
        bookingsTable.getColumnModel().getColumn(8).setCellRenderer(new StatusBadgeRenderer());

        JScrollPane tableScroll = new JScrollPane(bookingsTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        tableScroll.setBackground(BG_DARK);
        tableScroll.getViewport().setBackground(BG_DARK);

        bookingsPanel.add(tableScroll, BorderLayout.CENTER);

        // Control buttons bar at the bottom
        JPanel actionControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionControls.setOpaque(false);

        FlatButton cancelBtn = new FlatButton("Cancel Reservation", COLOR_RED, COLOR_RED.darker(), COLOR_RED.darker().darker());
        cancelBtn.addActionListener(e -> handleTableCancelReservation());

        FlatButton payBtn = new FlatButton("Process Payment", COLOR_GREEN, COLOR_GREEN.darker(), COLOR_GREEN.darker().darker());
        payBtn.addActionListener(e -> handleTableProcessPayment());

        FlatButton detailsBtn = new FlatButton("View Booking Details", ACCENT_INDIGO, ACCENT_HOVER, ACCENT_PRESSED);
        detailsBtn.addActionListener(e -> handleTableDetailsView());

        actionControls.add(cancelBtn);
        actionControls.add(payBtn);
        actionControls.add(detailsBtn);

        bookingsPanel.add(actionControls, BorderLayout.SOUTH);
    }

    // --- Action Handlers & Business Logic Bindings ---

    /**
     * Submits and validates booking inputs from the New Booking tab form.
     */
    private void handleFormBookingSubmit() {
        Room selectedRoom = (Room) roomDropdown.getSelectedItem();
        String guestName = guestNameField.getText().trim();
        String contactNum = contactField.getText().trim();

        if (selectedRoom == null) {
            JOptionPane.showMessageDialog(this, "Please select an available room.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (guestName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Guest name cannot be left blank.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (contactNum.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Contact number cannot be left blank.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date checkInVal = (Date) checkInSpinner.getValue();
        Date checkOutVal = (Date) checkOutSpinner.getValue();
        LocalDate checkIn = checkInVal.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate checkOut = checkOutVal.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            JOptionPane.showMessageDialog(this, "Check-out date must occur after check-in date.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Call Hotel Service to book the room
            Reservation res = hotel.bookRoom(selectedRoom, guestName, contactNum, checkIn, checkOut);

            // Clear inputs
            guestNameField.setText("");
            contactField.setText("");

            // Reload visual models
            refreshData();

            int payNow = JOptionPane.showConfirmDialog(this,
                    "Booking Registered! ID: " + res.getReservationId() + "\nWould you like to process payment simulation?",
                    "Success", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

            if (payNow == JOptionPane.YES_OPTION) {
                showPaymentDialog(res);
            } else {
                selectTab("Bookings", bookingsNavBtn);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Booking Failed: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Retrieves the selected reservation in the JTable list.
     */
    private Reservation getSelectedReservation() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please choose a reservation from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        String id = (String) bookingsTable.getValueAt(selectedRow, 0);
        for (Reservation res : hotel.getAllReservations()) {
            if (res.getReservationId().equals(id)) {
                return res;
            }
        }
        return null;
    }

    /**
     * Action triggered to cancel reservation from the booking register JTable.
     */
    private void handleTableCancelReservation() {
        Reservation res = getSelectedReservation();
        if (res == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Cancel reservation " + res.getReservationId() + " for " + res.getGuestName() + "?\nRoom " + res.getRoom().getRoomNumber() + " will become available.",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            hotel.cancelReservation(res.getReservationId());
            refreshData();
            JOptionPane.showMessageDialog(this, "Reservation cancelled successfully.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Action triggered to process payment simulation from the bookings register table.
     */
    private void handleTableProcessPayment() {
        Reservation res = getSelectedReservation();
        if (res == null) return;

        if (res.isPaid()) {
            JOptionPane.showMessageDialog(this, "This booking has already been paid.", "Paid Already", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        showPaymentDialog(res);
    }

    /**
     * Action triggered to display full modal description of selected booking.
     */
    private void handleTableDetailsView() {
        Reservation res = getSelectedReservation();
        if (res != null) {
            showBookingDetailsDialog(res);
        }
    }

    // --- Dynamic Rendering Updates ---

    /**
     * Re-draws and populates room grid cards depending on active text filters and type combos.
     */
    private void updateRoomsGrid() {
        roomsGridPanel.removeAll();

        String typeFilter = (String) roomTypeFilter.getSelectedItem();
        boolean onlyAvail = onlyAvailableCheck.isSelected();
        String query = roomSearchField.getText().trim().toLowerCase();

        List<Room> list = hotel.searchRooms(typeFilter, onlyAvail);
        for (Room r : list) {
            if (!query.isEmpty() && !r.getRoomNumber().contains(query)) {
                continue;
            }
            roomsGridPanel.add(createRoomCard(r));
        }

        roomsGridPanel.revalidate();
        roomsGridPanel.repaint();
    }

    /**
     * Re-populates the available rooms dropdown selector in the booking form.
     */
    private void updateBookingRoomDropdown() {
        roomDropdown.removeAllItems();
        List<Room> avail = hotel.getAvailableRooms();
        for (Room r : avail) {
            roomDropdown.addItem(r);
        }
        updatePricePreview();
    }

    /**
     * Updates the text label previewing cost dynamics in real time.
     */
    private void updatePricePreview() {
        Room r = (Room) roomDropdown.getSelectedItem();
        if (r == null) {
            pricePreviewLabel.setText("Stay duration: 0 nights | Total Cost: $0.00 (No Room Selected)");
            pricePreviewLabel.setForeground(TEXT_MUTED);
            return;
        }

        Date checkInVal = (Date) checkInSpinner.getValue();
        Date checkOutVal = (Date) checkOutSpinner.getValue();
        LocalDate inDate = checkInVal.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate outDate = checkOutVal.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (outDate.isBefore(inDate) || outDate.isEqual(inDate)) {
            pricePreviewLabel.setText("Error: Check-out date must occur after check-in date!");
            pricePreviewLabel.setForeground(COLOR_RED);
            return;
        }

        long nights = ChronoUnit.DAYS.between(inDate, outDate);
        if (nights <= 0) nights = 1;
        double price = Reservation.calculateTotalPrice(r, inDate, outDate);

        pricePreviewLabel.setText(String.format("Stay duration: %d night(s) | Total Cost: $%.2f", nights, price));
        pricePreviewLabel.setForeground(COLOR_GREEN);
    }

    /**
     * Triggers complete data synchronized reload. Updates metrics labels, charts,
     * tables, grids, and drop downs simultaneously.
     */
    public void refreshData() {
        int total = hotel.getAllRooms().size();
        int avail = hotel.getAvailableRooms().size();
        int booked = total - avail;

        double revenue = 0.0;
        double stdRev = 0.0;
        double dlxRev = 0.0;
        double suiteRev = 0.0;

        for (Reservation res : hotel.getAllReservations()) {
            if (res.isPaid()) {
                revenue += res.getTotalPrice();
                String type = res.getRoom().getRoomType();
                if (type.equalsIgnoreCase("Standard")) {
                    stdRev += res.getTotalPrice();
                } else if (type.equalsIgnoreCase("Deluxe")) {
                    dlxRev += res.getTotalPrice();
                } else if (type.equalsIgnoreCase("Suite")) {
                    suiteRev += res.getTotalPrice();
                }
            }
        }

        // Apply string metrics
        totalRoomsLabel.setText(String.valueOf(total));
        availableRoomsLabel.setText(String.valueOf(avail));
        bookedRoomsLabel.setText(String.valueOf(booked));
        revenueLabel.setText(String.format("$%.2f", revenue));

        // Reload charts
        occupancyChart.setData(total, booked);
        revenueChart.setData(stdRev, dlxRev, suiteRev);

        // Reload Bookings register table
        tableModel.setRowCount(0);
        for (Reservation r : hotel.getAllReservations()) {
            tableModel.addRow(new Object[]{
                    r.getReservationId(),
                    r.getRoom().getRoomNumber(),
                    r.getRoom().getRoomType(),
                    r.getGuestName(),
                    r.getContactNumber(),
                    r.getCheckInDate().toString(),
                    r.getCheckOutDate().toString(),
                    String.format("$%.2f", r.getTotalPrice()),
                    r.isPaid() ? "Paid (" + r.getPaymentMethod() + ")" : "Pending"
            });
        }

        // Re-filter components
        updateRoomsGrid();
        updateBookingRoomDropdown();
    }

    // --- Modal / Popups Creators ---

    /**
     * Builds and displays a customized pop-up modal showing reservation details.
     */
    private void showBookingDetailsDialog(Reservation res) {
        JDialog dialog = new JDialog(this, "Details: " + res.getReservationId(), true);
        dialog.setSize(420, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(BG_DARK);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 8, 10, 8);
        gbc.gridx = 0;

        // Title Header
        JLabel detailsHeader = new JLabel("Reservation Info");
        detailsHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        detailsHeader.setForeground(TEXT_LIGHT);
        gbc.gridwidth = 2;
        gbc.gridy = 0;
        centerPanel.add(detailsHeader, gbc);
        gbc.gridwidth = 1;

        // Detail list
        int rIndex = 1;
        addDetailRow(centerPanel, gbc, "Reservation ID:", res.getReservationId(), rIndex++);
        addDetailRow(centerPanel, gbc, "Room Association:", "Room #" + res.getRoom().getRoomNumber() + " (" + res.getRoom().getRoomType() + ")", rIndex++);
        addDetailRow(centerPanel, gbc, "Guest Name:", res.getGuestName(), rIndex++);
        addDetailRow(centerPanel, gbc, "Contact Number:", res.getContactNumber(), rIndex++);
        addDetailRow(centerPanel, gbc, "Check-in Date:", res.getCheckInDate().toString(), rIndex++);
        addDetailRow(centerPanel, gbc, "Check-out Date:", res.getCheckOutDate().toString(), rIndex++);
        addDetailRow(centerPanel, gbc, "Total Invoice Price:", String.format("$%.2f", res.getTotalPrice()), rIndex++);
        addDetailRow(centerPanel, gbc, "Status:", res.isPaid() ? "Paid (" + res.getPaymentMethod() + ")" : "Unpaid (Pending)", rIndex++);

        dialog.add(centerPanel, BorderLayout.CENTER);

        // Control Panel
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        controls.setBackground(BG_SIDEBAR);
        controls.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        FlatButton closeBtn = new FlatButton("Close", BORDER_COLOR, new Color(203, 213, 225), new Color(186, 200, 218), TEXT_LIGHT);
        closeBtn.addActionListener(e -> dialog.dispose());

        if (!res.isPaid()) {
            FlatButton payBtn = new FlatButton("Pay Now", COLOR_GREEN, COLOR_GREEN.darker(), COLOR_GREEN.darker().darker());
            payBtn.addActionListener(e -> {
                dialog.dispose();
                showPaymentDialog(res);
            });
            controls.add(payBtn);
        }

        FlatButton cancelBtn = new FlatButton("Cancel Reservation", COLOR_RED, COLOR_RED.darker(), COLOR_RED.darker().darker());
        cancelBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "Are you sure you want to cancel reservation " + res.getReservationId() + "?",
                    "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                hotel.cancelReservation(res.getReservationId());
                dialog.dispose();
                refreshData();
                JOptionPane.showMessageDialog(this, "Booking cancelled successfully.");
            }
        });

        controls.add(cancelBtn);
        controls.add(closeBtn);
        dialog.add(controls, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    /**
     * Builds and displays the simulation payment options modal.
     */
    private void showPaymentDialog(Reservation res) {
        JDialog dialog = new JDialog(this, "Simulate Payment", true);
        dialog.setSize(380, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(BG_DARK);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        JLabel title = new JLabel("Process Booking Payment");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_LIGHT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(title, gbc);

        gbc.gridy = 1;
        JLabel detail = new JLabel(res.getGuestName() + " | Reservation ID: " + res.getReservationId());
        detail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detail.setForeground(TEXT_MUTED);
        detail.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(detail, gbc);

        gbc.gridy = 2;
        JLabel amount = new JLabel(String.format("Amount Due: $%.2f", res.getTotalPrice()));
        amount.setFont(new Font("Segoe UI", Font.BOLD, 22));
        amount.setForeground(COLOR_GREEN);
        amount.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(amount, gbc);

        gbc.gridy = 3;
        gbc.gridwidth = 1;
        JLabel optLabel = new JLabel("Method:");
        optLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        optLabel.setForeground(TEXT_MUTED);
        centerPanel.add(optLabel, gbc);

        gbc.gridx = 1;
        String[] methods = {"UPI", "Card", "Cash"};
        JComboBox<String> combo = new JComboBox<>(methods);
        styleComboBox(combo);
        centerPanel.add(combo, gbc);

        dialog.add(centerPanel, BorderLayout.CENTER);

        JPanel actionControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actionControls.setBackground(BG_SIDEBAR);
        actionControls.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        FlatButton closeBtn = new FlatButton("Dismiss", BORDER_COLOR, new Color(203, 213, 225), new Color(186, 200, 218), TEXT_LIGHT);
        closeBtn.addActionListener(e -> dialog.dispose());

        FlatButton payBtn = new FlatButton("Simulate Paid", COLOR_GREEN, COLOR_GREEN.darker(), COLOR_GREEN.darker().darker());
        payBtn.addActionListener(e -> {
            String method = (String) combo.getSelectedItem();
            hotel.processPayment(res.getReservationId(), method);
            dialog.dispose();
            refreshData();
            JOptionPane.showMessageDialog(this, "Success: mark transaction complete via " + method, "Payment Verified", JOptionPane.INFORMATION_MESSAGE);
        });

        actionControls.add(closeBtn);
        actionControls.add(payBtn);
        dialog.add(actionControls, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    /**
     * Utility method helper that adds double column rows inside info dialogs.
     */
    private void addDetailRow(JPanel p, GridBagConstraints g, String key, String val, int row) {
        g.gridy = row;
        g.gridx = 0;
        g.weightx = 0.35;
        JLabel kl = new JLabel(key);
        kl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        kl.setForeground(TEXT_MUTED);
        p.add(kl, g);

        g.gridx = 1;
        g.weightx = 0.65;
        JLabel vl = new JLabel(val);
        vl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        vl.setForeground(TEXT_LIGHT);
        p.add(vl, g);
    }

    // --- Component Styling Helpers ---

    private void styleTextField(JTextField f) {
        f.setBackground(BG_CARD);
        f.setForeground(TEXT_LIGHT);
        f.setCaretColor(TEXT_LIGHT);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
    }

    private void styleComboBox(JComboBox<?> c) {
        c.setBackground(BG_CARD);
        c.setForeground(TEXT_LIGHT);
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        c.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        c.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean hasFocus) {
                Component comp = super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
                comp.setBackground(isSelected ? ACCENT_INDIGO : BG_CARD);
                comp.setForeground(TEXT_LIGHT);
                if (comp instanceof JLabel) {
                    ((JLabel) comp).setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                }
                return comp;
            }
        });
    }

    private void styleSpinner(JSpinner js) {
        js.setBackground(BG_CARD);
        js.setForeground(TEXT_LIGHT);
        js.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        if (js.getEditor() instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) js.getEditor()).getTextField();
            tf.setBackground(BG_CARD);
            tf.setForeground(TEXT_LIGHT);
            tf.setCaretColor(TEXT_LIGHT);
            tf.setBorder(null);
        }
    }

    /**
     * Selects and focuses a card panel inside the layout, resetting nav highlighting.
     */
    private void selectTab(String cardId, JButton actBtn) {
        cardLayout.show(contentPanel, cardId);

        // Reset sidebar button highlights
        for (Component c : sidebarPanel.getComponents()) {
            if (c instanceof JPanel) {
                for (Component child : ((JPanel) c).getComponents()) {
                    if (child instanceof SidebarButton) {
                        ((SidebarButton) child).setActive(false);
                    }
                }
            }
        }
        if (actBtn instanceof SidebarButton) {
            ((SidebarButton) actBtn).setActive(true);
        }
        sidebarPanel.repaint();
    }

    /**
     * Builds standard metric card widgets.
     */
    private JPanel createMetricCard(String title, JLabel valLabel, Color lineCol) {
        RoundedPanel card = new RoundedPanel(12, BG_CARD);
        card.setLayout(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLbl = new JLabel(title.toUpperCase());
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        titleLbl.setForeground(TEXT_MUTED);
        card.add(titleLbl, BorderLayout.NORTH);

        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valLabel.setForeground(TEXT_LIGHT);
        card.add(valLabel, BorderLayout.CENTER);

        // Bottom colored line
        JPanel line = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(lineCol);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 2, 2);
            }
        };
        line.setOpaque(false);
        line.setPreferredSize(new Dimension(0, 4));
        card.add(line, BorderLayout.SOUTH);

        return card;
    }

    /**
     * Generates a modern room status card.
     */
    private JPanel createRoomCard(Room r) {
        RoundedPanel card = new RoundedPanel(12, BG_CARD);
        card.setLayout(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        // Header info row
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel rNoLabel = new JLabel("Room " + r.getRoomNumber());
        rNoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        rNoLabel.setForeground(TEXT_LIGHT);
        topRow.add(rNoLabel, BorderLayout.WEST);

        // Room Type Badge
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (r.getRoomType().equalsIgnoreCase("Standard")) {
                    g2.setColor(new Color(0, 106, 254, 30));
                } else if (r.getRoomType().equalsIgnoreCase("Deluxe")) {
                    g2.setColor(new Color(245, 158, 11, 30));
                } else {
                    g2.setColor(new Color(168, 85, 247, 30)); // Suite is Purple
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            }
        };
        badge.setOpaque(false);
        JLabel badgeLabel = new JLabel(r.getRoomType());
        badgeLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        if (r.getRoomType().equalsIgnoreCase("Standard")) {
            badgeLabel.setForeground(new Color(29, 78, 216));
        } else if (r.getRoomType().equalsIgnoreCase("Deluxe")) {
            badgeLabel.setForeground(new Color(180, 83, 9));
        } else {
            badgeLabel.setForeground(new Color(109, 40, 217));
        }
        badge.add(badgeLabel);
        topRow.add(badge, BorderLayout.EAST);

        card.add(topRow, BorderLayout.NORTH);

        // Central details (Price & availability)
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        centerPanel.setOpaque(false);

        JLabel pLabel = new JLabel(String.format("$%.2f / night", r.getPrice()));
        pLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pLabel.setForeground(TEXT_LIGHT);
        centerPanel.add(pLabel);

        // Status indicator
        JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusRow.setOpaque(false);
        JLabel dot = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(r.isAvailable() ? COLOR_GREEN : COLOR_RED);
                g2.fillOval(0, 3, 8, 8);
            }
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(15, 15);
            }
        };
        JLabel statusText = new JLabel(r.isAvailable() ? " Available" : " Booked");
        statusText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusText.setForeground(r.isAvailable() ? COLOR_GREEN : COLOR_RED);
        statusRow.add(dot);
        statusRow.add(statusText);
        centerPanel.add(statusRow);

        card.add(centerPanel, BorderLayout.CENTER);

        // Quick action button
        FlatButton actionBtn;
        if (r.isAvailable()) {
            actionBtn = new FlatButton("Book Now", ACCENT_INDIGO, ACCENT_HOVER, ACCENT_PRESSED);
            actionBtn.addActionListener(e -> {
                // Focus booking page and pre-select this room
                selectTab("BookForm", bookNavBtn);
                // Search room in dropdown
                for (int i = 0; i < roomDropdown.getItemCount(); i++) {
                    Room item = roomDropdown.getItemAt(i);
                    if (item.getRoomNumber().equals(r.getRoomNumber())) {
                        roomDropdown.setSelectedIndex(i);
                        break;
                    }
                }
            });
        } else {
            actionBtn = new FlatButton("View Booking Info", BORDER_COLOR, new Color(203, 213, 225), new Color(186, 200, 218), TEXT_LIGHT);
            actionBtn.addActionListener(e -> {
                // Find associated reservation
                for (Reservation res : hotel.getAllReservations()) {
                    if (res.getRoom().getRoomNumber().equals(r.getRoomNumber())) {
                        showBookingDetailsDialog(res);
                        break;
                    }
                }
            });
        }
        card.add(actionBtn, BorderLayout.SOUTH);

        return card;
    }

    // --- Inner Custom Component Subclasses ---

    /**
     * Custom JPanel with anti-aliasing rounded corners.
     */
    static class RoundedPanel extends JPanel {
        private int radius;
        private Color bgColor;

        public RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            this.bgColor = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        }
    }

    /**
     * Beautiful custom buttons styled with anti-aliasing and active hover callbacks.
     */
    static class FlatButton extends JButton {
        private Color normalBg;
        private Color hoverBg;
        private Color pressedBg;
        private boolean isHovered = false;

        public FlatButton(String label, Color normal, Color hover, Color pressed, Color textCol) {
            super(label);
            this.normalBg = normal;
            this.hoverBg = hover;
            this.pressedBg = pressed;

            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setForeground(textCol);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    isHovered = true;
                    repaint();
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        public FlatButton(String label, Color normal, Color hover, Color pressed) {
            this(label, normal, hover, pressed, Color.WHITE);
        }

        public FlatButton(String label) {
            this(label, ACCENT_INDIGO, ACCENT_HOVER, ACCENT_PRESSED, Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (!isEnabled()) {
                g2.setColor(new Color(220, 225, 235));
            } else if (getModel().isPressed()) {
                g2.setColor(pressedBg);
            } else if (isHovered) {
                g2.setColor(hoverBg);
            } else {
                g2.setColor(normalBg);
            }
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g2.dispose();

            super.paintComponent(g);
        }
    }

    /**
     * Sidebar action buttons with styled active bar indicators.
     */
    static class SidebarButton extends JButton {
        private boolean isActive = false;

        public SidebarButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setForeground(TEXT_MUTED);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    if (!isActive) {
                        setForeground(ACCENT_INDIGO);
                    }
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    if (!isActive) {
                        setForeground(TEXT_MUTED);
                    }
                }
            });
        }

        public void setActive(boolean active) {
            this.isActive = active;
            setForeground(active ? ACCENT_INDIGO : TEXT_MUTED);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isActive) {
                g2.setColor(new Color(0, 98, 227, 20)); // Soft translucent Agoda blue highlight
                g2.fillRoundRect(8, 2, getWidth() - 16, getHeight() - 4, 8, 8);

                g2.setColor(ACCENT_INDIGO);
                g2.fillRoundRect(12, 8, 4, getHeight() - 16, 2, 2);
            }
            super.paintComponent(g);
        }
    }

    /**
     * Custom Table Cell Renderer to apply row colors and align texts.
     */
    static class CustomTableCellRenderer extends DefaultTableCellRenderer {
        public CustomTableCellRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            if (isSelected) {
                c.setBackground(new Color(0, 98, 227, 40)); // Light blue selection highlight
                c.setForeground(TEXT_LIGHT);
            } else {
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
                c.setForeground(TEXT_LIGHT);
            }
            return c;
        }
    }

    /**
     * Custom JTable Cell Badge renderer displaying Paid/Pending states.
     */
    static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            final String status = (value != null) ? value.toString() : "";
            JPanel cellPanel = new JPanel(new GridBagLayout());

            if (isSelected) {
                cellPanel.setBackground(new Color(0, 98, 227, 40));
            } else {
                cellPanel.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
            }
            cellPanel.setOpaque(true);

            // Badge container
            JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2)) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (status.startsWith("Paid")) {
                        g2.setColor(new Color(34, 197, 94, 35)); // Translucent success green
                    } else {
                        g2.setColor(new Color(245, 158, 11, 35)); // Translucent warning amber
                    }
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
            };
            badge.setOpaque(false);

            JLabel label = new JLabel(status);
            label.setFont(new Font("Segoe UI", Font.BOLD, 11));
            if (status.startsWith("Paid")) {
                label.setForeground(new Color(21, 128, 61));  // Rich dark green for readability
            } else {
                label.setForeground(new Color(180, 83, 9));    // Rich dark amber for readability
            }
            badge.add(label);

            cellPanel.add(badge);
            return cellPanel;
        }
    }

    /**
     * Custom graphics Donut Chart for Occupancy details.
     */
    static class OccupancyChart extends JComponent {
        private int total = 0;
        private int booked = 0;

        public void setData(int total, int booked) {
            this.total = total;
            this.booked = booked;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int size = Math.min(width, height) - 40;
            if (size <= 0) return;

            int x = (width - size) / 2;
            int y = (height - size) / 2;

            int bookedAngle = total > 0 ? (int) (((double) booked / total) * 360) : 0;
            int availAngle = 360 - bookedAngle;

            // Draw pie layers
            g2.setColor(COLOR_RED);
            g2.fillArc(x, y, size, size, 90, -bookedAngle);

            g2.setColor(COLOR_GREEN);
            g2.fillArc(x, y, size, size, 90 - bookedAngle, -availAngle);

            // Draw center cutout
            int cutoutSize = (int) (size * 0.65);
            int cx = x + (size - cutoutSize) / 2;
            int cy = y + (size - cutoutSize) / 2;
            g2.setColor(BG_CARD);
            g2.fillOval(cx, cy, cutoutSize, cutoutSize);

            // Draw occupancy label text
            int pct = total > 0 ? (int) (((double) booked / total) * 100) : 0;
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
            String pctText = pct + "%";
            FontMetrics fm = g2.getFontMetrics();
            int tx = cx + (cutoutSize - fm.stringWidth(pctText)) / 2;
            int ty = cy + (cutoutSize - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(pctText, tx, ty);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(TEXT_MUTED);
            String label = "Occupied";
            fm = g2.getFontMetrics();
            int lx = cx + (cutoutSize - fm.stringWidth(label)) / 2;
            int ly = ty + 18;
            g2.drawString(label, lx, ly);

            // Draw details legend
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.setColor(COLOR_GREEN);
            g2.fillRect(10, height - 20, 8, 8);
            g2.setColor(TEXT_LIGHT);
            g2.drawString("Avail: " + (total - booked), 22, height - 12);

            g2.setColor(COLOR_RED);
            g2.fillRect(85, height - 20, 8, 8);
            g2.setColor(TEXT_LIGHT);
            g2.drawString("Booked: " + booked, 97, height - 12);
        }
    }

    /**
     * Custom graphics Vertical Bar Chart for Revenue details.
     */
    static class RevenueChart extends JComponent {
        private double standardRev = 0.0;
        private double deluxeRev = 0.0;
        private double suiteRev = 0.0;

        public void setData(double standard, double deluxe, double suite) {
            this.standardRev = standard;
            this.deluxeRev = deluxe;
            this.suiteRev = suite;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int padBottom = 30;
            int padTop = 30;
            int padLeft = 30;
            int padRight = 30;

            // X baseline line
            g2.setColor(BORDER_COLOR);
            g2.drawLine(padLeft, height - padBottom, width - padRight, height - padBottom);

            // Draw Bars
            double max = Math.max(standardRev, Math.max(deluxeRev, suiteRev));
            if (max == 0.0) max = 1.0; // Avoid divide by zero

            int chartHeight = height - padBottom - padTop;
            int chartWidth = width - padLeft - padRight;
            int barWidth = Math.max(25, chartWidth / 7);
            int barGap = chartWidth / 6;

            // Standard Bar (Indigo)
            int h1 = (int) ((standardRev / max) * chartHeight);
            int x1 = padLeft + barGap;
            int y1 = height - padBottom - h1;
            g2.setColor(ACCENT_INDIGO);
            g2.fillRoundRect(x1, y1, barWidth, h1, 8, 8);
            drawValLabel(g2, standardRev, x1, y1, barWidth);

            // Deluxe Bar (Amber)
            int h2 = (int) ((deluxeRev / max) * chartHeight);
            int x2 = x1 + barWidth + barGap;
            int y2 = height - padBottom - h2;
            g2.setColor(new Color(245, 158, 11));
            g2.fillRoundRect(x2, y2, barWidth, h2, 8, 8);
            drawValLabel(g2, deluxeRev, x2, y2, barWidth);

            // Suite Bar (Purple)
            int h3 = (int) ((suiteRev / max) * chartHeight);
            int x3 = x2 + barWidth + barGap;
            int y3 = height - padBottom - h3;
            g2.setColor(new Color(168, 85, 247));
            g2.fillRoundRect(x3, y3, barWidth, h3, 8, 8);
            drawValLabel(g2, suiteRev, x3, y3, barWidth);

            // Draw axis labels
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.setColor(TEXT_MUTED);
            
            drawAxisLabel(g2, "Standard", x1, height - padBottom + 15, barWidth);
            drawAxisLabel(g2, "Deluxe", x2, height - padBottom + 15, barWidth);
            drawAxisLabel(g2, "Suite", x3, height - padBottom + 15, barWidth);
        }

        private void drawValLabel(Graphics2D g2, double val, int x, int y, int bWidth) {
            if (val <= 0.0) return;
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2.setColor(TEXT_LIGHT);
            String txt = String.format("$%.0f", val);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(txt, x + (bWidth - fm.stringWidth(txt)) / 2, y - 6);
        }

        private void drawAxisLabel(Graphics2D g2, String label, int x, int y, int bWidth) {
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(label, x + (bWidth - fm.stringWidth(label)) / 2, y);
        }
    }
}
