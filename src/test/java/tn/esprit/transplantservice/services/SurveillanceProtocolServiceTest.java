package tn.esprit.transplantservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.transplantservice.entities.KidneyTransplant;
import tn.esprit.transplantservice.entities.SurveillancePhase;
import tn.esprit.transplantservice.entities.SurveillanceProtocol;
import tn.esprit.transplantservice.repositories.KidneyTransplantRepository;
import tn.esprit.transplantservice.repositories.SurveillanceProtocolRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SurveillanceProtocolService - Tests unitaires")
class SurveillanceProtocolServiceTest {

    @Mock private SurveillanceProtocolRepository repo;
    @Mock private KidneyTransplantRepository transplantRepo;

    @InjectMocks
    private SurveillanceProtocolService service;

    private KidneyTransplant transplant;
    private final LocalDate TRANSPLANT_DATE = LocalDate.of(2026, 1, 10);

    @BeforeEach
    void setUp() {
        transplant = new KidneyTransplant();
        transplant.setId(1L);
        transplant.setTransplantDate(TRANSPLANT_DATE);
    }

    // ─── generateDefaultProtocol ──────────────────────────────────────────────

    @Test
    @DisplayName("generateDefaultProtocol - crée un nouveau protocole si aucun n'existe")
    void generateDefaultProtocol_noExisting_createsNew() {
        when(transplantRepo.findById(1L)).thenReturn(Optional.of(transplant));
        when(repo.findByTransplantId(1L)).thenReturn(Optional.empty());

        SurveillanceProtocol created = SurveillanceProtocol.builder()
                .id(5L)
                .transplant(transplant)
                .currentPhase(SurveillancePhase.IMMEDIATE)
                .consultationFrequency(7)
                .labTestFrequency(2)
                .nextConsultationDate(TRANSPLANT_DATE.plusDays(7))
                .nextLabTestDate(TRANSPLANT_DATE.plusDays(2))
                .completionPercentage(0.0)
                .build();

        when(repo.save(any(SurveillanceProtocol.class))).thenReturn(created);

        SurveillanceProtocol result = service.generateDefaultProtocol(1L);

        assertThat(result).isNotNull();
        assertThat(result.getCurrentPhase()).isEqualTo(SurveillancePhase.IMMEDIATE);
        assertThat(result.getConsultationFrequency()).isEqualTo(7);
        assertThat(result.getLabTestFrequency()).isEqualTo(2);
        assertThat(result.getCompletionPercentage()).isEqualTo(0.0);
        assertThat(result.getNextConsultationDate()).isEqualTo(TRANSPLANT_DATE.plusDays(7));
        assertThat(result.getNextLabTestDate()).isEqualTo(TRANSPLANT_DATE.plusDays(2));

        verify(repo).save(any(SurveillanceProtocol.class));
    }

    @Test
    @DisplayName("generateDefaultProtocol - retourne le protocole existant sans en créer un nouveau")
    void generateDefaultProtocol_existingProtocol_returnsExisting() {
        SurveillanceProtocol existing = SurveillanceProtocol.builder()
                .id(3L)
                .transplant(transplant)
                .currentPhase(SurveillancePhase.EARLY)
                .completionPercentage(25.0)
                .build();

        when(transplantRepo.findById(1L)).thenReturn(Optional.of(transplant));
        when(repo.findByTransplantId(1L)).thenReturn(Optional.of(existing));

        SurveillanceProtocol result = service.generateDefaultProtocol(1L);

        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getCurrentPhase()).isEqualTo(SurveillancePhase.EARLY);
        // existing protocol is returned — no new save
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("generateDefaultProtocol - lève exception si transplant introuvable")
    void generateDefaultProtocol_transplantNotFound_throwsException() {
        when(transplantRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.generateDefaultProtocol(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Transplant not found");

        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("generateDefaultProtocol - les dates sont calculées depuis la date de transplant")
    void generateDefaultProtocol_datesRelativeToTransplantDate() {
        LocalDate surgeryDate = LocalDate.of(2026, 3, 1);
        transplant.setTransplantDate(surgeryDate);

        when(transplantRepo.findById(1L)).thenReturn(Optional.of(transplant));
        when(repo.findByTransplantId(1L)).thenReturn(Optional.empty());
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SurveillanceProtocol result = service.generateDefaultProtocol(1L);

        assertThat(result.getNextConsultationDate()).isEqualTo(surgeryDate.plusDays(7));
        assertThat(result.getNextLabTestDate()).isEqualTo(surgeryDate.plusDays(2));
    }

    // ─── saveWithTransplant ───────────────────────────────────────────────────

    @Test
    @DisplayName("saveWithTransplant - lie le protocole au transplant et sauvegarde")
    void saveWithTransplant_found_linksAndSaves() {
        SurveillanceProtocol protocol = new SurveillanceProtocol();
        protocol.setCurrentPhase(SurveillancePhase.IMMEDIATE);

        when(transplantRepo.findById(1L)).thenReturn(Optional.of(transplant));
        when(repo.save(any(SurveillanceProtocol.class))).thenAnswer(inv -> {
            SurveillanceProtocol p = inv.getArgument(0);
            p.setId(7L);
            return p;
        });

        SurveillanceProtocol result = service.saveWithTransplant(protocol, 1L);

        assertThat(result.getTransplant()).isEqualTo(transplant);
        assertThat(result.getId()).isEqualTo(7L);
        verify(repo).save(protocol);
    }

    @Test
    @DisplayName("saveWithTransplant - lève exception si transplant introuvable")
    void saveWithTransplant_notFound_throwsException() {
        when(transplantRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.saveWithTransplant(new SurveillanceProtocol(), 99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Transplant not found");

        verify(repo, never()).save(any());
    }

    // ─── findByTransplantId ───────────────────────────────────────────────────

    @Test
    @DisplayName("findByTransplantId - retourne le protocole si trouvé")
    void findByTransplantId_found_returnsProtocol() {
        SurveillanceProtocol protocol = SurveillanceProtocol.builder()
                .id(5L).transplant(transplant)
                .currentPhase(SurveillancePhase.EARLY)
                .build();

        when(repo.findByTransplantId(1L)).thenReturn(Optional.of(protocol));

        SurveillanceProtocol result = service.findByTransplantId(1L);

        assertThat(result).isNotNull();
        assertThat(result.getCurrentPhase()).isEqualTo(SurveillancePhase.EARLY);
    }

    @Test
    @DisplayName("findByTransplantId - retourne null si aucun protocole")
    void findByTransplantId_notFound_returnsNull() {
        when(repo.findByTransplantId(99L)).thenReturn(Optional.empty());

        SurveillanceProtocol result = service.findByTransplantId(99L);

        assertThat(result).isNull();
    }

    // ─── findById ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findById - lève exception si protocole introuvable")
    void findById_notFound_throwsException() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Protocol not found");
    }
}
