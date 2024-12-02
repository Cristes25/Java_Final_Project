package ui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import static ui.ReservationScreen.showErrorDialog;


public class LoginScreen extends JFrame{
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel messageLabel;
    private int loggedUser;
    private MainMenuScreen mainMenuScreen; // passing a reference to the main menu to close it later.

    public LoginScreen(MainMenuScreen mainMenuScreen) {
        this.mainMenuScreen = mainMenuScreen;
        setTitle(" USER LOGIN");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        //UI Components
        usernameField= new JTextField(15);
        passwordField= new JPasswordField(15);
        loginButton= new JButton("Login");

        //UI components in a field
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3,2,10,10));

        JLabel usernameLabel= new JLabel("Username");
        usernameField= new JTextField();



        JLabel passwordLabel= new JLabel("Password: ");
        passwordField= new JPasswordField();

        loginButton = new JButton("Login:");
        messageLabel= new JLabel();

        usernameField =new JTextField();

        //Panel
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel());
        panel.add(loginButton);

        //Panel to JFrame
        add (panel,BorderLayout.CENTER);
        add(messageLabel,BorderLayout.SOUTH);

        //Button Action for loginButton
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }

        });
        setVisible(true);
    }
    private void handleLogin(){
        String username= usernameField.getText();

        char [] password= passwordField.getPassword();
        String passwordText= new String(password);

        int userID = validateLogin(username, passwordText);

        if (userID > 0) {
            this.dispose();
            JOptionPane.showMessageDialog(null, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            mainMenuScreen.dispose();
            Main.openReservationScreen(userID);
        }
        else {
            messageLabel.setText("Login failed. Invalid Username or Password ");
        }
    }

    private int validateLogin(String username, String password){
        int userID = -1;
        //Database connection SQLite
        String dbUrl="jdbc:sqlite:database/airlinereservation.db";

        String[] nameParts = username.split(" ", 2);

        if (nameParts.length < 2) {
            showErrorDialog("Please enter both First Name and Last Name separated by a space.");
            return userID;
        }

        String firstName = nameParts[0];
        String lastName = nameParts[1];

        try (Connection connection = DriverManager.getConnection(dbUrl)) {
            String query = "SELECT userID FROM Users WHERE FName=? AND LName=? AND password=?";
            try (PreparedStatement pst = connection.prepareStatement(query)) {
                pst.setString(1, firstName);
                pst.setString(2, lastName);
                pst.setString(3, password);

                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        userID = rs.getInt("userID");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                messageLabel.setText("Error checking credentials");
                e.printStackTrace();
            }
        } catch (SQLException e) {
            messageLabel.setText("Error connecting to the database");
            e.printStackTrace();
        }
        return userID;
    }

    public void setLoggedUser(int loggedUser) {
        this.loggedUser = loggedUser;
    }

    public int getLoggedUser() {
        return loggedUser;
    }

    //Main
    public static void main(String[] args){
        new LoginScreen(new MainMenuScreen());
    }

}
