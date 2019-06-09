package ru.tersoft.tracker.entity.db;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Ilia Vianni
 * Created on 08.04.2018.
 */
@Entity
@Table(name = "refs")
public class DBReference implements Serializable {
    @Id
    @ManyToOne
    @JoinColumn(name = "attribute_id")
    private DBAttribute attribute;

    @Id
    @ManyToOne
    @JoinColumn(name = "obj_id")
    private DBObject object;

    @Id
    @ManyToOne
    @JoinColumn(name = "reference_obj_id")
    private DBObject reference;

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

    public DBObject getReference() {
        return reference;
    }

    public void setReference(DBObject reference) {
        this.reference = reference;
    }
}
