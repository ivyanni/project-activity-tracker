package ru.tersoft.tracker.entity.db;

import javax.persistence.*;

/**
 * @author Ilia Vianni
 * Created on 05.04.2018.
 */
@Entity
@Table(name = "attributes")
public class DBAttribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attribute_id")
    private Long id;

    @Column(name = "attribute_type_id")
    private Long attrType;

    @Column(name = "title")
    private String name;

    @Column(name = "desc")
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAttrType() {
        return attrType;
    }

    public void setAttrType(Long attrType) {
        this.attrType = attrType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
