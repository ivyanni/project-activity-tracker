package ru.tersoft.tracker.entity;

import java.util.List;

/**
 * @author Ilia Vianni
 * Created on 21.04.2018.
 */
public class Project {
    public static final Long OBJECT_TYPE_ID = 7L;

    private Long id;
    private String name;
    private List<Layer> layers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Layer> getLayers() {
        return layers;
    }

    public void setLayers(List<Layer> layers) {
        this.layers = layers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
