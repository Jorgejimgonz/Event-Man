package com.gp16.event.service;

import com.gp16.event.dao.interfaces.EventDao;
import com.gp16.event.model.Event;
import java.util.List;

public class EventService {
    private final EventDao eventDao;

    public EventService(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    public List<Event> getAllEvents() {
        return eventDao.findAll();
    }

    public void addEvent(Event event) {
        eventDao.create(event);
    }

    public void deleteEvent(String eventId) {
        eventDao.delete(eventId);
    }
    public void updateEvent(Event event) {
        eventDao.update(event);
    }
}
