package tn.esprit.transplantservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.transplantservice.entities.KidneyTransplant;
import tn.esprit.transplantservice.entities.SurveillancePhase;
import tn.esprit.transplantservice.entities.SurveillanceProtocol;
import tn.esprit.transplantservice.repositories.KidneyTransplantRepository;
import tn.esprit.transplantservice.repositories.SurveillanceProtocolRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SurveillanceProtocolService implements CrudService<SurveillanceProtocol, Long> {

    private final SurveillanceProtocolRepository repo;
    private final KidneyTransplantRepository transplantRepo;

    public SurveillanceProtocol save(SurveillanceProtocol e){ return repo.save(e); }

    public SurveillanceProtocol saveWithTransplant(SurveillanceProtocol protocol, Long transplantId) {
        KidneyTransplant transplant = transplantRepo.findById(transplantId)
                .orElseThrow(() -> new RuntimeException("Transplant not found"));
        protocol.setTransplant(transplant);
        return repo.save(protocol);
    }

    public SurveillanceProtocol generateDefaultProtocol(Long transplantId) {
        KidneyTransplant transplant = transplantRepo.findById(transplantId)
                .orElseThrow(() -> new RuntimeException("Transplant not found"));

        // Check if protocol already exists
        return repo.findByTransplantId(transplantId).orElseGet(() -> {
            SurveillanceProtocol protocol = SurveillanceProtocol.builder()
                    .transplant(transplant)
                    .currentPhase(SurveillancePhase.IMMEDIATE)
                    .consultationFrequency(7) // Weekly
                    .labTestFrequency(2)       // Twice a week or every 2 days
                    .nextConsultationDate(transplant.getTransplantDate().plusDays(7))
                    .nextLabTestDate(transplant.getTransplantDate().plusDays(2))
                    .completionPercentage(0.0)
                    .build();
            return repo.save(protocol);
        });
    }

    public SurveillanceProtocol findByTransplantId(Long transplantId) {
        return repo.findByTransplantId(transplantId).orElse(null);
    }

    public List<SurveillanceProtocol> findAll(){ return repo.findAll(); }

    public SurveillanceProtocol findById(Long id){
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Protocol not found"));
    }

    public void delete(Long id){ repo.deleteById(id); }
}
