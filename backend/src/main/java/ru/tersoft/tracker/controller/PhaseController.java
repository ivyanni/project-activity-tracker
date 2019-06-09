package ru.tersoft.tracker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.tersoft.tracker.entity.Phase;
import ru.tersoft.tracker.service.PhaseService;

/**
 * @author Ilia Vianni
 * Created on 13.04.2018.
 */
@RestController
@RequestMapping(value = "/api/phases/")
public class PhaseController {
    private final PhaseService phaseService;

    @Autowired
    public PhaseController(PhaseService phaseService) {
        this.phaseService = phaseService;
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Phase createPhase(@RequestBody Phase phase) {
        return phaseService.createPhase(phase);
    }

    @RequestMapping(value = "{phaseId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Phase updatePhase(@PathVariable(name = "phaseId") Long phaseId, @RequestBody Phase phase) {
        phase.setId(phaseId);
        return phaseService.updatePhase(phase);
    }

    @RequestMapping(value = "{phaseId}", method = RequestMethod.DELETE)
    public void deletePhase(@PathVariable(name = "phaseId") Long phaseId) {
        phaseService.deletePhase(phaseId);
    }

    @RequestMapping(value = "{phaseId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Phase getPhase(@PathVariable(name = "phaseId") Long id) {
        return phaseService.getPhase(id);
    }
}
