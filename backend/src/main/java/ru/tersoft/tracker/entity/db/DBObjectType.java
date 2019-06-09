package ru.tersoft.tracker.entity.db;

import javax.persistence.*;

/**
 * @author Ilia Vianni
 * Created on 03.04.2018.
 */
@Entity
@Table(name = "obj_types")
public class DBObjectType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "obj_type_id")
    private Long id;

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
}
