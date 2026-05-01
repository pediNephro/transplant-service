package tn.esprit.transplantservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.transplantservice.entities.DonorType;
import tn.esprit.transplantservice.entities.GraftStatus;
import tn.esprit.transplantservice.entities.KidneyTransplant;
import tn.esprit.transplantservice.repositories.KidneyTransplantRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("KidneyTransplantService - Tests unitaires")
class KidneyTransplantServiceTest {

    @Mock
    private KidneyTransplantRepository repo;

    @InjectMocks
    private KidneyTransplantService service;

    private KidneyTransplant transplant;

    @BeforeEach
    void setUp() {
        transplant = KidneyTransplant.builder()
                .id(1L)
                .patientId(100L)
                .transplantDate(LocalDate.of(2026, 1, 10))
                .donorType(DonorType.DECEASED)
                .donorBloodGroup("A+")
                .coldIschemiaTime(18)
                .graftStatus(GraftStatus.ACTIVE)
                .build();
    }

    // ─── save ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("save - persiste et retourne le transplant")
    void save_success() {
        when(repo.save(any(KidneyTransplant.class))).thenReturn(transplant);

        KidneyTransplant result = service.save(transplant);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPatientId()).isEqualTo(100L);
        assertThat(result.getDonorType()).isEqualTo(DonorType.DECEASED);
        assertThat(result.getGraftStatus()).isEqualTo(GraftStatus.ACTIVE);
        verify(repo).save(transplant);
    }

    // ─── findAll ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findAll - retourne tous les transplants")
    void findAll_returnsList() {
        KidneyTransplant t2 = KidneyTransplant.builder()
                .id(2L)
                .patientId(101L)
                .donorType(DonorType.LIVING)
                .graftStatus(GraftStatus.ACTIVE)
                .build();

        when(repo.findAll()).thenReturn(List.of(transplant, t2));

        List<KidneyTransplant> result = service.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(KidneyTransplant::getId)
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    @DisplayName("findAll - retourne liste vide si aucun transplant")
    void findAll_empty_returnsEmptyList() {
        when(repo.findAll()).thenReturn(List.of());

        List<KidneyTransplant> result = service.findAll();

        assertThat(result).isEmpty();
    }

    // ─── findById ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findById - retourne le transplant si trouvé")
    void findById_found_returnsTransplant() {
        when(repo.findById(1L)).thenReturn(Optional.of(transplant));

        KidneyTransplant result = service.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDonorBloodGroup()).isEqualTo("A+");
        assertThat(result.getColdIschemiaTime()).isEqualTo(18);
    }

    @Test
    @DisplayName("findById - lève exception si transplant introuvable")
    void findById_notFound_throwsException() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Not found");
    }

    // ─── delete ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete - appelle deleteById avec le bon ID")
    void delete_callsRepository() {
        doNothing().when(repo).deleteById(1L);

        service.delete(1L);

        verify(repo).deleteById(1L);
    }

    @Test
    @DisplayName("delete - n'appelle pas findById avant suppression")
    void delete_doesNotCallFindById() {
        doNothing().when(repo).deleteById(1L);

        service.delete(1L);

        verify(repo, never()).findById(anyLong());
    }
}
