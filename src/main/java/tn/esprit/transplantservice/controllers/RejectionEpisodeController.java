package tn.esprit.transplantservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.transplantservice.entities.RejectionEpisode;
import tn.esprit.transplantservice.services.RejectionEpisodeService;

import java.util.List;

@RestController
@RequestMapping("/api/rejections")
@RequiredArgsConstructor
public class RejectionEpisodeController {

    @Autowired
    private final RejectionEpisodeService service;

    @PostMapping
    public RejectionEpisode create(@RequestBody RejectionEpisode e){ return service.save(e); }

    @GetMapping
    public List<RejectionEpisode> getAll(){ return service.findAll(); }

    @GetMapping("/{id}")
    public RejectionEpisode getById(@PathVariable Long id){ return service.findById(id); }

    @PutMapping("/{id}")
    public RejectionEpisode update(@PathVariable Long id, @RequestBody RejectionEpisode e){
        e.setId(id);
        return service.save(e);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){ service.delete(id); }
    @GetMapping("/transplant/{transplantId}")
    public ResponseEntity<List<RejectionEpisode>> getByTransplant(
            @PathVariable Long transplantId) {

        return ResponseEntity.ok(
                service.getByTransplantId(transplantId)
        );
    }
}
