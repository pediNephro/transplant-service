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
public class Biopsy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate biopsyDate;

    @Enumerated(EnumType.STRING)
    private BiopsyIndication indication;

    private Integer banffScoreT;
    private Integer banffScoreI;
    private Integer banffScoreG;

    @Enumerated(EnumType.STRING)
    private BanffCategory banffCategory;

    private String conclusion;
    private String reportPath;

    @ManyToOne
    @JsonBackReference
    private KidneyTransplant transplant;

}
