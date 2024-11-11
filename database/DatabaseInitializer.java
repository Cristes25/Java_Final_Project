package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseInitializer {
    private static final String URL = "jdbc:sqlite:database/airlineReservation.db";

    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            String usersTable = "CREATE TABLE IF NOT EXISTS Users (" +
                    "userID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "LName TEXT NOT NULL, " +
                    "FName TEXT NOT NULL, " +
                    "password TEXT NOT NULL);";

            String flightsTable = "CREATE TABLE IF NOT EXISTS Flights (" +
                    "FlightID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "departureAirport TEXT NOT NULL, " +
                    "arrivalAirport TEXT NOT NULL, " +
                    "departureTime TEXT NOT NULL, " +  // TIME as TEXT in SQLite
                    "arrivalTime TEXT NOT NULL, " +   // TIME as TEXT in SQLite
                    "basicPrice REAL NOT NULL, " +
                    "economicPrice REAL NOT NULL, " +
                    "premiumPrice REAL NOT NULL);";

            String reservationTable = "CREATE TABLE IF NOT EXISTS Reservation (" +
                    "ReservationID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "FlightID INT NOT NULL, " +
                    "UserID INT NOT NULL, " +
                    "ReservationDate TEXT NOT NULL, " + // DATETIME as TEXT in SQLite
                    "ReservationCode TEXT NOT NULL, " +
                    "Price REAL NOT NULL, " +
                    "FOREIGN KEY (FlightID) REFERENCES Flights(FlightID), " +
                    "FOREIGN KEY (UserID) REFERENCES Users(userID));";



            stmt.execute(usersTable);
            stmt.execute(flightsTable);
            stmt.execute(reservationTable);

            System.out.println("Database and tables created successfully.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        initializeDatabase();
    }
}


