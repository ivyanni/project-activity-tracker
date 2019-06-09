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
import ru.tersoft.tracker.dao.ObjectTypeDAO;
import ru.tersoft.tracker.entity.db.DBAttribute;
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
public class AttributeDAOTest {
    @Autowired private AttributeDAO attributeDAO;
    @Autowired private ObjectTypeDAO objectTypeDAO;

    private DBAttribute attribute;
    private DBObjectType objectType;

    @Before
    public void setUp() {
        attribute = new DBAttribute();
        attribute.setAttrType(1L);
        attribute.setName("Test");
        attribute.setDescription("Test");
        objectType = new DBObjectType();
        objectType.setName("Test");
        objectType.setDescription("Test");
    }

    @Test
    public void test() {
        // Creating attribute
        attribute = attributeDAO.createAttribute(attribute);
        Assert.assertNotNull(attribute.getId());

        // Getting attribute
        attribute = attributeDAO.getAttributeById(attribute.getId());
        Assert.assertNotNull(attribute);

        // Updating attribute
        attribute.setName("Updated attribute");
        DBAttribute updatedAttribute = attributeDAO.updateAttribute(attribute);
        Assert.assertNotNull(updatedAttribute);
        Assert.assertEquals("Updated attribute", updatedAttribute.getName());

        // Assigning attribute
        DBObjectType testObjectType = objectTypeDAO.createObjectType(objectType);
        attributeDAO.assignAttributeToObjectType(attribute, testObjectType);

        // Deleting attribute
        attributeDAO.deleteAttributeById(attribute.getId());
    }
}
