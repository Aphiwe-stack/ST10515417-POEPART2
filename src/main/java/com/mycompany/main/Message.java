package com.mycompany.main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

// Holds one chat message and the lists of sent and stored messages (Part 2)
public class Message {

    public static final int MAX_LENGTH = 250;

    // Options for what to do with a finished message
    public static final int SEND = 1;
    public static final int DISREGARD = 2;
    public static final int STORE = 3;

    // Every message sent or stored while the program runs
    private static ArrayList<Message> sentMessages = new ArrayList<>();
    private static ArrayList<Message> storedMessages = new ArrayList<>();

    // File the stored messages are written to
    private static String storageFile = "storedMessages.json";

    private String messageID;
    private int messageNumber;
    private String recipient;
    private String messageText;
    private String messageHash;

    public Message(String messageID, int messageNumber, String recipient, String messageText) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = createMessageHash();
    }

    // Makes a random ten digit ID, built one digit at a time so leading zeros are kept
    public static String generateMessageID() {
        Random random = new Random();
        String id = "";
        for (int i = 0; i < 10; i++) {
            id = id + random.nextInt(10);
        }
        return id;
    }

    // ID must be digits only and no more than ten characters
    public boolean checkMessageID() {
        return messageID != null && messageID.length() > 0
                && messageID.length() <= 10 && messageID.matches("[0-9]+");
    }

    // Recipient uses the same rules as the registered cell number
    public static String checkRecipientCell(String recipient) {
        if (Login.checkCellPhoneNumber(recipient)) {
            return Login.CELL_OK;
        }
        return Login.CELL_BAD;
    }

    // Message may not be longer than 250 characters
    public static String checkMessageLength(String text) {
        if (text.length() <= MAX_LENGTH) {
            return "Message ready to send.";
        }
        int extra = text.length() - MAX_LENGTH;
        return "Message exceeds 250 characters by " + extra + "; please reduce the size.";
    }

    // First two digits of the ID : message number : first word + last word, all in capitals
    public String createMessageHash() {
        String[] words = messageText.trim().split("\\s+");
        String first = words[0].replaceAll("[^A-Za-z0-9]", "");
        String last = words[words.length - 1].replaceAll("[^A-Za-z0-9]", "");
        String start = messageID.substring(0, Math.min(2, messageID.length()));
        return (start + ":" + messageNumber + ":" + first + last).toUpperCase();
    }

    // Send, disregard or store the message depending on the user's choice
    public String sentMessage(int choice) {
        if (choice == SEND) {
            sentMessages.add(this);
            return "Message successfully sent.";
        } else if (choice == DISREGARD) {
            return "Press 0 to delete the message.";
        } else if (choice == STORE) {
            storedMessages.add(this);
            if (storeMessage()) {
                return "Message successfully stored.";
            }
            return "Message could not be saved to the file.";
        }
        return "Invalid option, please choose 1, 2 or 3.";
    }

    // Full details of this message in the order the PoE asks for
    public String getDetails() {
        return "Message ID: " + messageID + "\n"
                + "Message Hash: " + messageHash + "\n"
                + "Recipient: " + recipient + "\n"
                + "Message: " + messageText;
    }

    // Details of every message sent so far
    public static String printMessages() {
        String output = "";
        for (Message message : sentMessages) {
            output = output + message.getDetails() + "\n\n";
        }
        return output;
    }

    // How many messages have been sent
    public static int returnTotalMessages() {
        return sentMessages.size();
    }

    // Writes all stored messages to the JSON file
    // TODO: add your reference for the JSON writing code here (the PoE says it must be attributed)
    public boolean storeMessage() {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < storedMessages.size(); i++) {
            Message m = storedMessages.get(i);
            json.append("  {\n");
            json.append("    \"messageID\": \"").append(escapeJson(m.messageID)).append("\",\n");
            json.append("    \"messageNumber\": ").append(m.messageNumber).append(",\n");
            json.append("    \"recipient\": \"").append(escapeJson(m.recipient)).append("\",\n");
            json.append("    \"message\": \"").append(escapeJson(m.messageText)).append("\",\n");
            json.append("    \"messageHash\": \"").append(escapeJson(m.messageHash)).append("\"\n");
            json.append("  }");
            if (i < storedMessages.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("]\n");

        try (FileWriter writer = new FileWriter(storageFile)) {
            writer.write(json.toString());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // Escapes characters that would break a JSON string
    private static String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // Used by the unit tests to start each test with empty lists and a test file
    public static void resetMessages(String testFile) {
        sentMessages.clear();
        storedMessages.clear();
        storageFile = testFile;
    }

    public String getMessageID() {
        return messageID;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public static int getStoredCount() {
        return storedMessages.size();
    }
}
