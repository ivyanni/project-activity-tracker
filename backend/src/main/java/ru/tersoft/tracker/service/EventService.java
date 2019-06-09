package ru.tersoft.tracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tersoft.tracker.dao.ObjectDAO;
import ru.tersoft.tracker.dao.ObjectTypeDAO;
import ru.tersoft.tracker.entity.Event;
import ru.tersoft.tracker.entity.db.DBObject;

import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Ilia Vianni
 * Created on 07.04.2018.
 */
@Service
public class EventService {
    private final ObjectTypeDAO objectTypeDAO;
    private final ObjectDAO objectDAO;
    private final PhaseService phaseService;
    private final StreamService streamService;

    @Autowired
    public EventService(ObjectTypeDAO objectTypeDAO, ObjectDAO objectDAO, PhaseService phaseService, StreamService streamService) {
        this.objectTypeDAO = objectTypeDAO;
        this.objectDAO = objectDAO;
        this.phaseService = phaseService;
        this.streamService = streamService;
    }

    public Event getFromDBObject(DBObject object) {
        if(!object.getObjectType().getId().equals(Event.OBJECT_TYPE_ID)) {
            throw new IllegalArgumentException("Object " + object.getId() + " has illegal type (" + object.getObjectType().getId() + ")");
        }
        Event event = new Event();
        event.setLayerId(object.getParent().getId());
        event.setId(object.getId());
        Map<Long, List<DBObject>> refs = objectDAO.getReferencesForObject(object.getId());
        Map<Long, String> params = objectDAO.getParametersForObject(object.getId());
        Optional<List<DBObject>> phase = refs.entrySet().stream().filter(e -> e.getKey().equals(Event.PHASE_ATTR_ID)).map(Map.Entry::getValue).findFirst();
        phase.ifPresent(e -> event.setPhase(phaseService.getFromDBObject(e.get(0))));
        Optional<Boolean> isDeliverable = params.entrySet().stream().filter(e -> e.getKey().equals(Event.IS_DELIVERABLE_ATTR_ID)).map(e -> Boolean.parseBoolean(e.getValue())).findFirst();
        isDeliverable.ifPresent(event::setDeliverable);
        Optional<List<DBObject>> stream = refs.entrySet().stream().filter(e -> e.getKey().equals(Event.STREAM_ATTR_ID)).map(Map.Entry::getValue).findFirst();
        stream.ifPresent(e -> event.setStream(streamService.getFromDBObject(e.get(0))));
        Optional<LocalDate> date = params.entrySet().stream().filter(e -> e.getKey().equals(Event.EVENT_DATE_ATTR_ID)).map(e -> LocalDate.ofEpochDay(Long.valueOf(e.getValue()))).findFirst();
        date.ifPresent(event::setDate);
        Optional<String> description = params.entrySet().stream().filter(e -> e.getKey().equals(Event.DESCRIPTION_ATTR_ID)).map(Map.Entry::getValue).findFirst();
        description.ifPresent(event::setDescription);
        Optional<LocalDate> completionDate = params.entrySet().stream().filter(e -> e.getKey().equals(Event.COMPLETION_DATE_ATTR_ID)).map(e -> LocalDate.ofEpochDay(Long.valueOf(e.getValue()))).findFirst();
        completionDate.ifPresent(event::setCompletionDate);
        Optional<List<DBObject>> topLevelEvents = refs.entrySet().stream().filter(e -> e.getKey().equals(Event.TOP_LEVEL_EVENTS_ATTR_ID)).map(Map.Entry::getValue).findFirst();
        topLevelEvents.ifPresent(e -> event.setTopLevelEvents(e.stream().map(DBObject::getId).collect(Collectors.toList())));
        event.setName(object.getName());
        return event;
    }

    public Event createEvent(Event event) {
        DBObject eventObject = new DBObject();
        eventObject.setParent(objectDAO.getObjectById(event.getLayerId()));
        eventObject.setName(event.getName());
        eventObject.setObjectType(objectTypeDAO.getObjectTypeById(Event.OBJECT_TYPE_ID));
        eventObject = objectDAO.createObject(eventObject);
        DBObject streamObject = objectDAO.getObjectById(event.getStream().getId());
        objectDAO.addReferenceForObject(eventObject, Event.STREAM_ATTR_ID, streamObject);
        if(event.getPhase() != null) {
            DBObject phaseObject = objectDAO.getObjectById(event.getPhase().getId());
            objectDAO.addReferenceForObject(eventObject, Event.PHASE_ATTR_ID, phaseObject);
        }
        if(event.getTopLevelEvents() != null) {
            for(Long topLevelEvent : event.getTopLevelEvents()) {
                DBObject topLevelEventObject = new DBObject();
                topLevelEventObject.setId(topLevelEvent);
                objectDAO.addReferenceForObject(eventObject, Event.TOP_LEVEL_EVENTS_ATTR_ID, topLevelEventObject);
            }
        }
        if(event.getDate() != null) {
            objectDAO.setParameterForObject(eventObject, Event.EVENT_DATE_ATTR_ID, String.valueOf(event.getDate().toEpochDay()));
        }
        if(event.getCompletionDate() != null) {
            objectDAO.setParameterForObject(eventObject, Event.COMPLETION_DATE_ATTR_ID, String.valueOf(event.getCompletionDate().toEpochDay()));
        }
        if(event.getDescription() != null) {
            objectDAO.setParameterForObject(eventObject, Event.DESCRIPTION_ATTR_ID, event.getDescription());
        }
        /*if(event.getTime() != null) {
            objectDAO.setParameterForObject(eventObject, Event.EVENT_TIME_ATTR_ID, String.valueOf(event.getTime().toSecondOfDay()));
        }*/
        objectDAO.setParameterForObject(eventObject, Event.IS_DELIVERABLE_ATTR_ID, event.getDeliverable().toString());
        return getFromDBObject(eventObject);
    }

    public Event updateEvent(Event event) {
        DBObject eventObject = new DBObject();
        eventObject.setParent(objectDAO.getObjectById(event.getLayerId()));
        eventObject.setName(event.getName());
        eventObject.setId(event.getId());
        eventObject.setObjectType(objectTypeDAO.getObjectTypeById(Event.OBJECT_TYPE_ID));
        eventObject = objectDAO.updateObject(eventObject);
        DBObject streamObject = objectDAO.getObjectById(event.getStream().getId());
        objectDAO.setReferenceForObject(eventObject, Event.STREAM_ATTR_ID, streamObject);
        if(event.getPhase() != null) {
            DBObject phaseObject = objectDAO.getObjectById(event.getPhase().getId());
            objectDAO.setReferenceForObject(eventObject, Event.PHASE_ATTR_ID, phaseObject);
        }
        objectDAO.deleteReferencesForObject(eventObject, Event.TOP_LEVEL_EVENTS_ATTR_ID);
        if(event.getTopLevelEvents() != null) {
            for(Long topLevelEvent : event.getTopLevelEvents()) {
                DBObject topLevelEventObject = new DBObject();
                topLevelEventObject.setId(topLevelEvent);
                objectDAO.addReferenceForObject(eventObject, Event.TOP_LEVEL_EVENTS_ATTR_ID, topLevelEventObject);
            }
        }
        if(event.getDate() != null) {
            objectDAO.setParameterForObject(eventObject, Event.EVENT_DATE_ATTR_ID, String.valueOf(event.getDate().toEpochDay()));
        }
        if(event.getCompletionDate() != null)
            objectDAO.setParameterForObject(eventObject, Event.COMPLETION_DATE_ATTR_ID, String.valueOf(event.getCompletionDate().toEpochDay()));
        objectDAO.setParameterForObject(eventObject, Event.DESCRIPTION_ATTR_ID, event.getDescription());
        /*if(event.getTime() != null) {
            objectDAO.setParameterForObject(eventObject, Event.EVENT_TIME_ATTR_ID, String.valueOf(event.getTime().toSecondOfDay()));
        }*/
        objectDAO.setParameterForObject(eventObject, Event.IS_DELIVERABLE_ATTR_ID, event.getDeliverable().toString());
        return getFromDBObject(eventObject);
    }

    public Event getEvent(Long id) {
        return getFromDBObject(objectDAO.getObjectById(id));
    }

    public void deleteEvent(Long id) {
        objectDAO.deleteObjectById(id);
    }
}
