package com.gp16.event.dao.interfaces;

import com.gp16.event.model.Event;
import java.util.List;

public interface EventDao {
    Event create(Event e);
    List<Event> findAll();
    Event findById(String id);
    void update(Event e);
    void delete(String id);
}
