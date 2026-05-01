package tn.esprit.transplantservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.transplantservice.entities.KidneyTransplant;
import tn.esprit.transplantservice.repositories.ComplicationRepository;
import tn.esprit.transplantservice.repositories.KidneyTransplantRepository;
import tn.esprit.transplantservice.entities.Complication;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplicationService implements CrudService<Complication, Long> {

    private final ComplicationRepository repo;
    private final KidneyTransplantRepository transplantRepo;

    public Complication save(Complication e){
        return repo.save(e);
    }

    public Complication saveWithTransplant(Complication complication, Long transplantId) {
        KidneyTransplant transplant = transplantRepo.findById(transplantId)
                .orElseThrow(() -> new RuntimeException("Transplant not found"));
        complication.setTransplant(transplant);
        return repo.save(complication);
    }

    public List<Complication> findByTransplantId(Long transplantId) {
        return repo.findByTransplantId(transplantId);
    }

    public List<Complication> findAll(){
        return repo.findAll();
    }

    public Complication findById(Long id){
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Complication not found"));
    }

    public void delete(Long id){
        repo.deleteById(id);
    }
}
