package tn.esprit.transplantservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.transplantservice.entities.HLACompatibility;
import tn.esprit.transplantservice.repositories.HLACompatibilityRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HLACompatibilityService implements CrudService<HLACompatibility, Long> {

    private final HLACompatibilityRepository repo;

    public HLACompatibility save(HLACompatibility e){ return repo.save(e); }

    public List<HLACompatibility> findAll(){ return repo.findAll(); }

    public HLACompatibility findById(Long id){
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("HLA not found"));
    }

    public void delete(Long id){ repo.deleteById(id); }
}
