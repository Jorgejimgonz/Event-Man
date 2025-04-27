package com.gp16.event.service;

import com.gp16.event.dao.interfaces.AttendeeDao;
import com.gp16.event.model.Attendee;
import java.util.List;

public class AttendeeService {
    private final AttendeeDao attendeeDao;

    public AttendeeService(AttendeeDao attendeeDao) {
        this.attendeeDao = attendeeDao;
    }

    public List<Attendee> getAllAttendees() {
        return attendeeDao.findAll();
    }

    public void addAttendee(Attendee attendee) {
        attendeeDao.create(attendee);
    }

    public void deleteAttendee(String attendeeId) {
        attendeeDao.delete(attendeeId);
    }

    public void updateAttendee(Attendee attendee) {
        attendeeDao.update(attendee);
    }
    
}
