package com.epam.lab_experiment;

import com.epam.lab_experiment.model.Experiment;
import com.epam.lab_experiment.model.ExperimentStatus;
import com.epam.lab_experiment.repository.ExperimentRepository;
import com.epam.lab_experiment.util.JsonUtil;
import com.epam.lab_experiment.util.TestDataUtil;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;

import static com.epam.lab_experiment.util.TestDataUtil.ID;
import static com.epam.lab_experiment.util.TestDataUtil.UNSAVED_EXPERIMENT;
import static com.epam.lab_experiment.web.Utils.EXPERIMENTS_ENDPOINT;
import static com.epam.lab_experiment.web.Utils.EXPERIMENT_ID_ENDPOINT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
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

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @DisplayName("Should save a new experiment record")
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

    @DisplayName("Should update existing experiment record")
    @Sql("/test-data/single-record.sql")
    @Test
    void shouldUpdateExistingExperimentRecord() throws Exception {
        var title = "Inflammation Pathway Mapping";
        var method = "observational";
        var category = "Immunology";

        var newDate = LocalDate.of(2026, 1, 15);
        var newStatus = ExperimentStatus.PLANNED;
        var newResearcher = "Dr. J. Doe";

        Experiment experimentUpdate = TestDataUtil.experimentBuilder()
                .leadResearcher(newResearcher)
                .status(newStatus)
                .startDate(newDate)
                .build();

        mvc.perform(
                put(EXPERIMENT_ID_ENDPOINT, ID)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonUtil.toJson(experimentUpdate))
        ).andExpect(status().isOk());

        Experiment updatedExperiment = repository.findById(ID).get();

        assertThat(updatedExperiment)
                .satisfies(exp -> {
                    assertThat(exp.getId()).isEqualTo(ID);
                    assertThat(exp.getTitle()).isEqualTo(title);
                    assertThat(exp.getLeadResearcher()).isEqualTo(newResearcher);
                    assertThat(exp.getMethod()).isEqualTo(method);
                    assertThat(exp.getCategory()).isEqualTo(category);
                    assertThat(exp.getStatus()).isEqualTo(newStatus);
                    assertThat(exp.getStartDate()).isEqualTo(newDate);
                });
    }

    @DisplayName("Should remove existing experiment record")
    @Sql("/test-data/single-record.sql")
    @Test
    void shouldRemoveExperimentRecord() throws Exception {

        mvc.perform(delete(EXPERIMENT_ID_ENDPOINT, ID))
                .andExpect(status().isNoContent());

        assertThat(repository.count()).isZero();
    }

    @DisplayName("Should retrieve paged experiment records")
    @Sql("/test-data/multi-record.sql")
    @Test
    void shouldRetrievePagedExperimentRecords() throws Exception {
        int page = 0;
        int size = 2;
        mvc.perform(get(EXPERIMENTS_ENDPOINT + "?page={page}&size={size}", page, size))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.content", hasSize(size)),
                        jsonPath("$.totalElements", Matchers.equalTo(5)),
                        jsonPath("$.totalPages", Matchers.equalTo(3))
                );
    }

    @DisplayName("Should retrieve filtered experiment records")
    @Sql("/test-data/multi-record.sql")
    @Test
    void shouldRetrieveFilteredExperimentRecords() throws Exception {
        var category = "Immunology";
        var status = ExperimentStatus.PLANNED;
        mvc.perform(get(EXPERIMENTS_ENDPOINT + "?category={category}&status={status}", category, status))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.content", hasSize(1)),
                        jsonPath("$.content[0].id", Matchers.equalTo(1))
                );
    }
}
