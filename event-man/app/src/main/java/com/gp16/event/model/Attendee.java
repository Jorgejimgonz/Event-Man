package com.gp16.event.model;

public class Attendee {
    private String id;
    private String name;
    private String email;       // if you need it in Main.java
    private String position;
    private boolean fullTime;
    private String status;

    public Attendee() {}

    // getters/setters only:
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public boolean isFullTime() { return fullTime; }
    public void setFullTime(boolean fullTime) { this.fullTime = fullTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
