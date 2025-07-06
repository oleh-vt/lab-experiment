package com.epam.lab_experiment.service;

import com.epam.lab_experiment.PostgresTestContainer;
import com.epam.lab_experiment.model.Experiment;
import com.epam.lab_experiment.util.JsonUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureJsonTesters
@ComponentScan(basePackages = {
        "com.epam.lab_experiment.repository",
        "com.epam.lab_experiment.service",
        "com.epam.lab_experiment.util"
})
class ExperimentServiceImplTest extends PostgresTestContainer {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ExperimentService service;

    @Autowired
    private JsonUtil jsonUtil;

    @BeforeAll
    void seedDatabaseOnce() throws Exception {
        String sql = """
                INSERT INTO experiment (id, title, lead_researcher, method, category, status, start_date)
                VALUES
                  (1, 'Vaccine Study', 'Dr. Alice', 'double-blind', 'Immunology', 'PLANNED', '2025-10-15'),
                  (2, 'Cancer Biomarker Analysis', 'Dr. Bob', 'case-control', 'Oncology', 'ONGOING', '2024-11-01'),
                  (3, 'Diabetes Drug Response', 'Dr. Carol', 'randomized', 'Endocrinology', 'COMPLETED', '2021-07-20');
                """;
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    @DisplayName("Should return filtered result")
    @ParameterizedTest
    @CsvSource({
            "{ \"title\": \"analysis\" },           2",
            "{ \"leadResearcher\": \"Carol\" },     3",
            "{ \"method\": \"randomized\" },        3",
            "{ \"category\": \"Immunology\" },      1",
            "{ \"status\": \"ONGOING\" },           2",
            "{ \"startDate\": \"2025-10-01\" },     1"
    })
    void name(String updateJson, long expectedRecordId) {

        Experiment filter = jsonUtil.fromJson(updateJson);

        List<Experiment> content = service.findAll(filter, PageRequest.ofSize(10))
                .getContent();

        assertThat(content)
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("id", expectedRecordId);
    }
}