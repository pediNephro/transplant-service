package tn.esprit.transplantservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.transplantservice.entities.Biopsy;
import tn.esprit.transplantservice.entities.RejectionEpisode;
import tn.esprit.transplantservice.repositories.BiopsyRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BiopsyService implements CrudService<Biopsy, Long> {

    private final BiopsyRepository repo;

    public Biopsy save(Biopsy e){ return repo.save(e); }

    public List<Biopsy> findAll(){ return repo.findAll(); }

    public Biopsy findById(Long id){
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Biopsy not found"));
    }

    public void delete(Long id){ repo.deleteById(id); }

    public List<Biopsy> getByTransplantId(Long transplantId) {
        return repo.findByTransplantId(transplantId);
    }
}

