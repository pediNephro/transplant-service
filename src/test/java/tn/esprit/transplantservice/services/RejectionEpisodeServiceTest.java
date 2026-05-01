package tn.esprit.transplantservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.transplantservice.entities.KidneyTransplant;
import tn.esprit.transplantservice.entities.RejectionEpisode;
import tn.esprit.transplantservice.entities.RejectionStatus;
import tn.esprit.transplantservice.entities.RejectionType;
import tn.esprit.transplantservice.entities.Severity;
import tn.esprit.transplantservice.repositories.RejectionEpisodeRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RejectionEpisodeService - Tests unitaires")
class RejectionEpisodeServiceTest {

    @Mock
    private RejectionEpisodeRepository repo;

    @InjectMocks
    private RejectionEpisodeService service;

    private KidneyTransplant transplant;
    private RejectionEpisode episode;

    @BeforeEach
    void setUp() {
        transplant = new KidneyTransplant();
        transplant.setId(1L);

        episode = RejectionEpisode.builder()
                .id(20L)
                .startDate(LocalDate.of(2026, 2, 15))
                .rejectionType(RejectionType.ACUTE)
                .severity(Severity.MODERATE)
                .creatinineIncrease(45.0)
                .gfrAtRejection(38.0)
                .treatment("Bolus methylprednisolone")
                .status(RejectionStatus.CONFIRMED)
                .transplant(transplant)
                .build();
    }

    // ─── save ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("save - persiste et retourne l'épisode de rejet")
    void save_success() {
        when(repo.save(any(RejectionEpisode.class))).thenReturn(episode);

        RejectionEpisode result = service.save(episode);

        assertThat(result.getId()).isEqualTo(20L);
        assertThat(result.getRejectionType()).isEqualTo(RejectionType.ACUTE);
        assertThat(result.getSeverity()).isEqualTo(Severity.MODERATE);
        assertThat(result.getStatus()).isEqualTo(RejectionStatus.CONFIRMED);
        verify(repo).save(episode);
    }

    // ─── findAll ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findAll - retourne tous les épisodes de rejet")
    void findAll_returnsList() {
        RejectionEpisode episode2 = RejectionEpisode.builder()
                .id(21L)
                .rejectionType(RejectionType.CHRONIC)
                .severity(Severity.MILD)
                .status(RejectionStatus.SUSPECTED)
                .transplant(transplant)
                .build();

        when(repo.findAll()).thenReturn(List.of(episode, episode2));

        List<RejectionEpisode> result = service.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(RejectionEpisode::getId)
                .containsExactlyInAnyOrder(20L, 21L);
    }

    @Test
    @DisplayName("findAll - retourne liste vide si aucun épisode")
    void findAll_empty_returnsEmptyList() {
        when(repo.findAll()).thenReturn(List.of());

        List<RejectionEpisode> result = service.findAll();

        assertThat(result).isEmpty();
    }

    // ─── findById ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findById - retourne l'épisode si trouvé")
    void findById_found_returnsEpisode() {
        when(repo.findById(20L)).thenReturn(Optional.of(episode));

        RejectionEpisode result = service.findById(20L);

        assertThat(result.getId()).isEqualTo(20L);
        assertThat(result.getTreatment()).isEqualTo("Bolus methylprednisolone");
    }

    @Test
    @DisplayName("findById - lève exception si épisode introuvable")
    void findById_notFound_throwsException() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Rejection not found");
    }

    // ─── delete ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete - appelle deleteById avec le bon ID")
    void delete_callsRepository() {
        doNothing().when(repo).deleteById(20L);

        service.delete(20L);

        verify(repo).deleteById(20L);
    }

    // ─── getByTransplantId ────────────────────────────────────────────────────

    @Test
    @DisplayName("getByTransplantId - retourne les épisodes liés au transplant")
    void getByTransplantId_returnsList() {
        RejectionEpisode episode2 = RejectionEpisode.builder()
                .id(21L)
                .rejectionType(RejectionType.CHRONIC)
                .severity(Severity.MILD)
                .status(RejectionStatus.RESOLVED)
                .transplant(transplant)
                .build();

        when(repo.findByTransplantId(1L)).thenReturn(List.of(episode, episode2));

        List<RejectionEpisode> result = service.getByTransplantId(1L);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(RejectionEpisode::getStatus)
                .containsExactlyInAnyOrder(RejectionStatus.CONFIRMED, RejectionStatus.RESOLVED);
    }

    @Test
    @DisplayName("getByTransplantId - retourne liste vide si aucun épisode pour ce transplant")
    void getByTransplantId_noEpisodes_returnsEmptyList() {
        when(repo.findByTransplantId(99L)).thenReturn(List.of());

        List<RejectionEpisode> result = service.getByTransplantId(99L);

        assertThat(result).isEmpty();
    }
}
