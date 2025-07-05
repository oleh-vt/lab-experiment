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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Collections;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureJsonTesters
@WebMvcTest(ExperimentController.class)
class ExperimentControllerTest {

    private static final String EXPERIMENTS_ENDPOINT = "/experiments";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<Experiment> jacksonTester;

    @MockitoBean
    private ExperimentRepository repository;

    @Test
    void contextLoads() throws Exception {
        doReturn(Collections.emptyList()).when(repository).findAll();

        mvc.perform(get(EXPERIMENTS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
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

    private String toJson(Experiment experiment) {
        try {
            return jacksonTester.write(experiment).getJson();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
