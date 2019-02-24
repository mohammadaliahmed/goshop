package com.clicknshop.goshop.Models;

public class CustomerNotificationModel {
     String notificationId,title,message,type,id;
    long time;

    public CustomerNotificationModel(String notificationId, String title, String message, String type, String id,long time) {
        this.notificationId = notificationId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.id = id;
        this.time=time;
    }


    public CustomerNotificationModel() {
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
