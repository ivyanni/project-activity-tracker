package ru.tersoft.tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.tersoft.tracker.entity.Project;
import ru.tersoft.tracker.service.ProjectService;

import java.util.List;

/**
 * @author Ilia Vianni
 * Created on 21.04.2018.
 */
@RestController
@RequestMapping(value = "/api/projects/")
public class ProjectController {
    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    @RequestMapping(value = "{projectId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Project getProject(@PathVariable(name = "projectId") Long id) {
        return projectService.getProject(id);
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Project createProject(@RequestParam(value = "name") String name) {
        return projectService.createProject(name);
    }

    @RequestMapping(value = "{projectId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Project updateProject(@PathVariable(name = "projectId") Long projectId, @RequestBody Project project) {
        project.setId(projectId);
        return projectService.updateProject(project);
    }

    @RequestMapping(value = "{projectId}", method = RequestMethod.DELETE)
    public void deleteProject(@PathVariable(name = "projectId") Long projectId) {
        projectService.deleteProject(projectId);
    }
}
