package ru.tersoft.tracker.dao;

import ru.tersoft.tracker.entity.db.DBObject;
import ru.tersoft.tracker.entity.db.DBObjectType;

import java.util.List;
import java.util.Map;

/**
 * @author Ilia Vianni
 * Created on 03.04.2018.
 */
public interface ObjectDAO {
    DBObject getObjectById(Long id);
    List<DBObject> getObjectsByObjectType(DBObjectType objectType);
    DBObject createObject(DBObject object);
    DBObject updateObject(DBObject object);
    void deleteObjectById(Long id);
    List<DBObject> getDependentObjects(Long id);
    Map<Long, String> getParametersForObject(Long id);
    Map<Long, List<DBObject>> getReferencesForObject(Long id);
    void setParameterForObject(DBObject object, Long attributeId, String value);
    void deleteReferencesForObject(DBObject object, Long attributeId);
    void addReferenceForObject(DBObject object, Long attributeId, DBObject reference);
    void setReferenceForObject(DBObject object, Long attributeId, DBObject reference);
}
