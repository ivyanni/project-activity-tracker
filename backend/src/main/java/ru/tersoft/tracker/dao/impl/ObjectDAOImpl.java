package ru.tersoft.tracker.dao.impl;

import org.springframework.stereotype.Repository;
import ru.tersoft.tracker.dao.ObjectDAO;
import ru.tersoft.tracker.entity.db.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Ilia Vianni
 * Created on 08.04.2018.
 */
@Repository
public class ObjectDAOImpl implements ObjectDAO {
    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public DBObject getObjectById(Long id) {
        return entityManager.find(DBObject.class, id);
    }

    @Override
    public List<DBObject> getObjectsByObjectType(DBObjectType objectType) {
        return entityManager.createQuery("select o from DBObject o where o.objectType = :objectType", DBObject.class).setParameter("objectType", objectType).getResultList();
    }

    @Override
    @Transactional
    public DBObject createObject(DBObject object) {
        entityManager.persist(object);
        entityManager.flush();
        entityManager.refresh(object);
        return object;
    }

    @Override
    @Transactional
    public DBObject updateObject(DBObject object) {
        return entityManager.merge(object);
    }

    @Override
    @Transactional
    public void deleteObjectById(Long id) {
        entityManager.createNativeQuery("delete from refs where obj_id=:object_id or reference_obj_id=:object_id")
                .setParameter("object_id", id).executeUpdate();
        entityManager.createNativeQuery("delete from parameters where obj_id=:object_id")
                .setParameter("object_id", id).executeUpdate();
        DBObject object = entityManager.find(DBObject.class, id);
        entityManager.remove(object);
    }

    @Override
    public List<DBObject> getDependentObjects(Long id) {
        return entityManager.createQuery("select o from DBObject o where o.parent = :parent", DBObject.class).setParameter("parent", getObjectById(id)).getResultList();
    }

    @Override
    public Map<Long, String> getParametersForObject(Long id) {
        DBObject object = getObjectById(id);
        List<DBParameter> params = entityManager.createQuery("select p from DBParameter p where p.object = :object", DBParameter.class).setParameter("object", object).getResultList();
        return params.stream().collect(Collectors.toMap(p -> p.getAttribute().getId(), DBParameter::getValue));
    }

    @Override
    public Map<Long, List<DBObject>> getReferencesForObject(Long id) {
        DBObject object = getObjectById(id);
        List<DBReference> references = entityManager.createQuery("select r from DBReference r where r.object = :object", DBReference.class).setParameter("object", object).getResultList();
        Map<Long, List<DBObject>> result = new HashMap<>();
        for(DBReference reference : references) {
            result.computeIfAbsent(reference.getAttribute().getId(), k -> new ArrayList<>()).add(reference.getReference());
        }
        return result;
    }

    @Override
    @Transactional
    public void setParameterForObject(DBObject object, Long attributeId, String value) {
        List params = entityManager.createNativeQuery("select * from parameters where attribute_id=:attr_id and obj_id=:object_id")
                .setParameter("attr_id", attributeId)
                .setParameter("object_id", object.getId()).getResultList();
        if(params.isEmpty()) {
            entityManager.createNativeQuery("insert into parameters(attribute_id, obj_id, value) values(:attr_id, :object_id, :value)")
                    .setParameter("attr_id", attributeId)
                    .setParameter("object_id", object.getId())
                    .setParameter("value", value).executeUpdate();
        } else {
            entityManager.createNativeQuery("update parameters set attribute_id=:attr_id, obj_id=:object_id, value=:value where attribute_id=:attr_id and obj_id=:object_id")
                    .setParameter("attr_id", attributeId)
                    .setParameter("object_id", object.getId())
                    .setParameter("value", value).executeUpdate();
        }
    }

    @Override
    @Transactional
    public void addReferenceForObject(DBObject object, Long attributeId, DBObject reference) {
        entityManager.createNativeQuery("insert into refs(attribute_id, obj_id, reference_obj_id) values(:attr_id, :object_id, :reference)")
                    .setParameter("attr_id", attributeId)
                    .setParameter("object_id", object.getId())
                    .setParameter("reference", reference.getId()).executeUpdate();
    }

    @Override
    @Transactional
    public void deleteReferencesForObject(DBObject object, Long attributeId) {
        entityManager.createNativeQuery("delete from refs where attribute_id=:attr_id and obj_id=:object_id")
                .setParameter("attr_id", attributeId)
                .setParameter("object_id", object.getId()).executeUpdate();
    }

    @Override
    @Transactional
    public void setReferenceForObject(DBObject object, Long attributeId, DBObject reference) {
        List refs = entityManager.createNativeQuery("select * from refs where attribute_id=:attr_id and obj_id=:object_id")
                .setParameter("attr_id", attributeId)
                .setParameter("object_id", object.getId()).getResultList();
        if(refs.isEmpty()) {
            entityManager.createNativeQuery("insert into refs(attribute_id, obj_id, reference_obj_id) values(:attr_id, :object_id, :reference)")
                    .setParameter("attr_id", attributeId)
                    .setParameter("object_id", object.getId())
                    .setParameter("reference", reference.getId()).executeUpdate();
        } else {
            entityManager.createNativeQuery("update refs set attribute_id=:attr_id, obj_id=:object_id, reference_obj_id=:reference where attribute_id=:attr_id and obj_id=:object_id")
                    .setParameter("attr_id", attributeId)
                    .setParameter("object_id", object.getId())
                    .setParameter("reference", reference.getId()).executeUpdate();
        }
    }
}
