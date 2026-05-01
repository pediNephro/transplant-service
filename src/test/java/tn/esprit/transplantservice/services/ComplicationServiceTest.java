package tn.esprit.transplantservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.transplantservice.entities.Complication;
import tn.esprit.transplantservice.entities.ComplicationStatus;
import tn.esprit.transplantservice.entities.ComplicationType;
import tn.esprit.transplantservice.entities.KidneyTransplant;
import tn.esprit.transplantservice.entities.Severity;
import tn.esprit.transplantservice.repositories.ComplicationRepository;
import tn.esprit.transplantservice.repositories.KidneyTransplantRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ComplicationService - Tests unitaires")
class ComplicationServiceTest {

    @Mock
    private ComplicationRepository repo;

    @Mock
    private KidneyTransplantRepository transplantRepo;

    @InjectMocks
    private ComplicationService service;

    private KidneyTransplant transplant;
    private Complication complication;

    @BeforeEach
    void setUp() {
        transplant = new KidneyTransplant();
        transplant.setId(10L);

        complication = Complication.builder()
                .id(1L)
                .transplant(transplant)
                .complicationType(ComplicationType.INFECTIOUS)
                .subType("CMV")
                .appearanceDate(LocalDate.of(2026, 3, 1))
                .severity(Severity.MODERATE)
                .description("Infection à CMV post-greffe")
                .treatment("Valganciclovir")
                .status(ComplicationStatus.ACTIVE)
                .build();
    }

    // ─── save ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("save - persiste et retourne la complication")
    void save_success() {
        when(repo.save(any(Complication.class))).thenReturn(complication);

        Complication result = service.save(complication);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getComplicationType()).isEqualTo(ComplicationType.INFECTIOUS);
        assertThat(result.getStatus()).isEqualTo(ComplicationStatus.ACTIVE);
        verify(repo).save(complication);
    }

    // ─── saveWithTransplant ───────────────────────────────────────────────────

    @Test
    @DisplayName("saveWithTransplant - attache le transplant et persiste la complication")
    void saveWithTransplant_success() {
        Complication newComp = Complication.builder()
                .complicationType(ComplicationType.VASCULAR)
                .severity(Severity.SEVERE)
                .status(ComplicationStatus.ACTIVE)
                .build();

        when(transplantRepo.findById(10L)).thenReturn(Optional.of(transplant));
        when(repo.save(any(Complication.class))).thenAnswer(inv -> {
            Complication c = inv.getArgument(0);
            c.setId(2L);
            return c;
        });

        Complication result = service.saveWithTransplant(newComp, 10L);

        assertThat(result.getTransplant()).isEqualTo(transplant);
        assertThat(result.getComplicationType()).isEqualTo(ComplicationType.VASCULAR);
        verify(transplantRepo).findById(10L);
        verify(repo).save(newComp);
    }

    @Test
    @DisplayName("saveWithTransplant - lève exception si transplant introuvable")
    void saveWithTransplant_transplantNotFound_throwsException() {
        when(transplantRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.saveWithTransplant(complication, 99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Transplant not found");

        verify(repo, never()).save(any());
    }

    // ─── findAll ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findAll - retourne toutes les complications")
    void findAll_returnsList() {
        Complication c2 = Complication.builder()
                .id(2L)
                .complicationType(ComplicationType.UROLOGICAL)
                .severity(Severity.MILD)
                .status(ComplicationStatus.RESOLVED)
                .transplant(transplant)
                .build();

        when(repo.findAll()).thenReturn(List.of(complication, c2));

        List<Complication> result = service.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Complication::getId)
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    @DisplayName("findAll - retourne liste vide si aucune complication")
    void findAll_empty_returnsEmptyList() {
        when(repo.findAll()).thenReturn(List.of());

        List<Complication> result = service.findAll();

        assertThat(result).isEmpty();
    }

    // ─── findById ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findById - retourne la complication si trouvée")
    void findById_found_returnsComplication() {
        when(repo.findById(1L)).thenReturn(Optional.of(complication));

        Complication result = service.findById(1L);

        assertThat(result.getSubType()).isEqualTo("CMV");
        assertThat(result.getSeverity()).isEqualTo(Severity.MODERATE);
    }

    @Test
    @DisplayName("findById - lève exception si complication introuvable")
    void findById_notFound_throwsException() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Complication not found");
    }

    // ─── findByTransplantId ───────────────────────────────────────────────────

    @Test
    @DisplayName("findByTransplantId - retourne les complications liées au transplant")
    void findByTransplantId_returnsList() {
        Complication c2 = Complication.builder()
                .id(2L)
                .complicationType(ComplicationType.OTHER)
                .status(ComplicationStatus.RESOLVED)
                .transplant(transplant)
                .build();

        when(repo.findByTransplantId(10L)).thenReturn(List.of(complication, c2));

        List<Complication> result = service.findByTransplantId(10L);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Complication::getStatus)
                .containsExactlyInAnyOrder(ComplicationStatus.ACTIVE, ComplicationStatus.RESOLVED);
    }

    @Test
    @DisplayName("findByTransplantId - retourne liste vide pour transplant sans complications")
    void findByTransplantId_noResults_returnsEmptyList() {
        when(repo.findByTransplantId(99L)).thenReturn(List.of());

        List<Complication> result = service.findByTransplantId(99L);

        assertThat(result).isEmpty();
    }

    // ─── delete ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete - appelle deleteById avec le bon ID")
    void delete_callsRepository() {
        doNothing().when(repo).deleteById(1L);

        service.delete(1L);

        verify(repo).deleteById(1L);
    }
}
