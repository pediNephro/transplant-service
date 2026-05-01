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
import tn.esprit.transplantservice.entities.KidneyTransplant;
import tn.esprit.transplantservice.entities.RejectionEpisode;
import tn.esprit.transplantservice.entities.RejectionStatus;
import tn.esprit.transplantservice.entities.RejectionType;
import tn.esprit.transplantservice.entities.Severity;
import tn.esprit.transplantservice.services.RejectionEpisodeService;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RejectionEpisodeController.class)
@DisplayName("RejectionEpisodeController - Tests d'intégration (MockMvc)")
class RejectionEpisodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RejectionEpisodeService service;

    private ObjectMapper objectMapper;
    private RejectionEpisode episode;
    private KidneyTransplant transplant;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

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

    // ─── POST /api/rejections ─────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/rejections - 200 OK avec l'épisode créé")
    void create_success() throws Exception {
        when(service.save(any(RejectionEpisode.class))).thenReturn(episode);

        mockMvc.perform(post("/api/rejections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(episode)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.rejectionType").value("ACUTE"))
                .andExpect(jsonPath("$.severity").value("MODERATE"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    // ─── GET /api/rejections ──────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/rejections - 200 OK avec liste des épisodes")
    void getAll_returnsList() throws Exception {
        RejectionEpisode episode2 = RejectionEpisode.builder()
                .id(21L)
                .rejectionType(RejectionType.CHRONIC)
                .severity(Severity.MILD)
                .status(RejectionStatus.SUSPECTED)
                .transplant(transplant)
                .build();

        when(service.findAll()).thenReturn(List.of(episode, episode2));

        mockMvc.perform(get("/api/rejections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(20))
                .andExpect(jsonPath("$[1].id").value(21));
    }

    // ─── GET /api/rejections/{id} ─────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/rejections/{id} - 200 OK si épisode trouvé")
    void getById_found_returns200() throws Exception {
        when(service.findById(20L)).thenReturn(episode);

        mockMvc.perform(get("/api/rejections/20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.treatment").value("Bolus methylprednisolone"))
                .andExpect(jsonPath("$.creatinineIncrease").value(45.0));
    }

    @Test
    @DisplayName("GET /api/rejections/{id} - exception si épisode introuvable")
    void getById_notFound_throwsException() {
        when(service.findById(99L)).thenThrow(new RuntimeException("Rejection not found"));

        assertThatThrownBy(() -> mockMvc.perform(get("/api/rejections/99")))
                .hasMessageContaining("Rejection not found");
    }

    // ─── PUT /api/rejections/{id} ─────────────────────────────────────────────

    @Test
    @DisplayName("PUT /api/rejections/{id} - 200 OK avec épisode mis à jour")
    void update_success_returns200() throws Exception {
        RejectionEpisode updated = RejectionEpisode.builder()
                .id(20L)
                .rejectionType(RejectionType.ACUTE)
                .severity(Severity.SEVERE)
                .status(RejectionStatus.CONFIRMED)
                .treatment("Plasmaphérèse + Rituximab")
                .transplant(transplant)
                .build();

        when(service.save(any(RejectionEpisode.class))).thenReturn(updated);

        mockMvc.perform(put("/api/rejections/20")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.severity").value("SEVERE"))
                .andExpect(jsonPath("$.treatment").value("Plasmaphérèse + Rituximab"));

        // Verify that setId was applied (id from path gets set on the entity)
        verify(service).save(argThat(e -> e.getId() == 20L));
    }

    // ─── DELETE /api/rejections/{id} ─────────────────────────────────────────

    @Test
    @DisplayName("DELETE /api/rejections/{id} - 200 OK après suppression")
    void delete_success_returns200() throws Exception {
        doNothing().when(service).delete(20L);

        mockMvc.perform(delete("/api/rejections/20"))
                .andExpect(status().isOk());

        verify(service).delete(20L);
    }

    // ─── GET /api/rejections/transplant/{transplantId} ────────────────────────

    @Test
    @DisplayName("GET /transplant/{transplantId} - 200 OK avec épisodes du transplant")
    void getByTransplant_returnsList() throws Exception {
        RejectionEpisode episode2 = RejectionEpisode.builder()
                .id(21L)
                .rejectionType(RejectionType.CHRONIC)
                .severity(Severity.MILD)
                .status(RejectionStatus.RESOLVED)
                .transplant(transplant)
                .build();

        when(service.getByTransplantId(1L)).thenReturn(List.of(episode, episode2));

        mockMvc.perform(get("/api/rejections/transplant/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].status").value("CONFIRMED"))
                .andExpect(jsonPath("$[1].status").value("RESOLVED"));
    }

    @Test
    @DisplayName("GET /transplant/{transplantId} - 200 OK avec liste vide")
    void getByTransplant_noEpisodes_returnsEmptyList() throws Exception {
        when(service.getByTransplantId(99L)).thenReturn(List.of());

        mockMvc.perform(get("/api/rejections/transplant/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
