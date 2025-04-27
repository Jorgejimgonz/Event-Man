package com.gp16.event.dao.interfaces;

import com.gp16.event.model.Attendee;
import java.util.List;

public interface AttendeeDao {
    Attendee create(Attendee attendee);
    List<Attendee> findAll();
    Attendee findById(String id);
    void update(Attendee attendee);
    void delete(String id);
}
