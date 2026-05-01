package tn.esprit.transplantservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.transplantservice.entities.Complication;
import tn.esprit.transplantservice.services.ComplicationService;

import java.util.List;

@RestController
@RequestMapping("/api/complications")
@RequiredArgsConstructor
public class ComplicationController {

    @Autowired
    private final ComplicationService service;

    @PostMapping
    public Complication create(@RequestBody Complication e){
        return service.save(e);
    }

    @GetMapping
    public List<Complication> getAll(){
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Complication getById(@PathVariable Long id){
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public Complication update(@PathVariable Long id,
                               @RequestBody Complication e){
        e.setId(id);
        return service.save(e);
    }

    @GetMapping("/transplant/{transplantId}")
    public List<Complication> getByTransplantId(@PathVariable Long transplantId){
        return service.findByTransplantId(transplantId);
    }

    @PostMapping("/transplant/{transplantId}")
    public Complication createWithTransplant(@PathVariable Long transplantId,
                                             @RequestBody Complication e){
        return service.saveWithTransplant(e, transplantId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        service.delete(id);
    }
}
