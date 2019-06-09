package ru.tersoft.tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.tersoft.tracker.entity.Layer;
import ru.tersoft.tracker.service.LayerService;

import java.util.List;

/**
 * @author Ilia Vianni
 * Created on 07.04.2018.
 */
@RestController
@RequestMapping(value = "/api/layers/")
public class LayerController {
    private final LayerService layerService;

    @Autowired
    public LayerController(LayerService layerService) {
        this.layerService = layerService;
    }

    @RequestMapping(value = "{layerId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Layer getLayer(@PathVariable(name = "layerId") Long id) {
        return layerService.getLayer(id);
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Layer createLayer(@RequestBody Layer layer) {
        return layerService.createLayer(layer);
    }

    @RequestMapping(value = "{layerId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Layer updateLayer(@PathVariable(name = "layerId") Long layerId, @RequestBody Layer layer) {
        layer.setId(layerId);
        return layerService.updateLayer(layer);
    }

    @RequestMapping(value = "{layerId}", method = RequestMethod.DELETE)
    public void deleteLayer(@PathVariable(name = "layerId") Long layerId) {
        layerService.deleteLayer(layerId);
    }
}
