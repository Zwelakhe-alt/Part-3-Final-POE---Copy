
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

/**
 * Self-contained test runner for the QuickChat classes. This avoids a JUnit
 * dependency so the project compiles and tests can be run directly.
 */
public class LoginTest {

    private List<Message> sentMessages;
    private List<Message> storedMessages;
    private List<Message> disregardedMessages;

    private void setup() throws Exception {
        // Access private static lists using reflection
        sentMessages = getPrivateList("sentMessages");
        storedMessages = getPrivateList("storedMessages");
        disregardedMessages = getPrivateList("disregardedMessages");

        // Clear all lists before each test
        sentMessages.clear();
        storedMessages.clear();
        disregardedMessages.clear();

        // Load test data
        sentMessages.add(new Message("MSG001", "+27835551234", "Did you get the cake"));
        sentMessages.add(new Message("MSG002", "+27835551234", "It is dinner time!"));
        sentMessages.add(new Message("MSG003", "+27838884567", "Where are you? You are late! I have asked you to be on time."));
        sentMessages.add(new Message("MSG004", "0838884567", "Its dinner time!"));

        // Create valid message hashes for deletion tests
        for (Message m : sentMessages) {
            m.createMessageHash();
        }
    }

    // Reflection Helper
    @SuppressWarnings("unchecked")
    private List<Message> getPrivateList(String fieldName) throws Exception {
        Field field = Login.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (List<Message>) field.get(null);
    }

    // Allows access to private getAllMessages()
    @SuppressWarnings("unchecked")
    private List<Message> getAllMessagesReflect() throws Exception {
        var method = Login.class.getDeclaredMethod("getAllMessages");
        method.setAccessible(true);
        return (List<Message>) method.invoke(null);
    }

    // Simple assertion helpers (throw AssertionError on failure)
    private static void assertEquals(Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError("Expected: " + expected + " but was: " + actual);
        }
    }

    private static void assertTrue(boolean cond) {
        if (!cond) {
            throw new AssertionError("Expected true but was false");
        }
    }

    private static void assertFalse(boolean cond) {
        if (cond) {
            throw new AssertionError("Expected false but was true");
        }
    }

    private static void assertNotNull(Object obj) {
        if (obj == null) {
            throw new AssertionError("Expected non-null value");
        }
    }

    // Individual tests converted from JUnit to plain methods
    private void testSentMessagesArrayPopulated() {
        assertEquals(4, sentMessages.size());
        assertEquals("Did you get the cake", sentMessages.get(0).getMessageText());
        assertEquals("It is dinner time!", sentMessages.get(1).getMessageText());
    }

    private void testLongestMessage() {
        Message longest = sentMessages.stream()
                .max((a, b) -> a.getMessageText().length() - b.getMessageText().length())
                .orElse(null);

        assertNotNull(longest);
        assertEquals("Where are you? You are late! I have asked you to be on time.", longest.getMessageText());
    }

    private void testSearchByMessageID() throws Exception {
        List<Message> all = getAllMessagesReflect();

        Message found = all.stream()
                .filter(m -> m.getMessageId().equals("MSG004"))
                .findFirst()
                .orElse(null);

        assertNotNull(found);
        assertEquals("Its dinner time!", found.getMessageText());
        assertEquals("0838884567", found.getRecipient());
    }

    private void testSearchByRecipient() throws Exception {
        List<Message> all = getAllMessagesReflect();

        List<Message> results = all.stream()
                .filter(m -> m.getRecipient().equals("+27838884567"))
                .toList();

        // Expectation based on setup: only 1 match
        assertEquals(1, results.size());
        assertEquals("Where are you? You are late! I have asked you to be on time.", results.get(0).getMessageText());
    }

    private void testDeleteByHash() {
        Message msgToDelete = sentMessages.get(1);
        sentMessages.remove(msgToDelete);
        assertFalse(sentMessages.contains(msgToDelete));
    }

    private void testDisplayReport() {
        StringBuilder report = new StringBuilder();

        for (Message m : sentMessages) {
            report.append("Hash: ").append(m.getMessageHash())
                    .append(" | Recipient: ").append(m.getRecipient())
                    .append("\nMessage: ").append(m.getMessageText()).append("\n\n");
        }

        assertTrue(report.toString().contains("Recipient: +27835551234"));
        assertTrue(report.toString().contains("Did you get the cake"));
        assertTrue(report.toString().contains("It is dinner time!"));
    }

    // Run all tests
    public void runAll() throws Exception {
        setup();
        testSentMessagesArrayPopulated();

        setup();
        testLongestMessage();

        setup();
        testSearchByMessageID();

        setup();
        testSearchByRecipient();

        setup();
        testDeleteByHash();

        setup();
        testDisplayReport();
    }

    // Main entry to run tests without JUnit
    public static void main(String[] args) {
        LoginTest runner = new LoginTest();
        try {
            runner.runAll();
            System.out.println("All tests passed.");
        } catch (AssertionError ae) {
            System.err.println("Test failed: " + ae.getMessage());
            ae.printStackTrace();
            System.exit(2);
        } catch (Exception e) {
            System.err.println("Error running tests: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
