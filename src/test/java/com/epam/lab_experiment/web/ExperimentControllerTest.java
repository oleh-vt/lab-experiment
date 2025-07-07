package com.epam.lab_experiment.web;

import com.epam.lab_experiment.exception.ExperimentNotFoundException;
import com.epam.lab_experiment.model.Experiment;
import com.epam.lab_experiment.service.ExperimentService;
import com.epam.lab_experiment.util.JsonUtil;
import com.epam.lab_experiment.util.TestDataUtil;
import jakarta.validation.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static com.epam.lab_experiment.util.TestDataUtil.*;
import static com.epam.lab_experiment.web.Utils.EXPERIMENTS_ENDPOINT;
import static com.epam.lab_experiment.web.Utils.EXPERIMENT_ID_ENDPOINT;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureJsonTesters
@WebMvcTest(ExperimentController.class)
@ComponentScan(basePackages = "com.epam.lab_experiment")
class ExperimentControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JsonUtil jsonUtil;

    @MockitoBean
    private ExperimentService service;

    @DisplayName("Should return result with default pagination offset = 0, size = 10, sort = id, DESC")
    @Test
    void shouldReturnPagedResult() throws Exception {
        List<Experiment> experiments = List.of(EXPERIMENT_1, EXPERIMENT_2);
        int pageNumber = 0;
        int pageSize = 10;
        Pageable defaultPagination = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        doReturn(new PageImpl<>(experiments, defaultPagination, experiments.size()))
                .when(service).findAll(any(Experiment.class), eq(defaultPagination));

        mvc.perform(get(EXPERIMENTS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.content", hasSize(experiments.size())),
                        jsonPath("$.page", equalTo(pageNumber)),
                        jsonPath("$.size", equalTo(pageSize)),
                        jsonPath("$.totalPages", equalTo(1)),
                        jsonPath("$.totalElements", equalTo(experiments.size()))
        );

    }

    @DisplayName("Should return 201 Created and the saved experiment")
    @Test
    void shouldReturn201CreatedAndTheSavedExperiment() throws Exception {
        Experiment experiment = UNSAVED_EXPERIMENT;

        Experiment savedExperiment = TestDataUtil.experimentBuilder(experiment)
                .id(1L)
                .build();

        doReturn(savedExperiment).when(service).save(experiment);

        mvc.perform(post(EXPERIMENTS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonUtil.toJson(experiment))
                )
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonUtil.toJson(savedExperiment)));
    }

    @DisplayName("When required fields not provided, should return 400 Bad Request and validation message")
    @Test
    void shouldReturn400BadRequestWhenMandatoryFieldsNotProvided() throws Exception {
        Experiment experiment = TestDataUtil.experimentBuilder()
                .title("  ")
                .leadResearcher("")
                .method(null)
                .status(null)
                .category("")
                .build();

        mvc.perform(post(EXPERIMENTS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonUtil.toJson(experiment)))
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
        long id = 2;
        LocalDate startDate = LocalDate.of(2025, 12, 1);

        Experiment updatedExperiment = TestDataUtil.experimentBuilder(EXPERIMENT_2)
                .startDate(startDate)
                .build();

        Experiment requestBody = TestDataUtil.experimentBuilder()
                .startDate(startDate)
                .build();

        doReturn(updatedExperiment).when(service).update(id, requestBody);

        mvc.perform(
                put(EXPERIMENT_ID_ENDPOINT, id)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonUtil.toJson(requestBody))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(jsonUtil.toJson(updatedExperiment)));
    }

    @DisplayName("When experiment to update does not exist, should return 404 Not Found")
    @Test
    void whenExperimentIdDoesNotExistShouldReturn404NotFound() throws Exception{
        doThrow(new ExperimentNotFoundException(ID)).when(service).update(eq(ID), any(Experiment.class));

        mvc.perform(
                put(EXPERIMENT_ID_ENDPOINT, ID)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonUtil.toJson(new Experiment()))
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("When experiment update values are invalid, should return 400 Bad Request")
    @Test
    void whenExperimentUpdateValuesInvalidShouldReturn400BadRequest() throws Exception{
        doThrow(new ConstraintViolationException(createConstraintViolations()))
                .when(service).update(anyLong(), any(Experiment.class));

        mvc.perform(
                put(EXPERIMENT_ID_ENDPOINT, ID)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonUtil.toJson(new Experiment()))
                )
                .andExpect(status().isBadRequest());
    }


    @DisplayName("Should delete experiment by id and return 204 No Content")
    @Test
    void shouldDeleteExperimentById() throws Exception {
        doNothing().when(service).delete(ID);

        mvc.perform(delete(EXPERIMENT_ID_ENDPOINT, ID))
                .andExpect(status().isNoContent());
    }

    @DisplayName("When experiment to delete does not exist, should return 404 Not Found")
    @Test
    void whenExperimentDoesNotExistShouldReturn404NotFound() throws Exception {
        doThrow(new ExperimentNotFoundException(ID)).when(service).delete(eq(ID));

        mvc.perform(delete(EXPERIMENT_ID_ENDPOINT, ID))
                .andExpect(status().isNotFound());
    }

    private Set<ConstraintViolation<Experiment>> createConstraintViolations() {
        Experiment exp = TestDataUtil.experimentBuilder(EXPERIMENT_1)
                .title("  ")
                .method(null)
                .build();
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = validatorFactory.getValidator();
            return validator.validate(exp);
        }
    }

}
