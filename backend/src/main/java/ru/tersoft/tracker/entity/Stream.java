package ru.tersoft.tracker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Ilia Vianni
 * Created on 08.04.2018.
 */
public class Stream {
    public static final Long OBJECT_TYPE_ID = 4L;
    public static final Long ORDER_ATTR_ID = 3L;
    private Long id;
    private Long layerId;
    private String name;
    private Integer order;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getLayerId() {
        return layerId;
    }

    public void setLayerId(Long layerId) {
        this.layerId = layerId;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
