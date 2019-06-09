package ru.tersoft.tracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tersoft.tracker.dao.ObjectDAO;
import ru.tersoft.tracker.dao.ObjectTypeDAO;
import ru.tersoft.tracker.entity.*;
import ru.tersoft.tracker.entity.db.DBObject;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Ilia Vianni
 * Created on 07.04.2018.
 */
@Service
public class StreamService {
    private final ObjectTypeDAO objectTypeDAO;
    private final ObjectDAO objectDAO;

    @Autowired
    public StreamService(ObjectTypeDAO objectTypeDAO, ObjectDAO objectDAO) {
        this.objectTypeDAO = objectTypeDAO;
        this.objectDAO = objectDAO;
    }

    public Stream getFromDBObject(DBObject object) {
        if(!object.getObjectType().getId().equals(Stream.OBJECT_TYPE_ID)) {
            throw new IllegalArgumentException("Object " + object.getId() + " has illegal type (" + object.getObjectType().getId() + ")");
        }
        Stream stream = new Stream();
        stream.setId(object.getId());
        stream.setLayerId(object.getParent().getId());
        stream.setName(object.getName());
        Integer order = objectDAO.getParametersForObject(object.getId()).entrySet().stream()
                .filter(o -> o.getKey().equals(Stream.ORDER_ATTR_ID)).map(e -> Integer.valueOf(e.getValue())).findFirst().orElse(0);
        stream.setOrder(order);
        return stream;
    }

    public Stream getStream(Long id) {
        return getFromDBObject(objectDAO.getObjectById(id));
    }

    public Stream createStream(Stream stream) {
        DBObject streamObject = new DBObject();
        streamObject.setParent(objectDAO.getObjectById(stream.getLayerId()));
        streamObject.setName(stream.getName());
        streamObject.setObjectType(objectTypeDAO.getObjectTypeById(Stream.OBJECT_TYPE_ID));
        streamObject = objectDAO.createObject(streamObject);
        objectDAO.setParameterForObject(streamObject, Stream.ORDER_ATTR_ID, stream.getOrder().toString());
        return getFromDBObject(streamObject);
    }

    public Stream updateStream(Stream stream) {
        DBObject streamObject = new DBObject();
        streamObject.setParent(objectDAO.getObjectById(stream.getLayerId()));
        streamObject.setName(stream.getName());
        streamObject.setId(stream.getId());
        streamObject.setObjectType(objectTypeDAO.getObjectTypeById(Stream.OBJECT_TYPE_ID));
        streamObject = objectDAO.updateObject(streamObject);
        objectDAO.setParameterForObject(streamObject, Stream.ORDER_ATTR_ID, stream.getOrder().toString());
        return getFromDBObject(streamObject);
    }

    public void deleteStream(Long id) {
        for(DBObject object : objectDAO.getObjectsByObjectType(objectTypeDAO.getObjectTypeById(Event.OBJECT_TYPE_ID))) {
            Map<Long, List<DBObject>> refs = objectDAO.getReferencesForObject(object.getId());
            Optional<List<DBObject>> stream = refs.entrySet().stream().filter(e -> e.getKey().equals(Event.STREAM_ATTR_ID)).map(Map.Entry::getValue).findFirst();
            stream.ifPresent(e -> {
                if(e.get(0).getId().equals(id)) {
                    objectDAO.deleteObjectById(object.getId());
                }
            });
        }
        objectDAO.deleteObjectById(id);
    }
}
