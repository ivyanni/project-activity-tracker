package ru.tersoft.tracker.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * @author Ilia Vianni
 * Created on 08.04.2018.
 */
public class Event {
    public static final Long OBJECT_TYPE_ID = 5L;
    public static final Long PHASE_ATTR_ID = 1L;
    public static final Long STREAM_ATTR_ID = 2L;
    public static final Long TOP_LEVEL_EVENTS_ATTR_ID = 4L;
    public static final Long EVENT_DATE_ATTR_ID = 5L;
    public static final Long IS_DELIVERABLE_ATTR_ID = 10L;
    public static final Long COMPLETION_DATE_ATTR_ID = 13L;
    public static final Long DESCRIPTION_ATTR_ID = 15L;
    private Long id;
    private Long layerId;
    private String name;
    private Phase phase;
    private Stream stream;
    private String description;
    private List<Long> topLevelEvents;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;
    private Boolean isDeliverable;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate completionDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public Stream getStream() {
        return stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }

    public Long getLayerId() {
        return layerId;
    }

    public void setLayerId(Long layerId) {
        this.layerId = layerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getTopLevelEvents() {
        return topLevelEvents;
    }

    public void setTopLevelEvents(List<Long> topLevelEvents) {
        this.topLevelEvents = topLevelEvents;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Boolean getDeliverable() {
        return isDeliverable;
    }

    public void setDeliverable(Boolean deliverable) {
        isDeliverable = deliverable;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
