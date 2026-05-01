package tn.esprit.transplantservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.transplantservice.entities.Biopsy;
import tn.esprit.transplantservice.entities.RejectionEpisode;
import tn.esprit.transplantservice.services.BiopsyService;

import java.util.List;

@RestController
@RequestMapping("/api/biopsies")
@RequiredArgsConstructor
public class BiopsyController {

    @Autowired
    private final BiopsyService service;

    @PostMapping
    public Biopsy create(@RequestBody Biopsy e){ return service.save(e); }

    @GetMapping
    public List<Biopsy> getAll(){ return service.findAll(); }

    @GetMapping("/{id}")
    public Biopsy getById(@PathVariable Long id){ return service.findById(id); }

    @PutMapping("/{id}")
    public Biopsy update(@PathVariable Long id, @RequestBody Biopsy e){
        e.setId(id);
        return service.save(e);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){ service.delete(id); }
    @GetMapping("/transplant/{transplantId}")
    public ResponseEntity<List<Biopsy>> getByTransplant(
            @PathVariable Long transplantId) {

        return ResponseEntity.ok(
                service.getByTransplantId(transplantId)
        );
    }

}
