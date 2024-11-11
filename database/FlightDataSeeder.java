package database;

import java.sql.*;
import java.sql.Connection;


public class FlightDataSeeder {

    private static final String URL = "jdbc:sqlite:database/airlineReservation.db";
    public static void main(String[] args) {
        clearTableData();

        String insertSQL = "INSERT INTO Flights (departureAirport, arrivalAirport, departureTime, arrivalTime, basicPrice, economicPrice, premiumPrice) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL)){
            PreparedStatement pstmt = conn.prepareStatement(insertSQL);{
                if (conn != null) {
                    System.out.println("Connection established");

                    String[][] flightData = {
                            {"MGA", "SJO", "08:00:00", "11:00:00", "100", "150", "200"},
                            {"SJO", "MGA", "17:00:00", "19:00:00", "100", "150", "200"},
                            {"MGA", "SAL", "06:30:00", "07:30:00", "130", "180", "230"},
                            {"SAL", "MGA", "15:00:00", "16:00:00", "130", "180", "230"},
                            {"MGA", "MIA", "10:00:00", "12:30:00", "300", "350", "400"},
                            {"MIA", "MGA", "14:00:00", "16:30:00", "300", "350", "400"},
                            {"SJO", "SAL", "09:00:00", "10:00:00", "120", "170", "220"},
                            {"SAL", "SJO", "16:00:00", "17:00:00", "120", "170", "220"},
                            {"SJO", "MIA", "11:00:00", "14:00:00", "250", "300", "350"},
                            {"MIA", "SJO", "18:00:00", "21:00:00", "250", "300", "350"},
                            {"SAL", "MIA", "07:00:00", "10:00:00", "270", "320", "370"},
                            {"MIA", "SAL", "13:00:00", "16:00:00", "270", "320", "370"}
                    };

                    for (String[] flight : flightData) {
                        pstmt.setString(1, (String) flight[0]); //departureAirport
                        pstmt.setString(2, (String) flight[1]); //arrivalAirport
                        pstmt.setString(3, (String) flight[2]); //departureTime
                        pstmt.setString(4, (String) flight[3]); //arrivalTime
                        pstmt.setFloat(5, Float.parseFloat(flight[4])); //basicPrice
                        pstmt.setFloat(6, Float.parseFloat(flight[5])); //economicPrice
                        pstmt.setFloat(7, Float.parseFloat(flight[6])); //premiumPrice
                        pstmt.addBatch(); // Add to batch

                    }
                    int[] rowsAffected = pstmt.executeBatch(); //Executes all inserts at once.
                    System.out.println(rowsAffected.length + " rows inserted successfully.");
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        viewTableData();
    }

    public static void clearTableData() {
        String deleteSQL = "DELETE FROM Flights";
        String resetSequenceSQL = "DELETE FROM sqlite_sequence WHERE name='Flights'";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL);
             PreparedStatement resetStmt = conn.prepareStatement(resetSequenceSQL))
        {
            if (conn != null) {
                int rowsDeleted = pstmt.executeUpdate();
                System.out.println("Flights table cleared. Rows affected: " + rowsDeleted);

                resetStmt.executeUpdate();
                System.out.println("ID sequence reset.");
            }
        } catch (SQLException e) {
            System.out.println("Error: Unable to clear the table.");
            e.printStackTrace();
        }
    }
    public static void viewTableData() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            String query = "SELECT * FROM Flights";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()){
                int flightID = rs.getInt("FlightID");
                String departureAirport = rs.getString("DepartureAirport");
                String arrivalAirport = rs.getString("ArrivalAirport");

                System.out.println("Flight ID: " + flightID + ", Departure airport: " + departureAirport +
                        ", Arrival airport: " + arrivalAirport);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
