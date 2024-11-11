package Reservation;

import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class ReservationSystemTerminal {

    private static final String URL = "jdbc:sqlite:database/airlineReservation.db";

    public static void main(String[] args) {
        displayDestinations();

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the origin: ");
        String departureAirport = sc.nextLine();
        System.out.println("Enter the destination: ");
        String arrivalAirport = sc.nextLine();
        System.out.println("Enter the date (YYYY/MM/DD): ");
        String date = sc.nextLine();
        System.out.println("Enter your price range (basic, economic, premium): ");
        String price = sc.nextLine();
        String reservationCode = generateRandomCode();
        sc.close();

        //userID will be set to 1 for testing.
        createReservation(departureAirport, arrivalAirport, 1, date, reservationCode, price);

        displayReservation(reservationCode);
    }

    public static void displayDestinations() {
        System.out.println("Available destinations: ");
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            String query = "SELECT DISTINCT departureAirport FROM Flights";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String destinationAirport = rs.getString("DepartureAirport");
                System.out.print(destinationAirport + ", ");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println();
    }

    public static void createReservation(String departureAirport, String arrivalAirport,
                                           int userID, String date, String reservationCode, String priceRange) {
        //Getting the flight ID using the arrival airport and departureAirport
        int flightID = -1; //declaring flightID outside of Try block
        double price = 0;
        String selectedPriceRange;

        if (priceRange.equals("basic")) {
            selectedPriceRange = "basicPrice";
        }
        else if (priceRange.equals("economic")) {
            selectedPriceRange = "economicPrice";
        }
        else{
            selectedPriceRange = "premiumPrice";
        }

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            String flightQuery = "SELECT flightID, " + selectedPriceRange  + " FROM Flights WHERE departureAirport = '"
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
            pstmt.setInt(2, 1); // The UserID will temporarily be 1
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
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";  // Alphanumeric characters
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }

    public static void displayReservation(String reservationCode) {
        try(Connection conn = DriverManager.getConnection(URL);
        Statement stmt = conn.createStatement()) {
            // Getting origin and destination
            String query = "SELECT * FROM Reservation WHERE ReservationCode = '" + reservationCode + "'";
            ResultSet reservationResultSet = stmt.executeQuery(query);
            int flightID = reservationResultSet.getInt("flightID");
            double price = reservationResultSet.getDouble("Price");
            ResultSet airportResultSet = stmt.executeQuery("SELECT departureAirport, arrivalAirport FROM Flights WHERE FlightID = '"
                    + flightID + "'");
            String departureAirport = airportResultSet.getString("departureAirport");
            String arrivalAirport = airportResultSet.getString("arrivalAirport");
            System.out.println("Origin: " + departureAirport + ", Destination: " + arrivalAirport +
                    " Price: " + price);
            }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}