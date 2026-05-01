package tn.esprit.transplantservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.transplantservice.entities.Immunosuppressant;
import tn.esprit.transplantservice.services.ImmunosuppressantService;

import java.util.List;

@RestController
@RequestMapping("/api/drugs")
@RequiredArgsConstructor
public class ImmunosuppressantController {

    @Autowired
    private final ImmunosuppressantService service;

    @PostMapping
    public Immunosuppressant create(@RequestBody Immunosuppressant e){ return service.save(e); }

    @GetMapping
    public List<Immunosuppressant> getAll(){ return service.findAll(); }

    @GetMapping("/{id}")
    public Immunosuppressant getById(@PathVariable Long id){ return service.findById(id); }

    @PutMapping("/{id}")
    public Immunosuppressant update(@PathVariable Long id, @RequestBody Immunosuppressant e){
        e.setId(id);
        return service.save(e);
    }

    @GetMapping("/transplant/{transplantId}")
    public List<Immunosuppressant> getByTransplantId(@PathVariable Long transplantId){
        return service.findByTransplantId(transplantId);
    }

    @PostMapping("/transplant/{transplantId}")
    public Immunosuppressant createWithTransplant(@PathVariable Long transplantId,
                                                  @RequestBody Immunosuppressant e){
        return service.saveWithTransplant(e, transplantId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){ service.delete(id); }
    @GetMapping("/transplant/{transplantId}/active")
    public ResponseEntity<List<Immunosuppressant>> getActiveByTransplantId(
            @PathVariable Long transplantId) {
        try {
            List<Immunosuppressant> activeDrugs = service.getActiveByTransplantId(transplantId);
            return ResponseEntity.ok(activeDrugs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
