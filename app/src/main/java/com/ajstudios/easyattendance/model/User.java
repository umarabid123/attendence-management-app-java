package com.ajstudios.easyattendance.model;

public class User {
    private String id;
    private String name;
    private String email;
    private String role;
    private String mobile;
    private boolean isRegistered;

    public User() {}

    public User(String name, String email, String mobile, String role, boolean isRegistered) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.role = role;
        this.isRegistered = isRegistered;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    
    public boolean isRegistered() { return isRegistered; }
    public void setRegistered(boolean registered) { isRegistered = registered; }
}
