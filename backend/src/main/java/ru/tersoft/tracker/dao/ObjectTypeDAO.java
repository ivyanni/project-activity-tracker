package ru.tersoft.tracker.dao;

import ru.tersoft.tracker.entity.db.DBObjectType;

/**
 * @author Ilia Vianni
 * Created on 03.04.2018.
 */
public interface ObjectTypeDAO {
    DBObjectType getObjectTypeById(Long id);
    DBObjectType createObjectType(DBObjectType objectType);
    DBObjectType updateObjectType(DBObjectType objectType);
    void deleteObjectTypeById(Long id);
}
