package tn.esprit.transplantservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.transplantservice.entities.Biopsy;
import tn.esprit.transplantservice.entities.RejectionEpisode;

import java.util.List;

public interface BiopsyRepository extends JpaRepository<Biopsy, Long> {
    List<Biopsy> findByTransplantId(Long transplantId);

}
