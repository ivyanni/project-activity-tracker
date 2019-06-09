package ru.tersoft.tracker.dao.impl;

import org.springframework.stereotype.Repository;
import ru.tersoft.tracker.dao.ObjectTypeDAO;
import ru.tersoft.tracker.entity.db.DBObjectType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

/**
 * @author Ilia Vianni
 * Created on 03.04.2018.
 */
@Repository
public class ObjectTypeDAOImpl implements ObjectTypeDAO {
    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public DBObjectType getObjectTypeById(Long id) {
        return entityManager.find(DBObjectType.class, id);
    }

    @Override
    @Transactional
    public DBObjectType createObjectType(DBObjectType objectType) {
        entityManager.persist(objectType);
        entityManager.flush();
        entityManager.refresh(objectType);
        return objectType;
    }

    @Override
    @Transactional
    public DBObjectType updateObjectType(DBObjectType objectType) {
        return entityManager.merge(objectType);
    }

    @Override
    @Transactional
    public void deleteObjectTypeById(Long id) {
        DBObjectType objectType = entityManager.find(DBObjectType.class, id);
        entityManager.remove(objectType);
    }
}
