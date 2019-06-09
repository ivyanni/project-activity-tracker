package ru.tersoft.tracker.dao;

import ru.tersoft.tracker.entity.db.DBAttribute;
import ru.tersoft.tracker.entity.db.DBObjectType;

/**
 * @author Ilia Vianni
 * Created on 05.04.2018.
 */
public interface AttributeDAO {
    DBAttribute getAttributeById(Long id);
    DBAttribute createAttribute(DBAttribute attribute);
    DBAttribute updateAttribute(DBAttribute attribute);
    void deleteAttributeById(Long id);
    void assignAttributeToObjectType(DBAttribute attribute, DBObjectType objectType);

}
