package ui;
import javax.swing.*;

public class MainMenuScreen extends javax.swing.JFrame {
    public MainMenuScreen() {
        initComponents();
    }
    private void initComponents() {
        JLabel mainLabel = new javax.swing.JLabel();
        JButton registerButton = new javax.swing.JButton();
        JButton logInButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(160, 115, 215));

        mainLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        mainLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mainLabel.setText("Welcome to The Airline Reservation System");

        registerButton.setText("Register");

        registerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registerButtonActionPerformed(evt);
            }
        });

        logInButton.setText("Log in");

        logInButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });

        //Horizontal group layout
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(49, 49, 49)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(registerButton)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(logInButton))
                                        .addComponent(mainLabel))
                                .addContainerGap(40, Short.MAX_VALUE))
        );

        // Vertical group layout

        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(mainLabel)
                                .addGap(86, 86, 86)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(registerButton)
                                        .addComponent(logInButton))
                                .addContainerGap(148, Short.MAX_VALUE))
        );

        pack();
    }

    private void registerButtonActionPerformed(java.awt.event.ActionEvent evt) {
        Main.openRegistrationScreen();
    }

    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {
        Main.openLoginScreen(this);
    }

}
