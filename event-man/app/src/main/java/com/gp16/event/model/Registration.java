package com.gp16.event.model;

import java.time.LocalDateTime;

public class Registration {
    private String id;
    private String eventId;
    private String attendeeId;
    private LocalDateTime timestamp;
    private String status; // e.g. "REGISTERED", "CANCELLED"

    // No-arg constructor for MongoDB mapping
    public Registration() {}

    // Convenience constructor
    public Registration(String eventId, String attendeeId) {
        this.eventId = eventId;
        this.attendeeId = attendeeId;
        this.timestamp = LocalDateTime.now();
        this.status = "REGISTERED";
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getAttendeeId() { return attendeeId; }
    public void setAttendeeId(String attendeeId) { this.attendeeId = attendeeId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
