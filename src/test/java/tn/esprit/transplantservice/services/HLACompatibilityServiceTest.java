package tn.esprit.transplantservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.transplantservice.entities.CrossMatchResult;
import tn.esprit.transplantservice.entities.HLACompatibility;
import tn.esprit.transplantservice.entities.KidneyTransplant;
import tn.esprit.transplantservice.repositories.HLACompatibilityRepository;
import tn.esprit.transplantservice.repositories.KidneyTransplantRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HLACompatibilityService - Tests unitaires")
class HLACompatibilityServiceTest {

    @Mock private HLACompatibilityRepository repo;
    @Mock private KidneyTransplantRepository transplantRepo;

    @InjectMocks
    private HLACompatibilityService service;

    private KidneyTransplant transplant;
    private HLACompatibility hla;

    @BeforeEach
    void setUp() {
        transplant = new KidneyTransplant();
        transplant.setId(1L);

        hla = new HLACompatibility();
        hla.setId(10L);
        hla.setDonorHlaA("A1,A2");
        hla.setDonorHlaB("B7,B8");
        hla.setDonorHlaDr("DR3,DR4");
        hla.setRecipientHlaA("A1,A3");      // 1 mismatch (A2 not in recipient)
        hla.setRecipientHlaB("B7,B8");      // 0 mismatches
        hla.setRecipientHlaDr("DR5,DR6");   // 2 mismatches (DR3 and DR4 absent)
        hla.setCrossMatchResult(CrossMatchResult.NEGATIVE);
    }

    // ─── addOrUpdateForTransplant ─────────────────────────────────────────────

    @Test
    @DisplayName("addOrUpdateForTransplant - crée un nouveau HLA si aucun existant")
    void addOrUpdate_newHla_savesWithMismatches() {
        transplant.setHlaCompatibility(null);   // no existing HLA
        when(transplantRepo.findById(1L)).thenReturn(Optional.of(transplant));

        HLACompatibility saved = new HLACompatibility();
        saved.setId(10L);
        saved.setDonorHlaA("A1,A2");
        saved.setRecipientHlaA("A1,A3");
        saved.setNumberOfMismatches(3);         // expected: 1 (A) + 0 (B) + 2 (DR)
        saved.setTransplant(transplant);
        when(repo.save(any(HLACompatibility.class))).thenReturn(saved);

        HLACompatibility result = service.addOrUpdateForTransplant(1L, hla);

        assertThat(result).isNotNull();
        // numberOfMismatches is set before save — verify save was called
        verify(repo).save(argThat(h -> h.getNumberOfMismatches() == 3));
        verify(transplantRepo).findById(1L);
    }

    @Test
    @DisplayName("addOrUpdateForTransplant - met à jour le HLA existant (conserve l'ID)")
    void addOrUpdate_existingHla_updatesId() {
        HLACompatibility existing = new HLACompatibility();
        existing.setId(99L);
        transplant.setHlaCompatibility(existing);

        when(transplantRepo.findById(1L)).thenReturn(Optional.of(transplant));
        when(repo.save(any(HLACompatibility.class))).thenAnswer(inv -> inv.getArgument(0));

        HLACompatibility result = service.addOrUpdateForTransplant(1L, hla);

        // The HLA should have gotten the existing ID
        assertThat(result.getId()).isEqualTo(99L);
    }

    @Test
    @DisplayName("addOrUpdateForTransplant - lève exception si transplant introuvable")
    void addOrUpdate_transplantNotFound_throwsException() {
        when(transplantRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addOrUpdateForTransplant(99L, hla))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Transplant not found with id: 99");

        verify(repo, never()).save(any());
    }

    // ─── calculateMismatches (via addOrUpdateForTransplant) ──────────────────

    @Test
    @DisplayName("calcul mismatches - 0 incompatibilité si HLA identiques")
    void mismatch_perfectMatch_returns0() {
        HLACompatibility perfect = new HLACompatibility();
        perfect.setDonorHlaA("A1,A2");
        perfect.setDonorHlaB("B7,B8");
        perfect.setDonorHlaDr("DR3,DR4");
        perfect.setRecipientHlaA("A1,A2");
        perfect.setRecipientHlaB("B7,B8");
        perfect.setRecipientHlaDr("DR3,DR4");
        perfect.setCrossMatchResult(CrossMatchResult.NEGATIVE);

        transplant.setHlaCompatibility(null);
        when(transplantRepo.findById(1L)).thenReturn(Optional.of(transplant));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.addOrUpdateForTransplant(1L, perfect);

        verify(repo).save(argThat(h -> h.getNumberOfMismatches() == 0));
    }

    @Test
    @DisplayName("calcul mismatches - maximum 2 par locus (plafond à 6 total)")
    void mismatch_allDifferent_capsAt2PerLocus() {
        HLACompatibility worst = new HLACompatibility();
        worst.setDonorHlaA("A1,A2");
        worst.setDonorHlaB("B7,B8");
        worst.setDonorHlaDr("DR3,DR4");
        worst.setRecipientHlaA("A3,A4");    // 2 mismatches → capped at 2
        worst.setRecipientHlaB("B9,B10");   // 2 mismatches → capped at 2
        worst.setRecipientHlaDr("DR5,DR6"); // 2 mismatches → capped at 2
        worst.setCrossMatchResult(CrossMatchResult.POSITIVE);

        transplant.setHlaCompatibility(null);
        when(transplantRepo.findById(1L)).thenReturn(Optional.of(transplant));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.addOrUpdateForTransplant(1L, worst);

        verify(repo).save(argThat(h -> h.getNumberOfMismatches() == 6));
    }

    @Test
    @DisplayName("calcul mismatches - null values comptent 0")
    void mismatch_nullAlleles_counted0() {
        HLACompatibility withNulls = new HLACompatibility();
        withNulls.setDonorHlaA(null);
        withNulls.setDonorHlaB("B7");
        withNulls.setDonorHlaDr(null);
        withNulls.setRecipientHlaA("A1");
        withNulls.setRecipientHlaB("B8");   // 1 mismatch
        withNulls.setRecipientHlaDr("DR3");
        withNulls.setCrossMatchResult(CrossMatchResult.NEGATIVE);

        transplant.setHlaCompatibility(null);
        when(transplantRepo.findById(1L)).thenReturn(Optional.of(transplant));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.addOrUpdateForTransplant(1L, withNulls);

        verify(repo).save(argThat(h -> h.getNumberOfMismatches() == 1));
    }

    // ─── findById ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findById - retourne le HLA existant")
    void findById_found_returnsHla() {
        when(repo.findById(10L)).thenReturn(Optional.of(hla));

        HLACompatibility result = service.findById(10L);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getDonorHlaA()).isEqualTo("A1,A2");
    }

    @Test
    @DisplayName("findById - lève exception si introuvable")
    void findById_notFound_throwsException() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("HLA not found");
    }

    // ─── getByTransplantId ────────────────────────────────────────────────────

    @Test
    @DisplayName("getByTransplantId - retourne la liste des HLA d'un transplant")
    void getByTransplantId_returnsList() {
        when(repo.findByTransplantId(1L)).thenReturn(List.of(hla));

        List<HLACompatibility> result = service.getByTransplantId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(10L);
    }
}
