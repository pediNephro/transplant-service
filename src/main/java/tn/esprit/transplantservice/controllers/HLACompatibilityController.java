package tn.esprit.transplantservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.transplantservice.entities.HLACompatibility;
import tn.esprit.transplantservice.entities.RejectionEpisode;
import tn.esprit.transplantservice.services.HLACompatibilityService;

import java.util.List;

@RestController
@RequestMapping("/api/hla")
@RequiredArgsConstructor
public class HLACompatibilityController {

    @Autowired
    private final HLACompatibilityService service;

    @PostMapping
    public HLACompatibility create(@RequestBody HLACompatibility e){ return service.save(e); }

    @GetMapping
    public List<HLACompatibility> getAll(){ return service.findAll(); }

    @GetMapping("/{id}")
    public HLACompatibility getById(@PathVariable Long id){ return service.findById(id); }

    @PutMapping("/{id}")
    public HLACompatibility update(@PathVariable Long id, @RequestBody HLACompatibility e){
        e.setId(id);
        return service.save(e);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){ service.delete(id); }

    @GetMapping("/transplant/{transplantId}")
    public ResponseEntity<List<HLACompatibility>> getByTransplant(
            @PathVariable Long transplantId) {

        return ResponseEntity.ok(
                service.getByTransplantId(transplantId)
        );
    }
}
