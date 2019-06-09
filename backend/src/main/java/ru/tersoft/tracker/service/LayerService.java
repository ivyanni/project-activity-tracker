package ru.tersoft.tracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tersoft.tracker.dao.ObjectDAO;
import ru.tersoft.tracker.dao.ObjectTypeDAO;
import ru.tersoft.tracker.entity.Event;
import ru.tersoft.tracker.entity.Layer;
import ru.tersoft.tracker.entity.Phase;
import ru.tersoft.tracker.entity.Stream;
import ru.tersoft.tracker.entity.db.DBObject;
import ru.tersoft.tracker.entity.db.DBObjectType;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Ilia Vianni
 * Created on 07.04.2018.
 */
@Service
public class LayerService {
    private final ObjectTypeDAO objectTypeDAO;
    private final ObjectDAO objectDAO;
    private final PhaseService phaseService;
    private final StreamService streamService;
    private final EventService eventService;

    @Autowired
    public LayerService(ObjectTypeDAO objectTypeDAO, ObjectDAO objectDAO, PhaseService phaseService, StreamService streamService, EventService eventService) {
        this.objectTypeDAO = objectTypeDAO;
        this.objectDAO = objectDAO;
        this.phaseService = phaseService;
        this.streamService = streamService;
        this.eventService = eventService;
    }

    public Layer getFromDBObject(DBObject object) {
        if(!object.getObjectType().getId().equals(Layer.OBJECT_TYPE_ID)) {
            throw new IllegalArgumentException("Object " + object.getId() + " has illegal type (" + object.getObjectType().getId() + ")");
        }
        Layer layer = new Layer();
        layer.setId(object.getId());
        layer.setName(object.getName());
        layer.setProjectId(object.getParent().getId());
        List<DBObject> dependentObjects = objectDAO.getDependentObjects(object.getId());
        Map<Long, String> params = objectDAO.getParametersForObject(object.getId());
        Optional<LocalDate> finishDate = params.entrySet().stream().filter(e -> e.getKey().equals(Layer.FINISH_DATE_ATTR_ID)).map(e -> LocalDate.ofEpochDay(Long.valueOf(e.getValue()))).findFirst();
        finishDate.ifPresent(layer::setFinishDate);
        List<Phase> phases = dependentObjects.stream()
                .filter(o -> o.getObjectType().getId().equals(Phase.OBJECT_TYPE_ID))
                .map(phaseService::getFromDBObject)
                //.sorted(Comparator.comparingInt(Phase::getOrder))
                .collect(Collectors.toList());
        List<Stream> streams = dependentObjects.stream()
                .filter(o -> o.getObjectType().getId().equals(Stream.OBJECT_TYPE_ID))
                .map(streamService::getFromDBObject)
                .sorted(Comparator.comparingInt(Stream::getOrder))
                .collect(Collectors.toList());
        List<Event> events = dependentObjects.stream()
                .filter(o -> o.getObjectType().getId().equals(Event.OBJECT_TYPE_ID))
                .map(eventService::getFromDBObject)
                .collect(Collectors.toList());
        layer.setPhases(phases);
        layer.setStreams(streams);
        layer.setEvents(events);
        return layer;
    }

    public Layer createLayer(Layer layer) {
        DBObject layerObject = new DBObject();
        DBObjectType objectType = objectTypeDAO.getObjectTypeById(Layer.OBJECT_TYPE_ID);
        layerObject.setName(layer.getName());
        layerObject.setParent(objectDAO.getObjectById(layer.getProjectId()));
        layerObject.setObjectType(objectType);
        layerObject = objectDAO.createObject(layerObject);
        Stream stream = new Stream();
        stream.setOrder(0);
        stream.setLayerId(layerObject.getId());
        stream.setName("New Stream");
        streamService.createStream(stream);
        Phase phase = new Phase();
        phase.setLayerId(layerObject.getId());
        phase.setName("New Phase");
        phase.setColor("#2b2b2b");
        phase.setStartDate(layer.getFinishDate().minus(7L, ChronoUnit.DAYS));
        phase.setEndDate(layer.getFinishDate());
        phaseService.createPhase(phase);
        objectDAO.setParameterForObject(layerObject, Layer.FINISH_DATE_ATTR_ID, String.valueOf(layer.getFinishDate().toEpochDay()));
        return getFromDBObject(layerObject);
    }

    public Layer updateLayer(Layer layer) {
        DBObject layerObject = new DBObject();
        DBObjectType objectType = objectTypeDAO.getObjectTypeById(Layer.OBJECT_TYPE_ID);
        layerObject.setName(layer.getName());
        layerObject.setId(layer.getId());
        layerObject.setParent(objectDAO.getObjectById(layer.getProjectId()));
        layerObject.setObjectType(objectType);
        layerObject = objectDAO.updateObject(layerObject);
        objectDAO.setParameterForObject(layerObject, Layer.FINISH_DATE_ATTR_ID, String.valueOf(layer.getFinishDate().toEpochDay()));
        return getFromDBObject(layerObject);
    }

    public Layer getLayer(Long id) {
        return getFromDBObject(objectDAO.getObjectById(id));
    }

    public void deleteLayer(Long id) {
        List<DBObject> objects = objectDAO.getDependentObjects(id);
        objects.forEach(object -> objectDAO.deleteObjectById(object.getId()));
        objectDAO.deleteObjectById(id);
    }
}
