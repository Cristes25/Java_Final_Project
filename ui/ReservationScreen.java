package ui;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReservationScreen extends javax.swing.JFrame {

    private static final String URL = "jdbc:sqlite:database/airlineReservation.db";
    private static int USER_ID;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private JLabel priceLabel;

    //Fetch Prices
    private double fetchPrice(String departure, String arrival, String category) {
        String priceColumn;
        switch (category.toLowerCase()) {
            case "basic":
                priceColumn = "basicPrice";
                break;
            case "economic":
                priceColumn = "economicPrice";
                break;
            case "premium":
                priceColumn = "premiumPrice";
                break;
            default:
                throw new IllegalArgumentException("Invalid category: " + category);
        }

        String query = "SELECT " + priceColumn + " FROM Flights WHERE departureAirport = ? AND arrivalAirport = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, departure);
            stmt.setString(2, arrival);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(priceColumn); // Return the fetched price
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if no price is found
    }
    private void updatePrice() {
        String departure = (String) departureComboBox.getSelectedItem();
        String arrival = (String) arrivalComboBox.getSelectedItem();
        if (departure == null || arrival == null || departure.equals(arrival)) {
            priceLabel.setText("Price: $0.00"); // Reset to default
            return;
        }
        double price = fetchPrice(departure, arrival, priceRange); // Fetch price dynamically
        if (price != -1) {
            priceLabel.setText("Price: $" + price); // Display the price
        } else {
            priceLabel.setText("Price not available"); // Handle no price found
        }
    }


    public ReservationScreen(int userID) {
        USER_ID = userID;
        initComponents();
    }

    private void initComponents() {

        // initiating the GUI Components

        priceRangeButtonGroup = new javax.swing.ButtonGroup();
        tabbedPane = new javax.swing.JTabbedPane();
        //Price Label init
        priceLabel = new javax.swing.JLabel();
        createReservationPane = new javax.swing.JPanel();
        departureComboBox = new javax.swing.JComboBox<>();
        arrivalComboBox = new javax.swing.JComboBox<>();
        departureLabel = new javax.swing.JLabel();
        arrivalLabel = new javax.swing.JLabel();
        dateTextField = new javax.swing.JTextField();
        dateLabel = new javax.swing.JLabel();
        basicPriceRadioButton = new javax.swing.JRadioButton();
        economicPriceRadioButton = new javax.swing.JRadioButton();
        premiumPriceRadioButton = new javax.swing.JRadioButton();
        createReservationButton = new javax.swing.JButton();
        createdConfirmationLabel = new javax.swing.JLabel(); //no use, but all the UI get weird without it lol
        viewReservationPane = new javax.swing.JPanel();
        viewReservationsScrollPane = new javax.swing.JScrollPane();
        viewReservationsTable = new javax.swing.JTable();
        cancelReservationPane = new javax.swing.JPanel();
        cancelLabel = new javax.swing.JLabel();
        cancelComboBox = new javax.swing.JComboBox<>();
        cancelButton = new javax.swing.JButton();
        createReservationPane.add(priceLabel);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        // Getting values for reservation pane combo boxes
        List<String> destinationList = getDestinationList();
        String[] destinationArray = destinationList.toArray(new String[0]);
        List<String[]> reservationList = getReservationList();
        String[][] reservationArray = reservationList.toArray(new String[0][]); //Array for the table

        departureComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(destinationArray));
        departureComboBox.addActionListener(evt->updatePrice());//New Listener

        arrivalComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(destinationArray));
        arrivalComboBox.addActionListener(evt->updatePrice());

        departureLabel.setText("Select departure airport");

        arrivalLabel.setText("Select arrival airport");

        dateTextField.setText("YYYY/MM/DD");

        dateLabel.setText("Enter date");



        priceRangeButtonGroup.add(basicPriceRadioButton);
        basicPriceRadioButton.setText("Basic");
        basicPriceRadioButton.addActionListener(evt ->{ priceRange = "basic";updatePrice();} );// Addes update price method
        basicPriceRadioButton.setSelected(true);

        priceRangeButtonGroup.add(economicPriceRadioButton);
        economicPriceRadioButton.setText("Economic");
        economicPriceRadioButton.addActionListener(evt -> {priceRange = "economic";updatePrice();});//Added update price method

        priceRangeButtonGroup.add(premiumPriceRadioButton);
        premiumPriceRadioButton.setText("Premium");
        premiumPriceRadioButton.addActionListener(evt -> {priceRange = "premium"; updatePrice();});//Added update price method

        createdConfirmationLabel.setText(""); // not used

        createReservationButton.setText("Create Reservation");
        priceLabel = new JLabel("Price: $0.00"); // Default price display
        createReservationPane.add(priceLabel);  // Add it to the pane


        createReservationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                String departureAirport = departureComboBox.getSelectedItem().toString();
                String arrivalAirport = arrivalComboBox.getSelectedItem().toString();

                if (departureAirport.equals(arrivalAirport)) {
                    // shows error if both airports are the same.
                    showErrorDialog("Origin cannot be the same as destination");
                    return;
                }
                String date = dateTextField.getText();

                try {
                    LocalDate ld = LocalDate.parse(date, FORMATTER);
                    if (ld.isBefore(LocalDate.now())) {
                        // shows error if the date is before today's date.
                        showErrorDialog("Date cannot be in the past");
                        return;
                    }
                } catch (DateTimeParseException e) {
                    // shows error if date cannot be parsed to YYYY/MM/DD
                    showErrorDialog("Invalid date; enter a date in YYYY/MM/DD format");
                    return;
                }

                String reservationCode = generateRandomCode(); // random code for reservation (different from ID)
                createReservation(departureAirport, arrivalAirport, USER_ID, date, reservationCode, priceRange);
                updateReservationsTable(viewReservationsTable); //refreshes reservation table
                cancelComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(getCodeList())); //refreshes cancel comboBox
                showSuccessDialog("Reservation created successfully. Code: " + reservationCode);
            }
        });

        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                String flightCode = cancelComboBox.getSelectedItem().toString();
                if (flightCode.equals(" ")) {
                    // Shows error if code selected is empty
                    showErrorDialog("Please select a flight code");
                    return;
                }
                cancelFlight(flightCode);
                cancelComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(getCodeList())); //updates cancel comboBox
                updateReservationsTable(viewReservationsTable); //updates reservation table
                showSuccessDialog("Flight canceled successfully");
            }
        });

        javax.swing.GroupLayout createReservationPaneLayout = new javax.swing.GroupLayout(createReservationPane);
        createReservationPane.setLayout(createReservationPaneLayout);


        // All the formatting for the Reservation Pane

        createReservationPaneLayout.setHorizontalGroup(
                createReservationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(createReservationPaneLayout.createSequentialGroup()
                                .addGroup(createReservationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(createReservationPaneLayout.createSequentialGroup()
                                                .addGap(156, 156, 156)
                                                .addComponent(departureComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 83, Short.MAX_VALUE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, createReservationPaneLayout.createSequentialGroup()
                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(departureLabel)
                                                .addGap(63, 63, 63)))
                                .addGroup(createReservationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(arrivalComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(arrivalLabel))
                                .addGap(112, 112, 112))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, createReservationPaneLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(createReservationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(createReservationButton, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(premiumPriceRadioButton)
                                        .addComponent(economicPriceRadioButton)
                                        .addComponent(basicPriceRadioButton)

                                        .addComponent(dateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(createReservationPaneLayout.createSequentialGroup()
                                                .addGap(44, 44, 44)
                                                .addComponent(dateLabel))
                                        .addComponent(createdConfirmationLabel, javax.swing.GroupLayout.Alignment.CENTER)) // Added the label here
                                .addGap(189, 189, 189))
                        .addGroup(createReservationPaneLayout.createSequentialGroup()
                                .addGap(50, 50, 50)
                                .addComponent(priceLabel))
    );

        createReservationPaneLayout.setVerticalGroup(
                createReservationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(createReservationPaneLayout.createSequentialGroup()
                                .addGap(39, 39, 39)
                                .addGroup(createReservationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(departureLabel)
                                        .addComponent(arrivalLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(createReservationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(departureComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(arrivalComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(28, 28, 28)
                                .addComponent(dateLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addComponent(basicPriceRadioButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(economicPriceRadioButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(premiumPriceRadioButton)

                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                        .addGroup(createReservationPaneLayout.createSequentialGroup()
                                .addComponent(createReservationButton, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED) // Add space between button and label
                                .addComponent(createdConfirmationLabel) // Add the label here
                                .addGap(24, 24, 24))
                        .addComponent(priceLabel))
        );


        tabbedPane.addTab("Create Reservation", createReservationPane);


        viewReservationsTable.setModel(new javax.swing.table.DefaultTableModel(
                reservationArray,
                new String[]{
                        "Flight", "Date", "Price", "Code"
                }
        ));

        javax.swing.table.TableRowSorter<javax.swing.table.DefaultTableModel> sorter =
                new javax.swing.table.TableRowSorter<>((javax.swing.table.DefaultTableModel) viewReservationsTable.getModel());
        viewReservationsTable.setRowSorter(sorter);

        viewReservationsScrollPane.setViewportView(viewReservationsTable);

        javax.swing.GroupLayout viewReservationPaneLayout = new javax.swing.GroupLayout(viewReservationPane);
        viewReservationPane.setLayout(viewReservationPaneLayout);

        viewReservationPaneLayout.setHorizontalGroup(
                viewReservationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(viewReservationPaneLayout.createSequentialGroup()
                                .addGap(20)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(viewReservationPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(viewReservationsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
                                .addContainerGap())
        );

        viewReservationPaneLayout.setVerticalGroup(
                viewReservationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(viewReservationPaneLayout.createSequentialGroup()
                                .addGap(10)
                                .addGap(18)
                                .addComponent(viewReservationsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabbedPane.addTab("View Reservations", viewReservationPane);

        cancelLabel.setText("Select code from flight to cancel: ");

        cancelComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(getCodeList()));

        cancelButton.setText("Cancel Flight");

        javax.swing.GroupLayout cancelReservationPaneLayout = new javax.swing.GroupLayout(cancelReservationPane);
        cancelReservationPane.setLayout(cancelReservationPaneLayout);
        cancelReservationPaneLayout.setHorizontalGroup(
                cancelReservationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(cancelReservationPaneLayout.createSequentialGroup()
                                .addGroup(cancelReservationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(cancelReservationPaneLayout.createSequentialGroup()
                                                .addGap(209, 209, 209)
                                                .addComponent(cancelButton))
                                        .addGroup(cancelReservationPaneLayout.createSequentialGroup()
                                                .addGap(173, 173, 173)
                                                .addComponent(cancelComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cancelReservationPaneLayout.createSequentialGroup()
                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(cancelLabel)))
                                .addContainerGap(177, Short.MAX_VALUE))
        );
        cancelReservationPaneLayout.setVerticalGroup(
                cancelReservationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(cancelReservationPaneLayout.createSequentialGroup()
                                .addGap(62, 62, 62)
                                .addComponent(cancelLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cancelComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(63, 63, 63)
                                .addComponent(cancelButton)
                                .addContainerGap(182, Short.MAX_VALUE))
        );

        tabbedPane.addTab("Cancel Flight", cancelReservationPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(tabbedPane)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(tabbedPane)
        );

        pack();
    }// </editor-fold>

    private void arrivalComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void dateTextFieldActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ReservationScreen(1).setVisible(true); //test user
            }
        });
    }

    // Variables declaration - do not modify
    private javax.swing.JComboBox<String> arrivalComboBox;
    private javax.swing.JLabel arrivalLabel;
    private javax.swing.JRadioButton basicPriceRadioButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JComboBox<String> cancelComboBox;
    private javax.swing.JLabel cancelLabel;
    private javax.swing.JPanel cancelReservationPane;
    private javax.swing.JButton createReservationButton;
    private javax.swing.JPanel createReservationPane;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JTextField dateTextField;
    private javax.swing.JComboBox<String> departureComboBox;
    private javax.swing.JLabel departureLabel;
    private javax.swing.JRadioButton economicPriceRadioButton;
    private javax.swing.JRadioButton premiumPriceRadioButton;
    private javax.swing.ButtonGroup priceRangeButtonGroup;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JPanel viewReservationPane;
    private javax.swing.JScrollPane viewReservationsScrollPane;
    private javax.swing.JTable viewReservationsTable;
    private javax.swing.JLabel createdConfirmationLabel;
    private JComboBox<String> paymentMethodComboBox; // Dropdown for payment Method
    private JButton confirmPaymentButton;//Button to confirm payment
    private JLabel paymentStatusLabel;//Display payment status
    // End of swing variables declaration

    private String priceRange = "Basic";

    // Methods

    public static List<String> getDestinationList() {
        //returns a List object with all the destinations in the database

        List<String> destinations = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            String query = "SELECT DISTINCT departureAirport FROM Flights";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String destinationAirport = rs.getString("DepartureAirport");
                destinations.add(destinationAirport);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return destinations;
    }

    public static void createReservation(String departureAirport, String arrivalAirport,
                                         int userID, String date, String reservationCode, String priceRange) {
        //Getting the flight ID using the arrival airport and departureAirport

        int flightID = -1; //declaring flightID outside of Try block
        double price = 0;
        String selectedPriceRange;

        if (priceRange.equals("basic")) {
            selectedPriceRange = "basicPrice";
        } else if (priceRange.equals("economic")) {
            selectedPriceRange = "economicPrice";
        } else {
            selectedPriceRange = "premiumPrice";
        }

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            String flightQuery = "SELECT flightID, " + selectedPriceRange + " FROM Flights WHERE departureAirport = '"
                    + departureAirport + "' AND arrivalAirport = '" + arrivalAirport + "'";
            ResultSet rs = stmt.executeQuery(flightQuery);
            if (rs.next()) {
                flightID = rs.getInt("flightID");
                price = rs.getDouble(selectedPriceRange);
            } else {
                System.out.println("No flight found with the specified criteria.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String insertReservation = "INSERT INTO Reservation (FlightID, UserID, ReservationDate, ReservationCode, Price) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(insertReservation)) {
            pstmt.setInt(1, flightID);
            pstmt.setInt(2, USER_ID);
            pstmt.setString(3, date);
            pstmt.setString(4, reservationCode);
            pstmt.setDouble(5, price);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Reservation created succesfully, your reservation code is: " + reservationCode);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String generateRandomCode() {
        // Randomly generates a code to assign to each reservation

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";  // Alphanumeric characters
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }

    public static List<String[]> getReservationList() {
        //returns a List object with the info about the user's reservations

        List<String[]> reservations = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            String query = "SELECT f.departureAirport, f.arrivalAirport, r.Price, r.ReservationDate, r.ReservationCode" +
                    " FROM Reservation r " +
                    "JOIN Flights f ON r.FlightID = f.FlightID WHERE r.UserID = " + USER_ID + ";";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String flight = rs.getString("departureAirport") + " to "
                        + rs.getString("arrivalAirport");
                String date = rs.getString("ReservationDate");
                String price = rs.getString("Price");
                String code = rs.getString("ReservationCode");

                reservations.add(new String[]{flight, date, price, code});
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    public static String[] getCodeList(){
        //Returns a String array with the codes for all the user's reservations for the cancel Combo box

        List<String> codeList = new ArrayList<>();
        codeList.add(" ");
        try (Connection connn = DriverManager.getConnection(URL);
             Statement stmt = connn.createStatement()) {
            String query = "SELECT DISTINCT reservationCode FROM Reservation WHERE userID = " + USER_ID + ";";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String code = rs.getString("reservationCode");
                codeList.add(code);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return codeList.toArray(new String[0]);
    }

    public static void updateReservationsTable(JTable table) {
        // Refreshes the data at the view reservations pane.

        List<String[]> reservationList = getReservationList();
        String[][] data = reservationList.toArray(new String[0][]);
        String[] columnNames = {"Flight", "Date", "Price", "Code"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(data, columnNames);
        table.setModel(model);
        javax.swing.table.TableRowSorter<javax.swing.table.DefaultTableModel> sorter =
                new javax.swing.table.TableRowSorter<>((javax.swing.table.DefaultTableModel) table.getModel());
        table.setRowSorter(sorter);
        table.revalidate();
        table.repaint();
        System.out.println("Reservation Table updated"); // delete
    }

    public static void cancelFlight(String flightCode){
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()){
            String query = "DELETE FROM Reservation WHERE reservationCode = '" + flightCode + "';";
            stmt.executeUpdate(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(
                null,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
    public void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

}
