package tn.esprit.transplantservice.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveillanceProtocol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Enumerated(EnumType.STRING)
    private SurveillancePhase currentPhase;

    private LocalDate nextConsultationDate;
    private LocalDate nextLabTestDate;

    private Integer consultationFrequency;
    private Integer labTestFrequency;

    private Double completionPercentage;

    @OneToOne
    @JoinColumn(name = "transplant_id")
    @JsonBackReference
    private KidneyTransplant transplant;
}
