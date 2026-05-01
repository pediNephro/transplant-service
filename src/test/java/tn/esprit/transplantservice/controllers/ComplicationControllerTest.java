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
import tn.esprit.transplantservice.entities.Complication;
import tn.esprit.transplantservice.entities.ComplicationStatus;
import tn.esprit.transplantservice.entities.ComplicationType;
import tn.esprit.transplantservice.entities.KidneyTransplant;
import tn.esprit.transplantservice.entities.Severity;
import tn.esprit.transplantservice.services.ComplicationService;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ComplicationController.class)
@DisplayName("ComplicationController - Tests d'intégration (MockMvc)")
class ComplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ComplicationService service;

    private ObjectMapper objectMapper;
    private Complication complication;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        KidneyTransplant transplant = new KidneyTransplant();
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

    // ─── POST /api/complications ──────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/complications - 200 OK avec la complication créée")
    void create_success() throws Exception {
        when(service.save(any(Complication.class))).thenReturn(complication);

        mockMvc.perform(post("/api/complications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(complication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.complicationType").value("INFECTIOUS"))
                .andExpect(jsonPath("$.subType").value("CMV"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    // ─── GET /api/complications ───────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/complications - 200 OK avec liste de complications")
    void getAll_returnsList() throws Exception {
        Complication c2 = Complication.builder()
                .id(2L)
                .complicationType(ComplicationType.VASCULAR)
                .severity(Severity.SEVERE)
                .status(ComplicationStatus.RESOLVED)
                .build();

        when(service.findAll()).thenReturn(List.of(complication, c2));

        mockMvc.perform(get("/api/complications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    @DisplayName("GET /api/complications - 200 OK avec liste vide")
    void getAll_empty_returns200() throws Exception {
        when(service.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/complications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ─── GET /api/complications/{id} ──────────────────────────────────────────

    @Test
    @DisplayName("GET /api/complications/{id} - 200 OK si complication trouvée")
    void getById_found_returns200() throws Exception {
        when(service.findById(1L)).thenReturn(complication);

        mockMvc.perform(get("/api/complications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.severity").value("MODERATE"))
                .andExpect(jsonPath("$.treatment").value("Valganciclovir"));
    }

    @Test
    @DisplayName("GET /api/complications/{id} - exception si complication introuvable")
    void getById_notFound_throwsException() {
        when(service.findById(99L)).thenThrow(new RuntimeException("Complication not found"));

        assertThatThrownBy(() -> mockMvc.perform(get("/api/complications/99")))
                .hasMessageContaining("Complication not found");
    }

    // ─── PUT /api/complications/{id} ──────────────────────────────────────────

    @Test
    @DisplayName("PUT /api/complications/{id} - 200 OK avec complication mise à jour")
    void update_success_returns200() throws Exception {
        Complication updated = Complication.builder()
                .id(1L)
                .complicationType(ComplicationType.INFECTIOUS)
                .severity(Severity.MILD)
                .status(ComplicationStatus.RESOLVED)
                .build();

        when(service.save(any(Complication.class))).thenReturn(updated);

        mockMvc.perform(put("/api/complications/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESOLVED"))
                .andExpect(jsonPath("$.severity").value("MILD"));
    }

    // ─── DELETE /api/complications/{id} ───────────────────────────────────────

    @Test
    @DisplayName("DELETE /api/complications/{id} - 200 OK après suppression")
    void delete_success_returns200() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/complications/1"))
                .andExpect(status().isOk());

        verify(service).delete(1L);
    }

    // ─── GET /api/complications/transplant/{transplantId} ─────────────────────

    @Test
    @DisplayName("GET /transplant/{transplantId} - 200 OK avec complications du transplant")
    void getByTransplantId_returnsList() throws Exception {
        when(service.findByTransplantId(10L)).thenReturn(List.of(complication));

        mockMvc.perform(get("/api/complications/transplant/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].complicationType").value("INFECTIOUS"));
    }

    @Test
    @DisplayName("GET /transplant/{transplantId} - 200 OK liste vide si aucune complication")
    void getByTransplantId_empty_returns200() throws Exception {
        when(service.findByTransplantId(99L)).thenReturn(List.of());

        mockMvc.perform(get("/api/complications/transplant/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ─── POST /api/complications/transplant/{transplantId} ────────────────────

    @Test
    @DisplayName("POST /transplant/{transplantId} - 200 OK crée complication liée au transplant")
    void createWithTransplant_success() throws Exception {
        Complication input = Complication.builder()
                .complicationType(ComplicationType.UROLOGICAL)
                .severity(Severity.MILD)
                .status(ComplicationStatus.ACTIVE)
                .build();

        Complication saved = Complication.builder()
                .id(3L)
                .complicationType(ComplicationType.UROLOGICAL)
                .severity(Severity.MILD)
                .status(ComplicationStatus.ACTIVE)
                .build();

        when(service.saveWithTransplant(any(Complication.class), eq(10L))).thenReturn(saved);

        mockMvc.perform(post("/api/complications/transplant/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.complicationType").value("UROLOGICAL"));
    }

    @Test
    @DisplayName("POST /transplant/{transplantId} - exception si transplant introuvable")
    void createWithTransplant_transplantNotFound_throwsException() {
        when(service.saveWithTransplant(any(Complication.class), eq(99L)))
                .thenThrow(new RuntimeException("Transplant not found"));

        assertThatThrownBy(() ->
                mockMvc.perform(post("/api/complications/transplant/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Complication()))))
                .hasMessageContaining("Transplant not found");
    }
}
