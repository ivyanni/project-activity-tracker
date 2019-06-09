package ru.tersoft.tracker.test.dao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.tersoft.tracker.Application;
import ru.tersoft.tracker.dao.AttributeDAO;
import ru.tersoft.tracker.dao.ObjectDAO;
import ru.tersoft.tracker.entity.db.DBObject;
import ru.tersoft.tracker.entity.db.DBAttribute;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * @author Ilia Vianni
 * Created on 09.05.2018.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@Transactional
@Rollback
public class ObjectDAOTest {
    @Autowired private ObjectDAO objectDAO;
    @Autowired private AttributeDAO attributeDAO;

    private DBObject object;
    private DBAttribute attribute;

    @Before
    public void setUp() {
        object = new DBObject();
        object.setName("Test");
        object.setDescription("Test");
        attribute = new DBAttribute();
        attribute.setAttrType(1L);
        attribute.setName("Test");
        attribute.setDescription("Test");
    }

    @Test
    public void test() {
        // Creating object
        object = objectDAO.createObject(object);
        Assert.assertNotNull(object.getId());

        // Getting object
        DBObject dbObject = objectDAO.getObjectById(object.getId());
        Assert.assertNotNull(dbObject);

        // Updating object
        dbObject.setName("TestUpdated");
        DBObject updatedObject = objectDAO.updateObject(dbObject);
        Assert.assertNotNull(updatedObject);
        Assert.assertEquals("TestUpdated", updatedObject.getName());

        // Dependent objects
        DBObject object2 = new DBObject();
        object2.setName("Dependent object");
        object2.setParent(object);
        objectDAO.createObject(object2);
        List<DBObject> objects = objectDAO.getDependentObjects(object.getId());
        Assert.assertEquals(1, objects.size());

        // Setting and getting parameters
        DBAttribute testAttribute = attributeDAO.createAttribute(attribute);
        objectDAO.setParameterForObject(object, testAttribute.getId(), "Test Value");
        Map<Long, String> params = objectDAO.getParametersForObject(object.getId());
        Assert.assertNotNull(params);
        Assert.assertEquals("Test Value", params.get(testAttribute.getId()));

        // Setting and getting references
        objectDAO.setReferenceForObject(object, testAttribute.getId(), object);
        objectDAO.addReferenceForObject(object, testAttribute.getId(), object);
        Map<Long, List<DBObject>> refs = objectDAO.getReferencesForObject(object.getId());
        Assert.assertNotNull(refs);
        Assert.assertEquals(2, refs.get(testAttribute.getId()).size());
        Assert.assertTrue(refs.get(testAttribute.getId()).contains(object));
        objectDAO.deleteReferencesForObject(object, testAttribute.getId());
        refs = objectDAO.getReferencesForObject(object.getId());
        Assert.assertNull(refs.get(testAttribute.getId()));

        // Deleting object
        objectDAO.deleteObjectById(object.getId());
    }
}
