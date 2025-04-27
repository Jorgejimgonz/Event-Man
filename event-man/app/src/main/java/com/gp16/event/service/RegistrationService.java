package com.gp16.event.service;

import com.gp16.event.dao.interfaces.RegistrationDao;
import com.gp16.event.dao.interfaces.EventDao;
import com.gp16.event.dao.interfaces.AttendeeDao;
import com.gp16.event.model.Registration;
import com.gp16.event.model.Attendee;
import com.gp16.event.model.Event;

import java.util.List;

public class RegistrationService {
    private final RegistrationDao regDao;
    private final EventDao eventDao;
    private final AttendeeDao attendeeDao;

    public RegistrationService(
        RegistrationDao regDao,
        EventDao eventDao,
        AttendeeDao attendeeDao
    ) {
        this.regDao      = regDao;
        this.eventDao    = eventDao;
        this.attendeeDao = attendeeDao;
    }

    public Registration register(String eventId, String attendeeId) throws Exception {
        Event ev = eventDao.findById(eventId);
        if (ev == null) throw new Exception("Event not found");
         // **New**: verify attendee exists
        Attendee at = attendeeDao.findById(attendeeId);
        if (at == null) throw new Exception("Attendee not found");
        List<Registration> regs = regDao.findByEventId(eventId);
        if (regs.size() >= ev.getCapacity()) 
            throw new Exception("Event is full");
        for (Registration r : regs) {
            if (r.getAttendeeId().equals(attendeeId)
                && "REGISTERED".equals(r.getStatus())) {
                throw new Exception("Attendee already registered");
            }
        }
        Registration reg = new Registration(eventId, attendeeId);
        return regDao.create(reg);
    }

    public void cancel(String registrationId) throws Exception {
        Registration reg = regDao.findById(registrationId);
        if (reg == null) throw new Exception("Registration not found");
        reg.setStatus("CANCELLED");
        regDao.update(reg);
    }

    public void updateStatus(String registrationId, String newStatus) throws Exception {
        Registration reg = regDao.findById(registrationId);
        if (reg == null) throw new Exception("Registration not found");
        reg.setStatus(newStatus);
        regDao.update(reg);
    }
}
