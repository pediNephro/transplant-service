package tn.esprit.transplantservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.transplantservice.entities.SurveillanceProtocol;
import tn.esprit.transplantservice.repositories.SurveillanceProtocolRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SurveillanceProtocolService implements CrudService<SurveillanceProtocol, Long> {

    private final SurveillanceProtocolRepository repo;

    public SurveillanceProtocol save(SurveillanceProtocol e){ return repo.save(e); }

    public List<SurveillanceProtocol> findAll(){ return repo.findAll(); }

    public SurveillanceProtocol findById(Long id){
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Protocol not found"));
    }

    public void delete(Long id){ repo.deleteById(id); }
}
