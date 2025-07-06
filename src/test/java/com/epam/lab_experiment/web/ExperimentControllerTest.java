package com.epam.lab_experiment.web;

import com.epam.lab_experiment.model.Experiment;
import com.epam.lab_experiment.model.ExperimentStatus;
import com.epam.lab_experiment.repository.ExperimentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureJsonTesters
@WebMvcTest(ExperimentController.class)
class ExperimentControllerTest {

    private static final String EXPERIMENTS_ENDPOINT = "/experiments";
    private static final String EXPERIMENT_ID_ENDPOINT = "/experiments/{id}";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<Experiment> jacksonTester;

    @MockitoBean
    private ExperimentRepository repository;

    @DisplayName("Should return result with default pagination offset = 0, size = 10, sort = id, DESC")
    @Test
    void shouldReturnPagedResult() throws Exception {
        List<Experiment> experiments = sampleExperiments();
        int pageNumber = 0;
        int pageSize = 10;
        Pageable defaultPagination = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        doReturn(new PageImpl<>(experiments, defaultPagination, experiments.size()))
                .when(repository).findAll(any(Specification.class), eq(defaultPagination));

        mvc.perform(get(EXPERIMENTS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(experiments.size())))
                .andExpect(jsonPath("$.number").value(pageNumber))
                .andExpect(jsonPath("$.size").value(pageSize));
    }

    @DisplayName("Should return 201 Created and the saved experiment")
    @Test
    void shouldReturn201CreatedAndTheSavedExperiment() throws Exception {
        Experiment experiment = Utils.experimentBuilder()
                .title("Drug X effect on cancer cells")
                .leadResearcher("Dr. Alice Morgan")
                .method("Cell Viability")
                .status(ExperimentStatus.PLANNED)
                .category("In Vitro")
                .build();

        Experiment savedExperiment = Utils.experimentBuilder(experiment)
                .id(1L)
                .build();

        doReturn(savedExperiment).when(repository).save(experiment);

        mvc.perform(post(EXPERIMENTS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(toJson(experiment))
                )
                .andExpect(status().isCreated())
                .andExpect(content().json(toJson(savedExperiment)));
    }

    @DisplayName("When required fields not provided, should return 400 Bad Request and validation message")
    @Test
    void shouldReturn400BadRequestWhenMandatoryFieldsNotProvided() throws Exception {
        Experiment experiment = Utils.experimentBuilder()
                .title("  ")
                .leadResearcher("")
                .method(null)
                .status(null)
                .category("")
                .build();

        mvc.perform(post(EXPERIMENTS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(toJson(experiment))
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        content().string(
                                allOf(
                                        containsString("Title is not provided"),
                                        containsString("Lead Researcher is not provided"),
                                        containsString("Method is not provided"),
                                        containsString("Experiment status is not provided"),
                                        containsString("Category is not provided")
                                ))
                );

    }

    @DisplayName("When the experiment exists, should update the experiment and return 200 OK")
    @Test
    void shouldUpdateExistingExperiment() throws Exception {
        long id = 1;
        LocalDate startDate = LocalDate.of(2025, 12, 1);

        Experiment existingExperiment = Utils.experimentBuilder()
                .id(id)
                .title("Drug X effect on cancer cells")
                .leadResearcher("Dr. Alice Morgan")
                .method("Cell Viability")
                .status(ExperimentStatus.PLANNED)
                .category("In Vitro")
                .build();
        Experiment updatedExperiment = Utils.experimentBuilder(existingExperiment)
                .startDate(startDate)
                .build();

        Experiment requestBody = Utils.experimentBuilder()
                .startDate(startDate)
                .build();

        doReturn(Optional.of(existingExperiment)).when(repository).findById(id);
        doReturn(updatedExperiment).when(repository).save(updatedExperiment);

        mvc.perform(
                put(EXPERIMENT_ID_ENDPOINT, id)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(toJson(requestBody))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(updatedExperiment)));
    }

    @DisplayName("When experiment id does not exist, should return 404 Not Found")
    @Test
    void whenExperimentIdDoesNotExistShouldReturn404NotFound() throws Exception{
        long id = 1;
        doReturn(Optional.empty()).when(repository).findById(id);

        mvc.perform(
                put(EXPERIMENT_ID_ENDPOINT, id)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(toJson(new Experiment()))
                )
                .andExpect(status().isNotFound());
    }


    @DisplayName("Should delete experiment by id and return 204 No Content")
    @Test
    void shouldDeleteExperimentById() throws Exception {
        long id = 1;
        Experiment exp = Utils.experimentBuilder()
                .id(id)
                .build();
        doReturn(Optional.of(exp)).when(repository).findById(id);
        doNothing().when(repository).delete(exp);

        mvc.perform(delete(EXPERIMENT_ID_ENDPOINT, id))
                .andExpect(status().isNoContent());
    }

    @DisplayName("When experiment does not exist, should return 404 Not Found")
    @Test
    void whenExperimentDoesNotExistShouldReturn404NotFound() throws Exception {
        long id = 1;
        doReturn(Optional.empty()).when(repository).findById(id);

        mvc.perform(delete(EXPERIMENT_ID_ENDPOINT, id))
                .andExpect(status().isNotFound());
    }

    private String toJson(Experiment experiment) {
        try {
            return jacksonTester.write(experiment).getJson();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Experiment> sampleExperiments() {
        Experiment exp1 = Utils.experimentBuilder()
                .id(1L)
                .title("COVID-19 Vaccine Efficacy Study")
                .leadResearcher("Dr. Lena Hofmann")
                .method("Randomized Control Trial")
                .status(ExperimentStatus.COMPLETED)
                .category("Immunology")
                .startDate(LocalDate.of(2024, 11, 20))
                .build();

        Experiment exp2 = Utils.experimentBuilder()
                .id(2L)
                .title("Protein Folding Simulation")
                .leadResearcher("Dr. Alice Morgan")
                .method("Molecular Dynamics")
                .status(ExperimentStatus.PLANNED)
                .category("Biophysics")
                .startDate(LocalDate.of(2025, 9, 1))
                .build();

        return List.of(exp1, exp2);
    }

}
