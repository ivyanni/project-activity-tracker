package ru.tersoft.tracker.dao.impl;

import org.springframework.stereotype.Repository;
import ru.tersoft.tracker.dao.AttributeDAO;
import ru.tersoft.tracker.entity.db.DBAttribute;
import ru.tersoft.tracker.entity.db.DBObjectType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

/**
 * @author Ilia Vianni
 * Created on 05.04.2018.
 */
@Repository
public class AttributeDAOImpl implements AttributeDAO {
    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public DBAttribute getAttributeById(Long id) {
        return entityManager.find(DBAttribute.class, id);
    }

    @Override
    @Transactional
    public DBAttribute createAttribute(DBAttribute attribute) {
        entityManager.persist(attribute);
        entityManager.flush();
        entityManager.refresh(attribute);
        return attribute;
    }

    @Override
    @Transactional
    public DBAttribute updateAttribute(DBAttribute attribute) {
        return entityManager.merge(attribute);
    }

    @Override
    @Transactional
    public void deleteAttributeById(Long id) {
        DBAttribute attribute = entityManager.find(DBAttribute.class, id);
        entityManager.remove(attribute);
    }

    @Override
    @Transactional
    public void assignAttributeToObjectType(DBAttribute attribute, DBObjectType objectType) {
        entityManager.createNativeQuery("insert into attr_obj_type_binds(attribute_id, obj_type_id) values(:attr_id, :object_type_id)")
                .setParameter("attr_id", attribute.getId())
                .setParameter("object_type_id", objectType.getId()).executeUpdate();
    }
}
