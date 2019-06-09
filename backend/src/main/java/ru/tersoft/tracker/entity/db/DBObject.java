package ru.tersoft.tracker.entity.db;

import javax.persistence.*;

/**
 * @author Ilia Vianni
 * Created on 03.04.2018.
 */
@Entity
@Table(name = "objects")
public class DBObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "obj_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parent_obj_id")
    private DBObject parent;

    @ManyToOne
    @JoinColumn(name = "obj_type_id")
    private DBObjectType objectType;

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

    public DBObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(DBObjectType objectType) {
        this.objectType = objectType;
    }

    public DBObject getParent() {
        return parent;
    }

    public void setParent(DBObject parent) {
        this.parent = parent;
    }
}
