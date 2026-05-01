package tn.esprit.transplantservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.transplantservice.entities.SurveillanceProtocol;

import java.util.Optional;

public interface SurveillanceProtocolRepository extends JpaRepository<SurveillanceProtocol, Long> {
    Optional<SurveillanceProtocol> findByTransplantId(Long transplantId);
}
