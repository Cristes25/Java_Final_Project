package ui;
import database.DatabaseInitializer;

import javax.swing.*;
import javax.swing.SwingUtilities;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


public class RegistrationScreen extends JFrame{
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JPasswordField passwordField;
    private JButton registerButton;
    private JLabel messageLabel;

    //constructor to set the ui screen
    public RegistrationScreen(){
        setTitle("User Registration "); //Window Layout
        setSize(450, 320);// window size
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // centers window
        setLayout(new BorderLayout());

        //panel to hold form fields and labels
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4,2,10,10));

        //form fields
        JLabel firstNameLabel = new JLabel("First Name");
        firstNameField = new JTextField();

        JLabel lastNameLabel= new JLabel("Last Name");
        lastNameField= new JTextField();

        JLabel passwordLabel = new JLabel("Password ");
        passwordField= new JPasswordField();

        //Register Button
        registerButton= new JButton("Register"); // Button to cause registration
        messageLabel = new JLabel("", JLabel.CENTER); // shows message to the user

        //Add components to the panel
        panel.add(firstNameLabel);
        panel.add(firstNameField);
        panel.add(lastNameLabel);
        panel.add(lastNameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel());
        panel.add(registerButton);

        add(panel, BorderLayout.CENTER);
        add(messageLabel, BorderLayout.SOUTH);

        //Action Listener for the register button
        registerButton.addActionListener (new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                handleRegistration(); //calls the handleRegistration method when the button is clicked
            }

        });
        setVisible(true);

    }
    //method for registration process
    private void handleRegistration(){
        String firstName=firstNameField.getText();
        String lastName= lastNameField.getText();
        char[]passwordChars=passwordField.getPassword();
        String password = new String(passwordChars);

        //Check that all spaces contain information
        if (firstName.isEmpty()|| lastName.isEmpty() || password.isEmpty()){
            messageLabel.setText("Please fill all required fields!");
            return;
        }
        if (registerUser(firstName, lastName, password)){
            messageLabel.setText("Successful Registration");
             //If registration is successful, clear the fields
            firstNameField.setText("");
            lastNameField.setText("");
            passwordField.setText("");
        }else {
            messageLabel.setText("Registration failed. Please try again.");
        }
    }
    //Method to register in the database
    private boolean registerUser (String firstName, String lastName, String password ){
        boolean isSuccess= false;
        String dbURL="jdbc:sqlite:database/airlineReservation.db"; //Path to SQlite database
        try(Connection connect = DriverManager.getConnection (dbURL)) {
            //if connection is established
            if (connect != null){
                String query="INSERT INTO Users (LName, FName, password) VALUES (?,?,?)";
                try (PreparedStatement pst= connect.prepareStatement(query)){
                    pst.setString(1, lastName);
                    pst.setString(2, firstName);
                    pst.setString(3,password);

                    //execute the query
                    int rowsAffected= pst.executeUpdate();

                    if (rowsAffected>0){
                        isSuccess=true; //Success if a row was inserted
                    }
                } catch (SQLException e){
                    //fail message
                    messageLabel.setText("Error during registration" +e.getMessage());
                    e.printStackTrace();
                }
            }else {
                messageLabel.setText("Failed to connect to the database");
            }

        }catch (SQLException e){
            //Error message if connection fails
            messageLabel.setText("Database connection Error " +e.getMessage());
            e.printStackTrace();
        }
        return isSuccess;
    }
    public static void main(String[] args){
        new RegistrationScreen();
    }


}