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
public class Complication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "transplant_id")
    @JsonBackReference
    private KidneyTransplant transplant;

    @Enumerated(EnumType.STRING)
    private ComplicationType complicationType;

    private String subType;
    private LocalDate appearanceDate;

    @Enumerated(EnumType.STRING)
    private Severity severity;

    private String description;
    private String treatment;

    @Enumerated(EnumType.STRING)
    private ComplicationStatus status;
}
