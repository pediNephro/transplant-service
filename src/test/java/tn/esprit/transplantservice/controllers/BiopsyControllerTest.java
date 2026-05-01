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
import tn.esprit.transplantservice.entities.BanffCategory;
import tn.esprit.transplantservice.entities.Biopsy;
import tn.esprit.transplantservice.entities.BiopsyIndication;
import tn.esprit.transplantservice.entities.KidneyTransplant;
import tn.esprit.transplantservice.services.BiopsyService;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BiopsyController.class)
@DisplayName("BiopsyController - Tests d'intégration (MockMvc)")
class BiopsyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BiopsyService service;

    private ObjectMapper objectMapper;
    private Biopsy biopsy;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        KidneyTransplant transplant = new KidneyTransplant();
        transplant.setId(10L);

        biopsy = Biopsy.builder()
                .id(1L)
                .biopsyDate(LocalDate.of(2026, 2, 1))
                .indication(BiopsyIndication.SUSPECTED_REJECTION)
                .banffScoreT(2)
                .banffScoreI(1)
                .banffCategory(BanffCategory.REJECTION)
                .conclusion("Rejet aigu confirmé")
                .build();
    }

    // ─── POST /api/biopsies ───────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/biopsies - 200 OK avec la biopsie créée")
    void create_success() throws Exception {
        when(service.save(any(Biopsy.class))).thenReturn(biopsy);

        mockMvc.perform(post("/api/biopsies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(biopsy)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.indication").value("SUSPECTED_REJECTION"))
                .andExpect(jsonPath("$.banffCategory").value("REJECTION"))
                .andExpect(jsonPath("$.conclusion").value("Rejet aigu confirmé"));
    }

    // ─── GET /api/biopsies ────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/biopsies - 200 OK avec liste de biopsies")
    void getAll_returnsList() throws Exception {
        Biopsy b2 = Biopsy.builder()
                .id(2L)
                .indication(BiopsyIndication.PROTOCOL)
                .banffCategory(BanffCategory.NORMAL)
                .build();

        when(service.findAll()).thenReturn(List.of(biopsy, b2));

        mockMvc.perform(get("/api/biopsies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    @DisplayName("GET /api/biopsies - 200 OK avec liste vide")
    void getAll_empty_returns200() throws Exception {
        when(service.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/biopsies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ─── GET /api/biopsies/{id} ───────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/biopsies/{id} - 200 OK si biopsie trouvée")
    void getById_found_returns200() throws Exception {
        when(service.findById(1L)).thenReturn(biopsy);

        mockMvc.perform(get("/api/biopsies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.banffScoreT").value(2));
    }

    @Test
    @DisplayName("GET /api/biopsies/{id} - exception si biopsie introuvable")
    void getById_notFound_throwsException() {
        when(service.findById(99L)).thenThrow(new RuntimeException("Biopsy not found"));

        assertThatThrownBy(() -> mockMvc.perform(get("/api/biopsies/99")))
                .hasMessageContaining("Biopsy not found");
    }

    // ─── PUT /api/biopsies/{id} ───────────────────────────────────────────────

    @Test
    @DisplayName("PUT /api/biopsies/{id} - 200 OK avec biopsie mise à jour")
    void update_success_returns200() throws Exception {
        Biopsy updated = Biopsy.builder()
                .id(1L)
                .indication(BiopsyIndication.PROTOCOL)
                .banffCategory(BanffCategory.BORDERLINE)
                .banffScoreT(1)
                .conclusion("Borderline")
                .build();

        when(service.save(any(Biopsy.class))).thenReturn(updated);

        mockMvc.perform(put("/api/biopsies/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.banffCategory").value("BORDERLINE"))
                .andExpect(jsonPath("$.conclusion").value("Borderline"));
    }

    // ─── DELETE /api/biopsies/{id} ────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /api/biopsies/{id} - 200 OK après suppression")
    void delete_success_returns200() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/biopsies/1"))
                .andExpect(status().isOk());

        verify(service).delete(1L);
    }

    // ─── GET /api/biopsies/transplant/{transplantId} ──────────────────────────

    @Test
    @DisplayName("GET /api/biopsies/transplant/{id} - 200 OK avec biopsies du transplant")
    void getByTransplant_returns200() throws Exception {
        when(service.getByTransplantId(10L)).thenReturn(List.of(biopsy));

        mockMvc.perform(get("/api/biopsies/transplant/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].indication").value("SUSPECTED_REJECTION"));
    }

    @Test
    @DisplayName("GET /api/biopsies/transplant/{id} - 200 OK liste vide si aucune biopsie")
    void getByTransplant_empty_returns200() throws Exception {
        when(service.getByTransplantId(99L)).thenReturn(List.of());

        mockMvc.perform(get("/api/biopsies/transplant/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
