package tn.esprit.transplantservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){ service.delete(id); }
}
