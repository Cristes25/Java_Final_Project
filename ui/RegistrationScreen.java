package ui;
import database.DatabaseConnection;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;

    public RegisterScreen() {
        setTitle("Register");
        setSize(300, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create UI components
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        confirmPasswordField = new JPasswordField(15);
        registerButton = new JButton("Register");

        // Arrange UI components in a panel
        JPanel panel = new JPanel();
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmPasswordField);
        panel.add(registerButton);

        add(panel);

        // Add button action listener
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(null, "Passwords do not match.");
                } else if (registerUser(username, password)) {
                    JOptionPane.showMessageDialog(null, "Registration successful!");
                } else {
                    JOptionPane.showMessageDialog(null, "Username already exists.");
                }
            }
        });
    }

    private boolean registerUser(String username, String password) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
             PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")) {

            // Check if username exists
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return false; // Username already taken
            }

            // Insert new user
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        new RegisterScreen().setVisible(true);
    }
}
