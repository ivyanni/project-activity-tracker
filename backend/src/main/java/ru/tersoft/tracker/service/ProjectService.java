package ru.tersoft.tracker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tersoft.tracker.dao.ObjectDAO;
import ru.tersoft.tracker.dao.ObjectTypeDAO;
import ru.tersoft.tracker.entity.Layer;
import ru.tersoft.tracker.entity.Project;
import ru.tersoft.tracker.entity.db.DBObject;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ilia Vianni
 * Created on 21.04.2018.
 */
@Service
public class ProjectService {
    private Logger LOGGER = LoggerFactory.getLogger(ProjectService.class);
    private final ObjectTypeDAO objectTypeDAO;
    private final ObjectDAO objectDAO;
    private final LayerService layerService;

    @Autowired
    public ProjectService(ObjectTypeDAO objectTypeDAO, ObjectDAO objectDAO, LayerService layerService) {
        this.objectTypeDAO = objectTypeDAO;
        this.objectDAO = objectDAO;
        this.layerService = layerService;
    }

    public Project getFromDBObject(DBObject object) {
        if(!object.getObjectType().getId().equals(Project.OBJECT_TYPE_ID)) {
            throw new IllegalArgumentException("Object " + object.getId() + " has illegal type (" + object.getObjectType().getId() + ")");
        }
        Project project = new Project();
        project.setId(object.getId());
        project.setName(object.getName());
        List<Layer> layers = objectDAO.getDependentObjects(object.getId()).stream().map(layerService::getFromDBObject).collect(Collectors.toList());
        project.setLayers(layers);
        return project;
    }

    public Project createProject(String name) {
        DBObject projectObject = new DBObject();
        projectObject.setName(name);
        projectObject.setObjectType(objectTypeDAO.getObjectTypeById(Project.OBJECT_TYPE_ID));
        projectObject = objectDAO.createObject(projectObject);
        return getFromDBObject(projectObject);
    }

    public Project updateProject(Project project) {
        DBObject projectObject = new DBObject();
        projectObject.setName(project.getName());
        projectObject.setId(project.getId());
        projectObject.setObjectType(objectTypeDAO.getObjectTypeById(Project.OBJECT_TYPE_ID));
        projectObject = objectDAO.updateObject(projectObject);
        return getFromDBObject(projectObject);
    }

    public Project getProject(Long id) {
        return getFromDBObject(objectDAO.getObjectById(id));
    }

    public List<Project> getAllProjects() {
        return objectDAO.getObjectsByObjectType(objectTypeDAO.getObjectTypeById(Project.OBJECT_TYPE_ID)).stream().map(this::getFromDBObject).collect(Collectors.toList());
    }

    public void deleteProject(Long id) {
        List<DBObject> objects = objectDAO.getDependentObjects(id);
        objects.forEach(object -> {
            List<DBObject> objectList = objectDAO.getDependentObjects(object.getId());
            objectList.forEach(object1 -> objectDAO.deleteObjectById(object1.getId()));
            objectDAO.deleteObjectById(object.getId());
        });
        objectDAO.deleteObjectById(id);
    }
}
