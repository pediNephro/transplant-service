package tn.esprit.transplantservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.transplantservice.entities.Complication;
import tn.esprit.transplantservice.repositories.ComplicationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplicationService implements CrudService<Complication, Long> {

    private final ComplicationRepository repo;

    public Complication save(Complication e){
        return repo.save(e);
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
