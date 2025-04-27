package com.gp16.event.dao.mongodb;

import com.gp16.event.dao.interfaces.AttendeeDao;
import com.gp16.event.model.Attendee;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class AttendeeMongoDao implements AttendeeDao {

    private final MongoDatabase db; // <-- THIS LINE is critical

    public AttendeeMongoDao(MongoDatabase db) {
        this.db = db;
    }

    @Override
    public void delete(String id) {
        var collection = db.getCollection("attendees", Document.class);
        var filter = new Document("_id", id);
        collection.deleteOne(filter);
    }

    @Override
    public Attendee findById(String id) {
        var collection = db.getCollection("attendees", Document.class);
        var filter = new Document("_id", id);
        Document doc = collection.find(filter).first();
    if (doc == null) {
        return null;
        }
            Attendee a = new Attendee();
            a.setId(doc.getString("_id"));
            a.setName(doc.getString("name"));
            a.setPosition(doc.getString("position"));
            a.setFullTime(doc.getBoolean("fullTime", false));
            a.setStatus(doc.getString("status"));
        return a;
    }

    @Override
    public Attendee create(Attendee attendee) {
    var collection = db.getCollection("attendees", Document.class);

    // Check if attendee ID is null — if yes, MongoDB will auto-create ObjectId
    String id = attendee.getId();
    if (id == null || id.isEmpty()) {
        id = new org.bson.types.ObjectId().toHexString();
        attendee.setId(id);
    }

    Document doc = new Document()
        .append("_id", id)
        .append("name", attendee.getName())
        .append("position", attendee.getPosition())
        .append("fullTime", attendee.isFullTime())
        .append("status", attendee.getStatus());

    collection.insertOne(doc); // <-- INSERT, NOT REPLACE
    return attendee;
    }


    @Override
    public List<Attendee> findAll() {
        var collection = db.getCollection("attendees", Document.class);
        var cursor = collection.find();
        List<Attendee> attendees = new ArrayList<>();
        for (Document doc : cursor) {
            Attendee a = new Attendee();
            a.setId(doc.getString("_id"));
            a.setName(doc.getString("name"));
            a.setPosition(doc.getString("position"));
            a.setFullTime(doc.getBoolean("fullTime", false));
            a.setStatus(doc.getString("status"));
            attendees.add(a);
        }
        return attendees;
    }

    @Override
    public void update(Attendee attendee) {
        var collection = db.getCollection("attendees", Document.class);
        var filter = new Document("_id", attendee.getId());
        var update = new Document("$set", new Document()
            .append("name", attendee.getName())
            .append("position", attendee.getPosition())
            .append("fullTime", attendee.isFullTime())
            .append("status", attendee.getStatus())
        );
        collection.updateOne(filter, update);
    }
}
