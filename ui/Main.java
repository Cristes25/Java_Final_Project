package ui;

public class Main {
    public static void main(String[] args) {
        showMainMenu();
    }

    public static void showMainMenu() {
        MainMenuScreen mainMenu = new MainMenuScreen();
        mainMenu.setVisible(true);
    }

    public static void openRegistrationScreen(){
        RegistrationScreen registrationScreen = new RegistrationScreen();
        registrationScreen.setVisible(true);
    }

    public static void openLoginScreen(MainMenuScreen mainMenuScreen){
        LoginScreen loginScreen = new LoginScreen(mainMenuScreen);
        loginScreen.setVisible(true);
    }

    public static void openReservationScreen(int userID){
        ReservationScreen reservationScreen = new ReservationScreen(userID);
        reservationScreen.setVisible(true);
    }
}
