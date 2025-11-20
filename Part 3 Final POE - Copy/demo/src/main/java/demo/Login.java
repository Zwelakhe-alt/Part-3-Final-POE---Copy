package demo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

/**
 * QuickChat - Integrated Application (Parts 1–3) Author: Your name and ST
 * number Program demonstrates registration, login, message sending, and Part 3
 * management functions (arrays, search, delete, JSON read, reports).
 */
public class Login {

    //=================//
    // USER CREDENTIALS
    //=================//
    private static String regFirstName;
    private static String regLastName;
    private static String regCell;
    private static String regUsername;
    private static String regPassword;

    //=================//
    // MESSAGE STORAGE
    //=================//
    private static final List<Message> sentMessages = new ArrayList<>();
    private static final List<Message> disregardedMessages = new ArrayList<>();
    private static final List<Message> storedMessages = new ArrayList<>();

    private static final List<String> messageIds = new ArrayList<>();
    private static final List<String> messageHashes = new ArrayList<>();

    //=================//
    // START (used by Main)
    //=================//
    public static void start() {
        JOptionPane.showMessageDialog(null, "Welcome to QuickChat");

        while (true) {
            Object[] options = {
                "Register",
                "Login",
                "Send Message",
                "Reports & Management",
                "Quit"
            };
            int selection = JOptionPane.showOptionDialog(
                    null,
                    "Select one",
                    "QuickChat Menu",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            switch (selection) {
    case 0 ->
        handleRegister();
    case 1 ->
        handleLogin();
    case 2 ->
        sendMessages();
    case 3 ->
        openManagementMenu();     // "Coming soon" now opens Management Menu
    case 4, JOptionPane.CLOSED_OPTION -> { 
        JOptionPane.showMessageDialog(null, "Goodbye!");
        System.exit(0);           // Quit now closes the whole app
    }
    default ->
        JOptionPane.showMessageDialog(null, "No option selected");
        }
     }
    }

    // Backwards-compatible main so users can Run `Login` directly in the IDE
    public static void main(String[] args) {
        start();
    }

    //=================//
    // REGISTRATION
    //=================//
    private static void handleRegister() {
        String firstName = JOptionPane.showInputDialog("Enter your first name:");
        String lastName = JOptionPane.showInputDialog("Enter your last name:");
        String cell = JOptionPane.showInputDialog("Enter your cell number (e.g. +27831234567):");
        String username = JOptionPane.showInputDialog("Enter your username (must contain '_' and be ≤ 5 chars):");
        String password = JOptionPane.showInputDialog("Enter your password:");

        if (firstName == null || lastName == null || cell == null || username == null || password == null) {
            return;
        }

        if (firstName.isEmpty() || lastName.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Names cannot be empty.");
            return;
        }

        if (!isValidCell(cell)) {
            JOptionPane.showMessageDialog(null, "Cell must be +27 followed by 9 digits.");
            return;
        }

        if (!isValidUsername(username)) {
            JOptionPane.showMessageDialog(null, "Username must contain '_' and be ≤ 5 chars.");
            return;
        }

        if (!isValidPassword(password)) {
            JOptionPane.showMessageDialog(null, """
                    Password must be at least 8 characters and include:
                    - one uppercase
                    - one lowercase
                    - one digit
                    - one special character""");
            return;
        }

        regFirstName = firstName.trim();
        regLastName = lastName.trim();
        regCell = cell.trim();
        regUsername = username.trim();
        regPassword = password;

        JOptionPane.showMessageDialog(null, "Registration successful!");
        handleLogin();
    }

    //=================//
    // LOGIN
    //=================//
    private static void handleLogin() {
        if (regUsername == null) {
            JOptionPane.showMessageDialog(null, "No user registered yet. Please register first.");
            return;
        }

        String username = JOptionPane.showInputDialog("Enter username:");
        String password = JOptionPane.showInputDialog("Enter password:");
        if (username == null || password == null) {
            return;
        }

        if (username.equals(regUsername) && password.equals(regPassword)) {
            JOptionPane.showMessageDialog(null,
                    "Welcome, " + regFirstName + " " + regLastName + "!\nCell: " + regCell);
        } else {
            JOptionPane.showMessageDialog(null, "Incorrect credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    //=================//
    // SEND MESSAGES
    //=================//
    private static void sendMessages() {
        if (regUsername == null) {
            JOptionPane.showMessageDialog(null, "Please register first.");
            return;
        }

        String input = JOptionPane.showInputDialog("How many messages would you like to send?");
        if (input == null) {
            return;
        }
        int num;
        try {
            num = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Enter a valid number.");
            return;
        }

        for (int i = 0; i < num; i++) {
            String recipient = JOptionPane.showInputDialog("Enter recipient number (+27...):");
            if (recipient == null) {
                return;
            }
            if (!isValidCell(recipient)) {
                JOptionPane.showMessageDialog(null, "Invalid number format.");
                i--;
                continue;
            }

            String msgText = JOptionPane.showInputDialog("Enter your message:");
            if (msgText == null) {
                return;
            }

            Message msg = new Message(generateMessageId(), recipient, msgText);
            String hash = msg.createMessageHash();

            String[] options = {"Send", "Disregard", "Store"};
            int choice = JOptionPane.showOptionDialog(null,
                    msg.printMessages() + "\n\nMessage Hash: " + hash,
                    "Message Options",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]);

            switch (choice) {
                case 0 -> {
                    msg.setFlag("SENT");
                    sentMessages.add(msg);
                }
                case 1 -> {
                    msg.setFlag("DISREGARDED");
                    disregardedMessages.add(msg);
                }
                case 2 -> {
                    msg.setFlag("STORED");
                    storedMessages.add(msg);
                }
                default -> {
                }
            }

            messageIds.add(msg.getMessageId());
            messageHashes.add(hash);
        }
        JOptionPane.showMessageDialog(null, "Messages processed successfully.");
    }

    //=================//
    // MANAGEMENT MENU
    //=================//
    private static void openManagementMenu() {
        String[] options = {
            "Display Sender & Recipient",
            "Display Longest Message",
            "Search by Message ID",
            "Search by Recipient",
            "Delete by Hash",
            "Read JSON File (Stored Messages)",
            "Display Report",
            "Back"
        };

        while (true) {
            int choice = JOptionPane.showOptionDialog(null,
                    "Select a function:",
                    "Part 3 Management",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]);

            switch (choice) {
                case 0 ->
                    displaySenderAndRecipient();
                case 1 ->
                    displayLongestMessage();
                case 2 ->
                    searchByMessageId();
                case 3 ->
                    searchByRecipient();
                case 4 ->
                    deleteByHash();
                case 5 ->
                    displayReport();
                default -> {
                    return;
                }
            }
        }
    }

    //=================//
    // PART 3 FUNCTIONS
    //=================//
    private static void displaySenderAndRecipient() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No sent messages.");
            return;
        }
        StringBuilder sb = new StringBuilder("Sender: Developer\n");
        for (Message m : sentMessages) {
            sb.append("Recipient: ").append(m.getRecipient())
                    .append(" | MessageID: ").append(m.getMessageId()).append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString());
    }

    private static void displayLongestMessage() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No sent messages.");
            return;
        }
        Message longest = sentMessages.stream()
                .max(Comparator.comparingInt(m -> m.getMessageText().length()))
                .orElse(null);
        JOptionPane.showMessageDialog(null, "Longest Sent Message:\n" + longest.printMessages());
    }

    private static void searchByMessageId() {
        String id = JOptionPane.showInputDialog("Enter Message ID:");
        if (id == null) {
            return;
        }

        for (Message m : getAllMessages()) {
            if (m.getMessageId().equals(id)) {
                JOptionPane.showMessageDialog(null, m.printMessages()
                        + "\nHash: " + m.getMessageHash() + "\nFlag: " + m.getFlag());
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Message not found.");
    }

    private static void searchByRecipient() {
        String recipient = JOptionPane.showInputDialog("Enter recipient number:");
        if (recipient == null) {
            return;
        }
        List<Message> results = new ArrayList<>();
        for (Message m : getAllMessages()) {
            if (m.getRecipient().equals(recipient)) {
                results.add(m);
            }
        }

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No messages found for that recipient.");
            return;
        }

        StringBuilder sb = new StringBuilder("Messages sent to ").append(recipient).append(":\n");
        for (Message m : results) {
            sb.append(m.printMessages()).append("\n\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString());
    }

    private static void deleteByHash() {
        String hash = JOptionPane.showInputDialog("Enter message hash to delete:");
        if (hash == null) {
            return;
        }

        for (Iterator<Message> it = getAllMessages().iterator(); it.hasNext();) {
            Message m = it.next();
            if (m.getMessageHash().equalsIgnoreCase(hash)) {
                it.remove();
                messageIds.remove(m.getMessageId());
                messageHashes.remove(m.getMessageHash());
                JOptionPane.showMessageDialog(null, "Message deleted successfully.");
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Message not found.");
    }

    private static void displayReport() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No sent messages for report.");
            return;
        }
        StringBuilder sb = new StringBuilder("==== Sent Message Report ====\n");
        for (Message m : sentMessages) {
            sb.append("Hash: ").append(m.getMessageHash())
                    .append(" | Recipient: ").append(m.getRecipient())
                    .append("\nMessage: ").append(m.getMessageText()).append("\n\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString());
    }

    private static List<Message> getAllMessages() {
        List<Message> all = new ArrayList<>();
        all.addAll(sentMessages);
        all.addAll(storedMessages);
        all.addAll(disregardedMessages);
        return all;
    }

    //=================//
    // VALIDATION HELPERS
    //=================//
    private static boolean isValidCell(String cell) {
        return cell != null && Pattern.matches("^\\+27\\d{9}$", cell);
    }

    private static boolean isValidUsername(String username) {
        return username != null && username.contains("_") && username.length() <= 5;
    }

    private static boolean isValidPassword(String password) {
        return password != null
                && Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$", password);
    }

    private static String generateMessageId() {
        return String.valueOf(1000000000L + new Random().nextInt(899999999));
    }
}

// Message class has been moved to Message.java

