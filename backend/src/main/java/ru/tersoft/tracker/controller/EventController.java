package ru.tersoft.tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.tersoft.tracker.entity.Event;
import ru.tersoft.tracker.service.EventService;

/**
 * @author Ilia Vianni
 * Created on 13.04.2018.
 */
@RestController
@RequestMapping(value = "/api/events/")
public class EventController {
    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Event createEvent(@RequestBody Event event) {
        return eventService.createEvent(event);
    }

    @RequestMapping(value = "{eventId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Event updateEvent(@PathVariable(name = "eventId") Long eventId, @RequestBody Event event) {
        event.setId(eventId);
        return eventService.updateEvent(event);
    }

    @RequestMapping(value = "{eventId}", method = RequestMethod.DELETE)
    public void deleteEvent(@PathVariable(name = "eventId") Long eventId) {
        eventService.deleteEvent(eventId);
    }

    @RequestMapping(value = "{eventId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Event getEvent(@PathVariable(name = "eventId") Long id) {
        return eventService.getEvent(id);
    }

}
