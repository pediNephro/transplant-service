package tn.esprit.transplantservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.transplantservice.entities.HLACompatibility;
import tn.esprit.transplantservice.entities.KidneyTransplant;
import tn.esprit.transplantservice.entities.SurveillanceProtocol;
import tn.esprit.transplantservice.services.HLACompatibilityService;
import tn.esprit.transplantservice.services.KidneyTransplantService;
import tn.esprit.transplantservice.services.SurveillanceProtocolService;

import java.util.List;

@RestController
@RequestMapping("/api/transplants")
@RequiredArgsConstructor
public class KidneyTransplantController {

    @Autowired
    private final KidneyTransplantService service;
    @Autowired
    private final HLACompatibilityService hlaService;
    @Autowired
    private final SurveillanceProtocolService protocolService;

    @PostMapping("/{transplantId}/surveillance-protocol/generate")
    public SurveillanceProtocol generateProtocol(@PathVariable Long transplantId) {
        return protocolService.generateDefaultProtocol(transplantId);
    }

    @PostMapping("/{transplantId}/hla-compatibility")
    public HLACompatibility addHLA(@PathVariable Long transplantId, @RequestBody HLACompatibility hla) {
        return hlaService.addOrUpdateForTransplant(transplantId, hla);
    }

    @PostMapping
    public KidneyTransplant create(@RequestBody KidneyTransplant t) {
        return service.save(t);
    }

    @GetMapping
    public List<KidneyTransplant> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public KidneyTransplant getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public KidneyTransplant update(@PathVariable Long id,
            @RequestBody KidneyTransplant t) {
        t.setId(id);
        return service.save(t);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
