package tn.esprit.transplantservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.transplantservice.entities.RejectionEpisode;
import tn.esprit.transplantservice.repositories.RejectionEpisodeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RejectionEpisodeService implements CrudService<RejectionEpisode, Long> {

    private final RejectionEpisodeRepository repo;

    public RejectionEpisode save(RejectionEpisode e){ return repo.save(e); }

    public List<RejectionEpisode> findAll(){ return repo.findAll(); }

    public RejectionEpisode findById(Long id){
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Rejection not found"));
    }

    public void delete(Long id){ repo.deleteById(id); }
    public List<RejectionEpisode> getByTransplantId(Long transplantId) {
        return repo.findByTransplantId(transplantId);
    }

}
