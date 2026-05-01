package tn.esprit.transplantservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.transplantservice.entities.Immunosuppressant;

import java.util.List;

public interface ImmunosuppressantRepository extends JpaRepository<Immunosuppressant, Long> {
    List<Immunosuppressant> findByTransplantId(Long transplantId);
    List<Immunosuppressant> findByTransplantIdAndIsActiveTrue(Long transplantId);

    // Optional: Find all drugs for a patient

}
