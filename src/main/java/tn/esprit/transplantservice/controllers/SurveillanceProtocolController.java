package tn.esprit.transplantservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.transplantservice.entities.SurveillanceProtocol;
import tn.esprit.transplantservice.services.SurveillanceProtocolService;

import java.util.List;

@RestController
@RequestMapping("/api/surveillance")
@RequiredArgsConstructor
public class SurveillanceProtocolController {

    @Autowired
    private final SurveillanceProtocolService service;

    @PostMapping
    public SurveillanceProtocol create(@RequestBody SurveillanceProtocol e){ return service.save(e); }

    @GetMapping
    public List<SurveillanceProtocol> getAll(){ return service.findAll(); }

    @GetMapping("/{id}")
    public SurveillanceProtocol getById(@PathVariable Long id){ return service.findById(id); }

    @PutMapping("/{id}")
    public SurveillanceProtocol update(@PathVariable Long id, @RequestBody SurveillanceProtocol e){
        e.setId(id);
        return service.save(e);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){ service.delete(id); }
}
