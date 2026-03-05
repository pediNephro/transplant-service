package tn.esprit.transplantservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.transplantservice.entities.KidneyTransplant;
import tn.esprit.transplantservice.repositories.KidneyTransplantRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KidneyTransplantService implements CrudService<KidneyTransplant, Long> {

    private final KidneyTransplantRepository repo;

    public KidneyTransplant save(KidneyTransplant e){ return repo.save(e); }

    public List<KidneyTransplant> findAll(){ return repo.findAll(); }

    public KidneyTransplant findById(Long id){
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    public void delete(Long id){ repo.deleteById(id); }
}
