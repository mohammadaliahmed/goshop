package com.clicknshop.goshop.Models;

/**
 * Created by AliAh on 24/06/2018.
 */

public class ChatModel {
    String id, text, username;
    long time;
    String status, initiator;
    String name;
    String imageUrl, documentUrl, mediaType, messageType;


    public ChatModel(String id, String text, String username, long time, String status, String initiator, String name,
                     String imageUrl, String documentUrl, String mediaType, String messageType

    ) {
        this.id = id;
        this.text = text;
        this.username = username;
        this.time = time;
        this.status = status;
        this.initiator = initiator;
        this.name = name;
        this.imageUrl = imageUrl;
        this.documentUrl = documentUrl;
        this.mediaType = mediaType;
        this.messageType = messageType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public ChatModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
