package com.epam.lab_experiment;

import com.epam.lab_experiment.model.Experiment;
import com.epam.lab_experiment.repository.ExperimentRepository;
import com.epam.lab_experiment.util.JsonUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static com.epam.lab_experiment.util.TestDataUtil.UNSAVED_EXPERIMENT;
import static com.epam.lab_experiment.web.Utils.EXPERIMENTS_ENDPOINT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LabExperimentIntegrationTest {

    public static final String POSTGRES_IMAGE_NAME = "postgres:17.5";

    private static final String DB_NAME = "testdb";
    private static final String DB_USER = "test-user";
    private static final String PASSWORD = "changeit";

    @Autowired
    private ExperimentRepository repository;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private JsonUtil jsonUtil;

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER =
            new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_IMAGE_NAME))
                    .withDatabaseName(DB_NAME)
                    .withUsername(DB_USER)
                    .withPassword(PASSWORD);

    @DisplayName("Should save a new experiment record to database")
    @Test
    void shouldSaveExperimentToDatabase() throws Exception {
        Experiment experiment = UNSAVED_EXPERIMENT;

        mvc.perform(post(EXPERIMENTS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUtil.toJson(experiment))
        ).andExpect(status().isCreated());

        Iterable<Experiment> savedExperiments = repository.findAll();

        assertThat(savedExperiments)
                .hasSize(1)
                .first()
                .satisfies(exp -> {
                    assertThat(exp.getId()).isNotNull();
                    assertThat(exp.getTitle()).isEqualTo(experiment.getTitle());
                    assertThat(exp.getLeadResearcher()).isEqualTo(experiment.getLeadResearcher());
                    assertThat(exp.getMethod()).isEqualTo(experiment.getMethod());
                    assertThat(exp.getCategory()).isEqualTo(experiment.getCategory());
                    assertThat(exp.getStatus()).isEqualTo(experiment.getStatus());
                    assertThat(exp.getStartDate()).isEqualTo(experiment.getStartDate());
                });

    }
}
