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
public class Immunosuppressant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Enumerated(EnumType.STRING)
    private DrugName drugName;

    private Double currentDose;
    private LocalDate startDate;
    private Boolean isActive;

    private Double targetTroughMin;
    private Double targetTroughMax;

    private Double lastTroughLevel;
    private LocalDate lastMeasurementDate;

    @Enumerated(EnumType.STRING)
    private LevelStatus levelStatus;

    @ManyToOne
    @JsonBackReference
    private KidneyTransplant transplant;

}

