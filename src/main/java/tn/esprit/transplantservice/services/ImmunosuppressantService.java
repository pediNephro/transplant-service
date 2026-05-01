package tn.esprit.transplantservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.transplantservice.entities.Immunosuppressant;
import tn.esprit.transplantservice.entities.KidneyTransplant;
import tn.esprit.transplantservice.repositories.ImmunosuppressantRepository;
import tn.esprit.transplantservice.repositories.KidneyTransplantRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImmunosuppressantService implements CrudService<Immunosuppressant, Long> {

    private final ImmunosuppressantRepository repo;
    private final KidneyTransplantRepository transplantRepo;

    public Immunosuppressant save(Immunosuppressant e){ return repo.save(e); }

    public Immunosuppressant saveWithTransplant(Immunosuppressant drug, Long transplantId) {
        KidneyTransplant transplant = transplantRepo.findById(transplantId)
                .orElseThrow(() -> new RuntimeException("Transplant not found"));
        drug.setTransplant(transplant);
        return repo.save(drug);
    }

    public List<Immunosuppressant> findByTransplantId(Long transplantId) {
        return repo.findByTransplantId(transplantId);
    }

    public List<Immunosuppressant> findAll(){ return repo.findAll(); }

    public Immunosuppressant findById(Long id){
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Drug not found"));
    }

    public void delete(Long id){ repo.deleteById(id); }
    public List<Immunosuppressant> getActiveByTransplantId(Long transplantId) {
        return repo.findByTransplantIdAndIsActiveTrue(transplantId);
    }
}
