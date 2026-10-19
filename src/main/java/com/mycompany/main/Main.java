package com.mycompany.main;

import java.util.Scanner;

// Console app: register, log in, then send messages through the QuickChat menu
public class Main {

    static Scanner input = new Scanner(System.in);

    // Reads a whole number, asks again if the user types anything else
    public static int readNumber(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = input.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    // Registration: asks for each detail and repeats it until it is valid
    public static void register(Login login) {
        System.out.print("Enter your first name: ");
        String firstName = input.nextLine();
        System.out.print("Enter your last name: ");
        String lastName = input.nextLine();

        // Loop for the username
        String username;
        while (true) {
            System.out.print("Enter your username: ");
            username = input.nextLine();
            if (Login.checkUserName(username)) {
                break;
            }
            System.out.println(Login.USERNAME_BAD);
        }

        // Loop for the password
        String password;
        while (true) {
            System.out.print("Enter your password: ");
            password = input.nextLine();
            if (Login.checkPasswordComplexity(password)) {
                break;
            }
            System.out.println(Login.PASSWORD_BAD);
        }

        // Loop for the cellphone number
        String cellphone;
        while (true) {
            System.out.print("Enter your cellphone number (+27): ");
            cellphone = input.nextLine();
            if (Login.checkCellPhoneNumber(cellphone)) {
                break;
            }
            System.out.println(Login.CELL_BAD);
        }

        // All details are valid, so register the user
        System.out.println(login.registerUser(firstName, lastName, username, password, cellphone));
    }

    // Login: three attempts, returns true if the user got in
    public static boolean logIn(Login login) {
        System.out.println("\n--- Login ---");
        for (int attempt = 1; attempt <= 3; attempt++) {
            System.out.print("Enter username to login: ");
            String username = input.nextLine();
            System.out.print("Enter password to login: ");
            String password = input.nextLine();

            boolean loginSuccessful = login.loginUser(username, password);
            System.out.println(login.returnLoginStatus(loginSuccessful));
            if (loginSuccessful) {
                return true;
            }
        }
        System.out.println("Too many failed attempts.");
        return false;
    }

    // Asks for the recipient until the number is valid
    public static String askRecipient() {
        while (true) {
            System.out.print("Enter the recipient's cell number: ");
            String recipient = input.nextLine().trim();
            String result = Message.checkRecipientCell(recipient);
            System.out.println(result);
            if (result.equals(Login.CELL_OK)) {
                return recipient;
            }
        }
    }

    // Asks for the message text until it is not empty and not too long
    public static String askMessageText() {
        while (true) {
            System.out.print("Enter your message: ");
            String text = input.nextLine();
            if (text.trim().isEmpty()) {
                System.out.println("The message cannot be empty.");
                continue;
            }
            String result = Message.checkMessageLength(text);
            System.out.println(result);
            if (text.length() <= Message.MAX_LENGTH) {
                return text;
            }
        }
    }

    // Loops once for each message the user said they would enter
    public static void sendMessages(int numberOfMessages) {
        for (int i = 0; i < numberOfMessages; i++) {
            System.out.println("\n--- Message " + (i + 1) + " of " + numberOfMessages + " ---");

            String recipient = askRecipient();
            String text = askMessageText();

            Message message = new Message(Message.generateMessageID(), i, recipient, text);
            if (!message.checkMessageID()) {
                System.out.println("The message ID is not valid, this message was skipped.");
                continue;
            }
            System.out.println("Message ID generated: " + message.getMessageID());

            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            int choice = readNumber("Choose an option: ");
            while (choice < 1 || choice > 3) {
                choice = readNumber("Please choose 1, 2 or 3: ");
            }

            System.out.println(message.sentMessage(choice));

            if (choice == Message.SEND) {
                System.out.println(message.getDetails());
            } else if (choice == Message.DISREGARD) {
                // Keep asking until the user confirms the delete with 0
                int confirm = readNumber("");
                while (confirm != 0) {
                    System.out.println("Press 0 to delete the message.");
                    confirm = readNumber("");
                }
                System.out.println("Message deleted.");
            }
        }
        System.out.println("\nTotal messages sent: " + Message.returnTotalMessages());
    }

    public static void main(String[] args) {
        Login login = new Login();
        register(login);

        // Messages can only be sent after a successful login
        if (!logIn(login)) {
            return;
        }

        System.out.println("\nWelcome to QuickChat.");

        int numberOfMessages = readNumber("How many messages would you like to enter? ");
        while (numberOfMessages < 1) {
            numberOfMessages = readNumber("Please enter at least 1 message: ");
        }
        boolean messagesEntered = false;

        int option = 0;
        while (option != 3) {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            option = readNumber("Choose an option: ");

            if (option == 1) {
                if (messagesEntered) {
                    System.out.println("You have already entered all " + numberOfMessages + " messages.");
                } else {
                    sendMessages(numberOfMessages);
                    messagesEntered = true;
                }
            } else if (option == 2) {
                System.out.println("Coming Soon.");
            } else if (option == 3) {
                System.out.println("Goodbye.");
            } else {
                System.out.println("Please choose 1, 2 or 3.");
            }
        }
    }
}
