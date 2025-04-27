package com.gp16.event.dao.mongodb;

import com.gp16.event.dao.interfaces.RegistrationDao;
import com.gp16.event.model.Registration;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RegistrationMongoDao implements RegistrationDao {
    private final MongoCollection<Document> col;

    public RegistrationMongoDao(MongoDatabase db) {
        // 'registrations' is the collection name
        this.col = db.getCollection("registrations");
    }

    private Registration fromDoc(Document doc) {
        Registration r = new Registration();
        r.setId(doc.getObjectId("_id").toHexString());
        r.setEventId(doc.getString("eventId"));
        r.setAttendeeId(doc.getString("attendeeId"));
        // timestamp stored as Date in UTC
        Date d = doc.getDate("timestamp");
        r.setTimestamp(LocalDateTime.ofInstant(Instant.ofEpochMilli(d.getTime()), ZoneOffset.UTC));
        r.setStatus(doc.getString("status"));
        return r;
    }

    private Document toDoc(Registration r) {
        Document doc = new Document("eventId", r.getEventId())
            .append("attendeeId", r.getAttendeeId())
            .append("timestamp", Date.from(r.getTimestamp().toInstant(ZoneOffset.UTC)))
            .append("status", r.getStatus());
        if (r.getId() != null) {
            doc.append("_id", new ObjectId(r.getId()));
        }
        return doc;
    }

    @Override
    public Registration create(Registration reg) {
        Document doc = toDoc(reg);
        col.insertOne(doc);
        reg.setId(doc.getObjectId("_id").toHexString());
        return reg;
    }

    @Override
    public Registration findById(String id) {
        Document doc = col.find(new Document("_id", new ObjectId(id))).first();
        return doc != null ? fromDoc(doc) : null;
    }

    @Override
    public List<Registration> findByEventId(String eventId) {
        List<Registration> list = new ArrayList<>();
        for (Document doc : col.find(new Document("eventId", eventId))) {
            list.add(fromDoc(doc));
        }
        return list;
    }

    @Override
    public List<Registration> findByAttendeeId(String attendeeId) {
        List<Registration> list = new ArrayList<>();
        for (Document doc : col.find(new Document("attendeeId", attendeeId))) {
            list.add(fromDoc(doc));
        }
        return list;
    }

    @Override
    public List<Registration> findAll() {
        List<Registration> list = new ArrayList<>();
        for (Document doc : col.find()) {
            list.add(fromDoc(doc));
        }
        return list;
    }

    @Override
    public void update(Registration reg) {
        col.replaceOne(
            new Document("_id", new ObjectId(reg.getId())),
            toDoc(reg)
        );
    }

    @Override
    public void delete(String id) {
        col.deleteOne(new Document("_id", new ObjectId(id)));
    }
}
