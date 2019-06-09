package ru.tersoft.tracker.entity.db;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Ilia Vianni
 * Created on 08.04.2018.
 */
@Entity
@Table(name = "parameters")
public class DBParameter implements Serializable {
    @Id
    @ManyToOne
    @JoinColumn(name = "attribute_id")
    private DBAttribute attribute;

    @Id
    @ManyToOne
    @JoinColumn(name = "obj_id")
    private DBObject object;

    @Column(name = "value")
    private String value;


    public DBAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(DBAttribute attribute) {
        this.attribute = attribute;
    }

    public DBObject getObject() {
        return object;
    }

    public void setObject(DBObject object) {
        this.object = object;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
