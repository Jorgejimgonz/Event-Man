package com.gp16.event.dao.interfaces;

import com.gp16.event.model.Registration;
import java.util.List;

public interface RegistrationDao {
    Registration create(Registration reg);
    Registration findById(String id);
    List<Registration> findByEventId(String eventId);
    List<Registration> findByAttendeeId(String attendeeId);
    List<Registration> findAll();
    void update(Registration reg);
    void delete(String id);
}
