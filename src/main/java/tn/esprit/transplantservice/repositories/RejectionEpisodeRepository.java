package tn.esprit.transplantservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.transplantservice.entities.RejectionEpisode;

import java.util.List;

public interface RejectionEpisodeRepository extends JpaRepository<RejectionEpisode, Long> {
    List<RejectionEpisode> findByTransplantId(Long transplantId);

}
