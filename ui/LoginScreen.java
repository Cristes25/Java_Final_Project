package ui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;



public class LoginScreen extends JFrame{
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel messageLabel;

    public LoginScreen() {
        setTitle(" USER LOGIN");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

        if (validateLogin(username,passwordText)) {
            messageLabel.setText("Login was successful: ");
        }
        else {
            messageLabel.setText("Login failed. Invalid Username or Password ");

        }
    }
    private boolean validateLogin(String username, String password){
        boolean isValid=false;
        //Database connection SQLite
        String dbUrl="jdbc:sqlite:database/airlinereservation.db";
        try(Connection connection = DriverManager.getConnection(dbUrl)){
            //Modified query to concatenate FName and LName
            String query= "SELECT * FROM Users WHERE (FName || LName)=? AND password=?";
            try(PreparedStatement pst = connection.prepareStatement(query)){
                pst.setString(1, username);
                pst.setString(2, password);
                ResultSet rs= pst.executeQuery();

                if (rs.next()){
                    isValid= true;
                }

            }
            catch (SQLException e){
                messageLabel.setText("Error checking credentials");
                e.printStackTrace();
            }
        } catch (SQLException e) {
            messageLabel.setText("Error connecting to the database");
            e.printStackTrace();
        }
        return isValid;
    }
    //Main
    public static void main(String[] args){
        new LoginScreen();
    }

}
