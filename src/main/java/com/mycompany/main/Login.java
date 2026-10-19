package com.mycompany.main;

// Handles registration and login (Part 1)
public class Login {

    // Messages shown to the user
    public static final String USERNAME_OK = "Username successfully captured.";
    public static final String USERNAME_BAD = "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
    public static final String PASSWORD_OK = "Password successfully captured.";
    public static final String PASSWORD_BAD = "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
    public static final String CELL_OK = "Cell phone number successfully captured.";
    public static final String CELL_BAD = "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    public static final String REGISTER_OK = "User registered successfully.";
    public static final String LOGIN_BAD = "Username or password incorrect, please try again.";

    // Details saved when the user registers
    private String firstName;
    private String lastName;
    private String registeredUsername;
    private String registeredPassword;
    private String registeredCellphone;
    private boolean registered = false;

    // Username must have an underscore and be no more than 5 characters
    public static boolean checkUserName(String username) {
        return username != null && username.length() <= 5 && username.contains("_");
    }

    // Password needs 8+ characters, a capital, a lowercase, a number and a special character
    public static boolean checkPasswordComplexity(String password) {
        return password != null
                && password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*[0-9].*")
                && password.matches(".*[^a-zA-Z0-9].*");
    }

    // Cell number must start with +27 followed by exactly 9 digits
    // TODO: add your Harvard reference for the regex here (the PoE says it must be attributed)
    public static boolean checkCellPhoneNumber(String number) {
        return number != null && number.matches("^\\+27[0-9]{9}$");
    }

    // Message for the username check
    public static String usernameMessage(String username) {
        return checkUserName(username) ? USERNAME_OK : USERNAME_BAD;
    }

    // Message for the password check
    public static String passwordMessage(String password) {
        return checkPasswordComplexity(password) ? PASSWORD_OK : PASSWORD_BAD;
    }

    // Message for the cell number check
    public static String cellPhoneMessage(String number) {
        return checkCellPhoneNumber(number) ? CELL_OK : CELL_BAD;
    }

    // Checks every detail, saves them if they are all valid and returns the messages to display
    public String registerUser(String firstName, String lastName, String username,
                               String password, String cellphone) {
        String result = usernameMessage(username) + "\n"
                + passwordMessage(password) + "\n"
                + cellPhoneMessage(cellphone);

        if (checkUserName(username) && checkPasswordComplexity(password)
                && checkCellPhoneNumber(cellphone)) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.registeredUsername = username;
            this.registeredPassword = password;
            this.registeredCellphone = cellphone;
            this.registered = true;
            result = result + "\n" + REGISTER_OK;
        }
        return result;
    }

    // True when the entered details match the registered details
    public boolean loginUser(String loginUsername, String loginPassword) {
        return registered
                && registeredUsername.equals(loginUsername)
                && registeredPassword.equals(loginPassword);
    }

    // Message shown after a login attempt
    public String returnLoginStatus(boolean loginSuccessful) {
        if (loginSuccessful) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
        }
        return LOGIN_BAD;
    }

    public boolean isRegistered() {
        return registered;
    }
}
