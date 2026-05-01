package tn.esprit.transplantservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.transplantservice.entities.CrossMatchResult;
import tn.esprit.transplantservice.entities.DonorType;
import tn.esprit.transplantservice.entities.GraftStatus;
import tn.esprit.transplantservice.entities.HLACompatibility;
import tn.esprit.transplantservice.entities.KidneyTransplant;
import tn.esprit.transplantservice.entities.SurveillancePhase;
import tn.esprit.transplantservice.entities.SurveillanceProtocol;
import tn.esprit.transplantservice.services.HLACompatibilityService;
import tn.esprit.transplantservice.services.KidneyTransplantService;
import tn.esprit.transplantservice.services.SurveillanceProtocolService;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = KidneyTransplantController.class)
@DisplayName("KidneyTransplantController - Tests d'intégration (MockMvc)")
class KidneyTransplantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private KidneyTransplantService service;
    @MockitoBean private HLACompatibilityService hlaService;
    @MockitoBean private SurveillanceProtocolService protocolService;

    private ObjectMapper objectMapper;
    private KidneyTransplant transplant;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

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

    // ─── POST /api/transplants ────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/transplants - 200 OK avec le transplant créé")
    void create_success() throws Exception {
        when(service.save(any(KidneyTransplant.class))).thenReturn(transplant);

        mockMvc.perform(post("/api/transplants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transplant)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.patientId").value(100))
                .andExpect(jsonPath("$.donorType").value("DECEASED"))
                .andExpect(jsonPath("$.graftStatus").value("ACTIVE"));
    }

    // ─── GET /api/transplants ─────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/transplants - 200 OK avec liste de transplants")
    void getAll_returnsList() throws Exception {
        KidneyTransplant t2 = KidneyTransplant.builder()
                .id(2L)
                .patientId(101L)
                .transplantDate(LocalDate.of(2025, 8, 5))
                .donorType(DonorType.LIVING)
                .graftStatus(GraftStatus.ACTIVE)
                .build();

        when(service.findAll()).thenReturn(List.of(transplant, t2));

        mockMvc.perform(get("/api/transplants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    @DisplayName("GET /api/transplants - 200 OK avec liste vide")
    void getAll_empty_returns200() throws Exception {
        when(service.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/transplants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ─── GET /api/transplants/{id} ────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/transplants/{id} - 200 OK si transplant trouvé")
    void getById_found_returns200() throws Exception {
        when(service.findById(1L)).thenReturn(transplant);

        mockMvc.perform(get("/api/transplants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.donorBloodGroup").value("A+"))
                .andExpect(jsonPath("$.coldIschemiaTime").value(18));
    }

    @Test
    @DisplayName("GET /api/transplants/{id} - exception si transplant introuvable")
    void getById_notFound_throwsException() {
        when(service.findById(99L)).thenThrow(new RuntimeException("Not found"));

        assertThatThrownBy(() -> mockMvc.perform(get("/api/transplants/99")))
                .hasMessageContaining("Not found");
    }

    // ─── PUT /api/transplants/{id} ────────────────────────────────────────────

    @Test
    @DisplayName("PUT /api/transplants/{id} - 200 OK avec transplant mis à jour")
    void update_success_returns200() throws Exception {
        KidneyTransplant updated = KidneyTransplant.builder()
                .id(1L)
                .patientId(100L)
                .transplantDate(LocalDate.of(2026, 1, 10))
                .donorType(DonorType.DECEASED)
                .graftStatus(GraftStatus.LOST)
                .build();

        when(service.save(any(KidneyTransplant.class))).thenReturn(updated);

        mockMvc.perform(put("/api/transplants/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.graftStatus").value("LOST"));
    }

    // ─── DELETE /api/transplants/{id} ─────────────────────────────────────────

    @Test
    @DisplayName("DELETE /api/transplants/{id} - 200 OK après suppression")
    void delete_success_returns200() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/transplants/1"))
                .andExpect(status().isOk());

        verify(service).delete(1L);
    }

    // ─── POST /api/transplants/{id}/surveillance-protocol/generate ────────────

    @Test
    @DisplayName("POST /{id}/surveillance-protocol/generate - génère le protocole par défaut")
    void generateProtocol_success() throws Exception {
        SurveillanceProtocol protocol = SurveillanceProtocol.builder()
                .id(5L)
                .transplant(transplant)
                .currentPhase(SurveillancePhase.IMMEDIATE)
                .consultationFrequency(7)
                .labTestFrequency(2)
                .nextConsultationDate(LocalDate.of(2026, 1, 17))
                .nextLabTestDate(LocalDate.of(2026, 1, 12))
                .completionPercentage(0.0)
                .build();

        when(protocolService.generateDefaultProtocol(1L)).thenReturn(protocol);

        mockMvc.perform(post("/api/transplants/1/surveillance-protocol/generate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.currentPhase").value("IMMEDIATE"))
                .andExpect(jsonPath("$.consultationFrequency").value(7))
                .andExpect(jsonPath("$.completionPercentage").value(0.0));
    }

    @Test
    @DisplayName("POST /{id}/surveillance-protocol/generate - exception si transplant introuvable")
    void generateProtocol_transplantNotFound_throwsException() {
        when(protocolService.generateDefaultProtocol(99L))
                .thenThrow(new RuntimeException("Transplant not found"));

        assertThatThrownBy(() ->
                mockMvc.perform(post("/api/transplants/99/surveillance-protocol/generate")))
                .hasMessageContaining("Transplant not found");
    }

    // ─── POST /api/transplants/{id}/hla-compatibility ─────────────────────────

    @Test
    @DisplayName("POST /{id}/hla-compatibility - crée / met à jour la compatibilité HLA")
    void addHLA_success() throws Exception {
        HLACompatibility hla = new HLACompatibility();
        hla.setId(10L);
        hla.setDonorHlaA("A1,A2");
        hla.setDonorHlaB("B7,B8");
        hla.setDonorHlaDr("DR3,DR4");
        hla.setRecipientHlaA("A1,A3");
        hla.setRecipientHlaB("B7,B8");
        hla.setRecipientHlaDr("DR5,DR6");
        hla.setCrossMatchResult(CrossMatchResult.NEGATIVE);
        hla.setNumberOfMismatches(3);

        when(hlaService.addOrUpdateForTransplant(eq(1L), any(HLACompatibility.class)))
                .thenReturn(hla);

        mockMvc.perform(post("/api/transplants/1/hla-compatibility")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hla)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.numberOfMismatches").value(3))
                .andExpect(jsonPath("$.crossMatchResult").value("NEGATIVE"));
    }

    @Test
    @DisplayName("POST /{id}/hla-compatibility - exception si transplant introuvable")
    void addHLA_transplantNotFound_throwsException() {
        when(hlaService.addOrUpdateForTransplant(eq(99L), any()))
                .thenThrow(new RuntimeException("Transplant not found with id: 99"));

        assertThatThrownBy(() ->
                mockMvc.perform(post("/api/transplants/99/hla-compatibility")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new HLACompatibility()))))
                .hasMessageContaining("Transplant not found with id: 99");
    }
}
