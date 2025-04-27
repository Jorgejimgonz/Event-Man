package com.gp16.event.model;

import java.time.LocalDateTime;

public class Event {
    private String id;
    private String name;
    private String title;
    private String location;
    private int capacity;
    private LocalDateTime dateTime;

    public Event() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "Event{" +
               "id='" + id + '\'' +
               ", title='" + title + '\'' +
               ", name='" + name + '\'' +
               ", location='" + location + '\'' +
               ", capacity=" + capacity +
               ", dateTime=" + dateTime +
               '}';
    }
}
