package ru.tersoft.tracker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tersoft.tracker.dao.ObjectDAO;
import ru.tersoft.tracker.dao.ObjectTypeDAO;
import ru.tersoft.tracker.entity.Event;
import ru.tersoft.tracker.entity.Phase;
import ru.tersoft.tracker.entity.db.DBObject;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Ilia Vianni
 * Created on 07.04.2018.
 */
@Service
public class PhaseService {
    private Logger logger = LoggerFactory.getLogger(PhaseService.class);
    private final ObjectTypeDAO objectTypeDAO;
    private final ObjectDAO objectDAO;

    @Autowired
    public PhaseService(ObjectTypeDAO objectTypeDAO, ObjectDAO objectDAO) {
        this.objectTypeDAO = objectTypeDAO;
        this.objectDAO = objectDAO;
    }

    public Phase getFromDBObject(DBObject object) {
        if(!object.getObjectType().getId().equals(Phase.OBJECT_TYPE_ID)) {
            throw new IllegalArgumentException("Object " + object.getId() + " has illegal type (" + object.getObjectType().getId() + ")");
        }
        Phase phase = new Phase();
        phase.setId(object.getId());
        phase.setLayerId(object.getParent().getId());
        phase.setName(object.getName());
        Map<Long, String> params = objectDAO.getParametersForObject(object.getId());
        Optional<String> color = params.entrySet().stream().filter(e -> e.getKey().equals(Phase.COLOR_ATTR_ID)).map(Map.Entry::getValue).findFirst();
        color.ifPresent(phase::setColor);
        Optional<LocalDate> startDate = params.entrySet().stream().filter(e -> e.getKey().equals(Phase.PHASE_START_DATE_ATTR_ID)).map(e -> LocalDate.ofEpochDay(Long.valueOf(e.getValue()))).findFirst();
        startDate.ifPresent(phase::setStartDate);
        Optional<LocalDate> endDate = params.entrySet().stream().filter(e -> e.getKey().equals(Phase.PHASE_END_DATE_ATTR_ID)).map(e -> LocalDate.ofEpochDay(Long.valueOf(e.getValue()))).findFirst();
        endDate.ifPresent(phase::setEndDate);
        return phase;
    }

    public Phase createPhase(Phase phase) {
        DBObject phaseObject = new DBObject();
        phaseObject.setParent(objectDAO.getObjectById(phase.getLayerId()));
        phaseObject.setName(phase.getName());
        phaseObject.setObjectType(objectTypeDAO.getObjectTypeById(Phase.OBJECT_TYPE_ID));
        phaseObject = objectDAO.createObject(phaseObject);
        objectDAO.setParameterForObject(phaseObject, Phase.PHASE_START_DATE_ATTR_ID, String.valueOf(phase.getStartDate().toEpochDay()));
        objectDAO.setParameterForObject(phaseObject, Phase.PHASE_END_DATE_ATTR_ID, String.valueOf(phase.getEndDate().toEpochDay()));
        objectDAO.setParameterForObject(phaseObject, Phase.COLOR_ATTR_ID, phase.getColor());
        return getFromDBObject(phaseObject);
    }

    public Phase updatePhase(Phase phase) {
        DBObject phaseObject = new DBObject();
        phaseObject.setParent(objectDAO.getObjectById(phase.getLayerId()));
        phaseObject.setName(phase.getName());
        phaseObject.setId(phase.getId());
        phaseObject.setObjectType(objectTypeDAO.getObjectTypeById(Phase.OBJECT_TYPE_ID));
        phaseObject = objectDAO.updateObject(phaseObject);
        objectDAO.setParameterForObject(phaseObject, Phase.PHASE_START_DATE_ATTR_ID, String.valueOf(phase.getStartDate().toEpochDay()));
        /*logger.debug("enddate: " + phase.getEndDate().toString());
        logger.debug("enddate: " + String.valueOf(phase.getEndDate().toEpochDay()));*/
        objectDAO.setParameterForObject(phaseObject, Phase.PHASE_END_DATE_ATTR_ID, String.valueOf(phase.getEndDate().toEpochDay()));
        objectDAO.setParameterForObject(phaseObject, Phase.COLOR_ATTR_ID, phase.getColor());
        return getFromDBObject(phaseObject);
    }

    public Phase getPhase(Long id) {
        return getFromDBObject(objectDAO.getObjectById(id));
    }

    public void deletePhase(Long id) {
        for(DBObject object : objectDAO.getObjectsByObjectType(objectTypeDAO.getObjectTypeById(Event.OBJECT_TYPE_ID))) {
            Map<Long, List<DBObject>> refs = objectDAO.getReferencesForObject(object.getId());
            Optional<List<DBObject>> stream = refs.entrySet().stream().filter(e -> e.getKey().equals(Event.PHASE_ATTR_ID)).map(Map.Entry::getValue).findFirst();
            stream.ifPresent(e -> {
                if(e.get(0).getId().equals(id)) {
                    objectDAO.deleteObjectById(object.getId());
                }
            });
        }
        objectDAO.deleteObjectById(id);
    }
}
