package tn.esprit.transplantservice.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KidneyTransplant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long patientId;
    private LocalDate transplantDate;

    @Enumerated(EnumType.STRING)
    private DonorType donorType;

    private String donorBloodGroup;
    private Integer coldIschemiaTime;

    @Enumerated(EnumType.STRING)
    private GraftStatus graftStatus;

    private String surgicalReportPath;
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "transplant", cascade = CascadeType.ALL)
    @JsonManagedReference
    private HLACompatibility hlaCompatibility;

    @OneToMany(mappedBy = "transplant", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<RejectionEpisode> rejectionEpisodes;

    @OneToMany(mappedBy = "transplant", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Biopsy> biopsies;

    @OneToMany(mappedBy = "transplant", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Immunosuppressant> immunosuppressants;

    @OneToMany(mappedBy = "transplant", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Complication> complications;

    @OneToOne(mappedBy = "transplant", cascade = CascadeType.ALL)
    @JsonManagedReference
    private SurveillanceProtocol surveillanceProtocol;
}
