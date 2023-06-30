package com.example.message;

import java.io.Serializable;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Message implements Serializable {

    private MessageType messageType;
    private String message;
    private String userName;

    public Message(MessageType messageType) {
        this.messageType = messageType;
        this.message = "";
        this.userName = "";
    }

    public Message(MessageType messageType, String message) {
        this.messageType = messageType;
        this.message = message;
        this.userName = "";
    }

    public Message(MessageType messageType, String message, String userName) {
        this.messageType = messageType;
        this.message = message;
        this.userName = userName;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getMessage() {
        return message;
    }

    public String getUserName() {
        return userName;
    }
}
