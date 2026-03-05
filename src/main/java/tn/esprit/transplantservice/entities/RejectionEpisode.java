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
public class RejectionEpisode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private LocalDate startDate;

    @Enumerated(EnumType.STRING)
    private RejectionType rejectionType;

    @Enumerated(EnumType.STRING)
    private Severity severity;

    private Double creatinineIncrease;
    private Double gfrAtRejection;
    private String treatment;

    @Enumerated(EnumType.STRING)
    private RejectionStatus status;

    private Long biopsyId;

    @ManyToOne
    @JsonBackReference
    private KidneyTransplant transplant;

}
