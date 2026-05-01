package tn.esprit.transplantservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.transplantservice.entities.BanffCategory;
import tn.esprit.transplantservice.entities.Biopsy;
import tn.esprit.transplantservice.entities.BiopsyIndication;
import tn.esprit.transplantservice.entities.KidneyTransplant;
import tn.esprit.transplantservice.repositories.BiopsyRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BiopsyService - Tests unitaires")
class BiopsyServiceTest {

    @Mock
    private BiopsyRepository repo;

    @InjectMocks
    private BiopsyService service;

    private KidneyTransplant transplant;
    private Biopsy biopsy;

    @BeforeEach
    void setUp() {
        transplant = new KidneyTransplant();
        transplant.setId(10L);

        biopsy = Biopsy.builder()
                .id(1L)
                .biopsyDate(LocalDate.of(2026, 2, 1))
                .indication(BiopsyIndication.SUSPECTED_REJECTION)
                .banffScoreT(2)
                .banffScoreI(1)
                .banffCategory(BanffCategory.REJECTION)
                .conclusion("Rejet aigu confirmé")
                .transplant(transplant)
                .build();
    }

    // ─── save ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("save - persiste et retourne la biopsie")
    void save_success() {
        when(repo.save(any(Biopsy.class))).thenReturn(biopsy);

        Biopsy result = service.save(biopsy);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getIndication()).isEqualTo(BiopsyIndication.SUSPECTED_REJECTION);
        assertThat(result.getBanffCategory()).isEqualTo(BanffCategory.REJECTION);
        verify(repo).save(biopsy);
    }

    // ─── findAll ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findAll - retourne toutes les biopsies")
    void findAll_returnsList() {
        Biopsy b2 = Biopsy.builder()
                .id(2L)
                .indication(BiopsyIndication.PROTOCOL)
                .banffCategory(BanffCategory.NORMAL)
                .transplant(transplant)
                .build();

        when(repo.findAll()).thenReturn(List.of(biopsy, b2));

        List<Biopsy> result = service.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Biopsy::getId)
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    @DisplayName("findAll - retourne liste vide si aucune biopsie")
    void findAll_empty_returnsEmptyList() {
        when(repo.findAll()).thenReturn(List.of());

        List<Biopsy> result = service.findAll();

        assertThat(result).isEmpty();
    }

    // ─── findById ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findById - retourne la biopsie si trouvée")
    void findById_found_returnsBiopsy() {
        when(repo.findById(1L)).thenReturn(Optional.of(biopsy));

        Biopsy result = service.findById(1L);

        assertThat(result.getConclusion()).isEqualTo("Rejet aigu confirmé");
        assertThat(result.getBanffScoreT()).isEqualTo(2);
    }

    @Test
    @DisplayName("findById - lève exception si biopsie introuvable")
    void findById_notFound_throwsException() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Biopsy not found");
    }

    // ─── delete ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete - appelle deleteById avec le bon ID")
    void delete_callsRepository() {
        doNothing().when(repo).deleteById(1L);

        service.delete(1L);

        verify(repo).deleteById(1L);
    }

    // ─── getByTransplantId ────────────────────────────────────────────────────

    @Test
    @DisplayName("getByTransplantId - retourne les biopsies liées au transplant")
    void getByTransplantId_returnsList() {
        Biopsy b2 = Biopsy.builder()
                .id(2L)
                .indication(BiopsyIndication.PROTOCOL)
                .transplant(transplant)
                .build();

        when(repo.findByTransplantId(10L)).thenReturn(List.of(biopsy, b2));

        List<Biopsy> result = service.getByTransplantId(10L);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Biopsy::getId)
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    @DisplayName("getByTransplantId - retourne liste vide pour transplant sans biopsies")
    void getByTransplantId_noResults_returnsEmptyList() {
        when(repo.findByTransplantId(99L)).thenReturn(List.of());

        List<Biopsy> result = service.getByTransplantId(99L);

        assertThat(result).isEmpty();
    }
}
