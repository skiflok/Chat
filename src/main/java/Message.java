import java.io.Serializable;


public class Message implements Serializable {

    private final MessageType messageType;
    private final String message;

    public Message(MessageType messageType) {
        this.messageType = messageType;
        message = null;
    }

    public Message(MessageType messageType, String message) {
        this.messageType = messageType;
        this.message = message;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getMessage() {
        return message;
    }
}
