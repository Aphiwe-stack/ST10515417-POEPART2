package com.mycompany.main;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class MessageTest {

    @TempDir
    Path tempFolder;

    // Test data from the PoE
    private final String text1 = "Hi Mike, can you join us for dinner tonight?";
    private final String text2 = "Hi Keegan, did you receive the payment?";

    private Message message1;
    private Message message2;

    // Fresh lists and a temporary JSON file before every test
    @BeforeEach
    public void setUp() {
        Message.resetMessages(tempFolder.resolve("storedMessages.json").toString());
        message1 = new Message("0012345678", 0, "+27718693002", text1);
        message2 = new Message("0098765432", 1, "08575975889", text2);
    }

    @Test
    public void messageLengthSuccess() {
        assertEquals("Message ready to send.", Message.checkMessageLength(text1));
    }

    @Test
    public void messageLengthExactly250IsAllowed() {
        assertEquals("Message ready to send.", Message.checkMessageLength("a".repeat(250)));
    }

    @Test
    public void messageLengthFailure() {
        String tooLong = "a".repeat(260);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                Message.checkMessageLength(tooLong));
    }

    @Test
    public void recipientCorrectlyFormatted() {
        assertEquals("Cell phone number successfully captured.",
                Message.checkRecipientCell(message1.getRecipient()));
    }

    @Test
    public void recipientIncorrectlyFormatted() {
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
                Message.checkRecipientCell(message2.getRecipient()));
    }

    @Test
    public void messageHashForFirstMessage() {
        assertEquals("00:0:HITONIGHT", message1.createMessageHash());
        assertEquals("00:0:HITONIGHT", message1.getMessageHash());
    }

    @Test
    public void messageHashForRemainingMessages() {
        Message[] messages = {message1, message2};
        String[] expected = {"00:0:HITONIGHT", "00:1:HIPAYMENT"};
        for (int i = 0; i < messages.length; i++) {
            assertEquals(expected[i], messages[i].createMessageHash());
        }
    }

    @Test
    public void messageHashWithOneWord() {
        Message oneWord = new Message("1234567890", 2, "+27718693002", "Thanks!");
        assertEquals("12:2:THANKSTHANKS", oneWord.createMessageHash());
    }

    @Test
    public void generatedMessageIDIsValid() {
        for (int i = 0; i < 50; i++) {
            String id = Message.generateMessageID();
            assertEquals(10, id.length());
            assertTrue(id.matches("[0-9]{10}"));
        }
        Message message = new Message(Message.generateMessageID(), 0, "+27718693002", text1);
        assertTrue(message.checkMessageID());
    }

    @Test
    public void messageIDTooLongIsRejected() {
        Message longId = new Message("12345678901", 0, "+27718693002", text1);
        assertFalse(longId.checkMessageID());
    }

    @Test
    public void sendMessage() {
        assertEquals("Message successfully sent.", message1.sentMessage(Message.SEND));
        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    public void disregardMessage() {
        assertEquals("Press 0 to delete the message.", message2.sentMessage(Message.DISREGARD));
        assertEquals(0, Message.returnTotalMessages());
    }

    @Test
    public void storeMessageWritesJsonFile() throws IOException {
        assertEquals("Message successfully stored.", message1.sentMessage(Message.STORE));
        assertEquals(1, Message.getStoredCount());
        assertEquals(0, Message.returnTotalMessages());

        String json = Files.readString(tempFolder.resolve("storedMessages.json"));
        assertTrue(json.contains("\"messageID\": \"0012345678\""));
        assertTrue(json.contains("\"recipient\": \"+27718693002\""));
        assertTrue(json.contains("\"messageHash\": \"00:0:HITONIGHT\""));
    }

    @Test
    public void invalidSendOption() {
        assertEquals("Invalid option, please choose 1, 2 or 3.", message1.sentMessage(9));
    }

    @Test
    public void totalMessagesAfterPoeTestData() {
        message1.sentMessage(Message.SEND);
        message2.sentMessage(Message.DISREGARD);
        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    public void printMessagesShowsDetailsInOrder() {
        message1.sentMessage(Message.SEND);
        String expected = "Message ID: 0012345678\n"
                + "Message Hash: 00:0:HITONIGHT\n"
                + "Recipient: +27718693002\n"
                + "Message: " + text1 + "\n\n";
        assertEquals(expected, Message.printMessages());
    }
}
