package ru.tersoft.tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.tersoft.tracker.entity.Stream;
import ru.tersoft.tracker.service.StreamService;

/**
 * @author Ilia Vianni
 * Created on 13.04.2018.
 */
@RestController
@RequestMapping(value = "/api/streams/")
public class StreamController {
    private final StreamService streamService;

    @Autowired
    public StreamController(StreamService streamService) {
        this.streamService = streamService;
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Stream createStream(@RequestBody Stream stream) {
        return streamService.createStream(stream);
    }

    @RequestMapping(value = "{streamId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Stream updateStream(@PathVariable(name = "streamId") Long streamId, @RequestBody Stream stream) {
        stream.setId(streamId);
        return streamService.updateStream(stream);
    }

    @RequestMapping(value = "{streamId}", method = RequestMethod.DELETE)
    public void deleteStream(@PathVariable(name = "streamId") Long streamId) {
        streamService.deleteStream(streamId);
    }

    @RequestMapping(value = "{streamId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Stream getStream(@PathVariable(name = "streamId") Long id) {
        return streamService.getStream(id);
    }
}
