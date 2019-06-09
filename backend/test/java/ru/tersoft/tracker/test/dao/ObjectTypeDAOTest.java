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
import ru.tersoft.tracker.dao.ObjectTypeDAO;
import ru.tersoft.tracker.entity.db.DBObjectType;

import javax.transaction.Transactional;

/**
 * @author Ilia Vianni
 * Created on 09.05.2018.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@Transactional
@Rollback
public class ObjectTypeDAOTest {
    @Autowired private ObjectTypeDAO objectTypeDAO;

    private DBObjectType objectType;

    @Before
    public void setUp() {
        objectType = new DBObjectType();
        objectType.setName("Test");
        objectType.setDescription("Test");
    }

    @Test
    public void test() {
        // Creating object type
        objectType = objectTypeDAO.createObjectType(objectType);
        Assert.assertNotNull(objectType.getId());

        // Getting object type
        objectType = objectTypeDAO.getObjectTypeById(objectType.getId());
        Assert.assertNotNull(objectType);

        // Updating object type
        objectType.setName("Updated type");
        DBObjectType updatedObjectType = objectTypeDAO.updateObjectType(objectType);
        Assert.assertNotNull(updatedObjectType);
        Assert.assertEquals("Updated type", updatedObjectType.getName());

        // Deleting object type
        objectTypeDAO.deleteObjectTypeById(objectType.getId());
    }
}
