package demo;
public class Message {

    private String messageId;
    private String messageHash;
    private String recipient;
    private String messageText;
    private String flag;

    public Message(String messageId, String recipient, String messageText) {
        this.messageId = messageId;
        this.recipient = recipient;
        this.messageText = messageText;
    }

    public String createMessageHash() {
        if (messageId == null || messageText == null) {
            return null;
        }
        String firstTwo = messageId.substring(0, 2);
        char lastChar = messageId.charAt(messageId.length() - 1);
        String numberPart = Character.isDigit(lastChar) ? String.valueOf(lastChar) : "0";
        String[] words = messageText.trim().split("\\s+");
        String firstWord = words[0];
        String lastWord = words.length > 1 ? words[words.length - 1] : words[0];
        messageHash = (firstTwo + ":" + numberPart + ":" + (firstWord + lastWord)).toUpperCase();
        return messageHash;
    }

    public String printMessages() {
        return "To: " + recipient + "\nMessage: " + messageText;
    }

    // Getters & setters
    public String getMessageId() {
        return messageId;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
