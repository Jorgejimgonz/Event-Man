package com.gp16.event.dao.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.gp16.event.dao.interfaces.EventDao;
import com.gp16.event.model.Event;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;

public class EventMongoDao implements EventDao {
    private final MongoCollection<Document> coll;

    public EventMongoDao(MongoDatabase db) {
        this.coll = db.getCollection("events");
    }

    private Document toDoc(Event e) {
        Document d = new Document()
            .append("name", e.getName())
            .append("location", e.getLocation())
            .append("dateTime", e.getDateTime().toString())
            .append("capacity", e.getCapacity());
        if (e.getId() != null) {
            d.append("_id", new ObjectId(e.getId()));
        }
        return d;
    }

    private Event fromDoc(Document d) {
        Event e = new Event();
        e.setId(d.getObjectId("_id").toHexString());
        e.setName(d.getString("name"));
        e.setLocation(d.getString("location"));
        
        // New: Set the capacity, default to 5 if not present
        Integer capacity = d.getInteger("capacity", 5); 
        e.setCapacity(capacity);
    
        Object rawDate = d.get("dateTime");
        if (rawDate instanceof java.util.Date) {
            e.setDateTime(((java.util.Date) rawDate).toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime());
        }
        return e;
    }
    

    @Override
    public Event create(Event e) {
        Document d = toDoc(e);
        coll.insertOne(d);
        e.setId(d.getObjectId("_id").toHexString());
        return e;
    }

    @Override
    public List<Event> findAll() {
        List<Event> out = new ArrayList<>();
        for (Document d : coll.find()) {
            out.add(fromDoc(d));
        }
        return out;
    }

    @Override
    public Event findById(String id) {
        Document d = coll.find(new Document("_id", new ObjectId(id))).first();
        return d == null ? null : fromDoc(d);
    }

    @Override
    public void update(Event e) {
        coll.replaceOne(
            new Document("_id", new ObjectId(e.getId())),
            toDoc(e)
        );
    }

    @Override
    public void delete(String id) {
        coll.deleteOne(new Document("_id", new ObjectId(id)));
    }
}
