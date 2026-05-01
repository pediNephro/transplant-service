package tn.esprit.transplantservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.transplantservice.entities.HLACompatibility;
import tn.esprit.transplantservice.entities.KidneyTransplant;
import tn.esprit.transplantservice.entities.RejectionEpisode;
import tn.esprit.transplantservice.repositories.HLACompatibilityRepository;
import tn.esprit.transplantservice.repositories.KidneyTransplantRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HLACompatibilityService implements CrudService<HLACompatibility, Long> {

    private final HLACompatibilityRepository repo;
    private final KidneyTransplantRepository transplantRepo;

    public HLACompatibility addOrUpdateForTransplant(Long transplantId, HLACompatibility hla) {
        KidneyTransplant transplant = transplantRepo.findById(transplantId)
                .orElseThrow(() -> new RuntimeException("Transplant not found with id: " + transplantId));

        // Calculate mismatches before saving
        hla.setNumberOfMismatches(calculateMismatches(hla));
        
        // Link to transplant
        hla.setTransplant(transplant);

        // Check if HLA already exists for this transplant
        HLACompatibility existing = transplant.getHlaCompatibility();
        if (existing != null) {
            hla.setId(existing.getId());
        }

        return repo.save(hla);
    }

    private Integer calculateMismatches(HLACompatibility hla) {
        int totalMismatches = 0;
        totalMismatches += countLocusMismatches(hla.getDonorHlaA(), hla.getRecipientHlaA());
        totalMismatches += countLocusMismatches(hla.getDonorHlaB(), hla.getRecipientHlaB());
        totalMismatches += countLocusMismatches(hla.getDonorHlaDr(), hla.getRecipientHlaDr());
        return totalMismatches;
    }

    private int countLocusMismatches(String donor, String recipient) {
        if (donor == null || recipient == null) return 0;

        Set<String> donorAlleles = parseAlleles(donor);
        Set<String> recipientAlleles = parseAlleles(recipient);

        int mismatches = 0;
        for (String dA : donorAlleles) {
            if (!recipientAlleles.contains(dA)) {
                mismatches++;
            }
        }
        
        // Ensure max 2 per locus as per 0-6 score logic
        return Math.min(mismatches, 2);
    }

    private Set<String> parseAlleles(String s) {
        return Arrays.stream(s.split("[,;\\s]+"))
                .map(String::trim)
                .filter(str -> !str.isEmpty())
                .collect(Collectors.toSet());
    }

    public HLACompatibility save(HLACompatibility e){ return repo.save(e); }

    public List<HLACompatibility> findAll(){ return repo.findAll(); }

    public HLACompatibility findById(Long id){
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("HLA not found"));
    }

    public void delete(Long id){ repo.deleteById(id); }
     public List<HLACompatibility> getByTransplantId(Long transplantId) {
        return repo.findByTransplantId(transplantId);
    }
}
