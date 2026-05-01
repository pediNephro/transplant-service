package tn.esprit.transplantservice.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HLACompatibility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String donorHlaA;
    private String donorHlaB;
    private String donorHlaDr;

    private String recipientHlaA;
    private String recipientHlaB;
    private String recipientHlaDr;

    @Enumerated(EnumType.STRING)
    private CrossMatchResult crossMatchResult;

    private Integer numberOfMismatches;
    @OneToOne
    @JoinColumn(name = "transplant_id")
    @JsonBackReference
    private KidneyTransplant transplant;

}
