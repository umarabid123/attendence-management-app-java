package com.ajstudios.easyattendance.models;

public class ClassItem {
    private String id;
    private String name_class;
    private String name_subject;
    private String position_bg;

    public ClassItem() {
        // Default constructor required for calls to DataSnapshot.getValue(ClassItem.class)
    }

    public ClassItem(String id, String name_class, String name_subject, String position_bg) {
        this.id = id;
        this.name_class = name_class;
        this.name_subject = name_subject;
        this.position_bg = position_bg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName_class() {
        return name_class;
    }

    public void setName_class(String name_class) {
        this.name_class = name_class;
    }

    public String getName_subject() {
        return name_subject;
    }

    public void setName_subject(String name_subject) {
        this.name_subject = name_subject;
    }

    public String getPosition_bg() {
        return position_bg;
    }

    public void setPosition_bg(String position_bg) {
        this.position_bg = position_bg;
    }
}
