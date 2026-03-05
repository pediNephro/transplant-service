package tn.esprit.transplantservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.transplantservice.entities.Immunosuppressant;
import tn.esprit.transplantservice.repositories.ImmunosuppressantRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImmunosuppressantService implements CrudService<Immunosuppressant, Long> {

    private final ImmunosuppressantRepository repo;

    public Immunosuppressant save(Immunosuppressant e){ return repo.save(e); }

    public List<Immunosuppressant> findAll(){ return repo.findAll(); }

    public Immunosuppressant findById(Long id){
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Drug not found"));
    }

    public void delete(Long id){ repo.deleteById(id); }
}
