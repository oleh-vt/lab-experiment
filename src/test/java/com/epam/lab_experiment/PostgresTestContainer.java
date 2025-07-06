package com.epam.lab_experiment;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@ActiveProfiles("test")
@Testcontainers
public abstract class PostgresTestContainer {
    public static final String POSTGRES_IMAGE_NAME = "postgres:17.5";

    private static final String DB_NAME = "testdb";
    private static final String DB_USER = "test-user";
    private static final String PASSWORD = "changeit";

    @Container
    @ServiceConnection
    protected static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER =
            new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_IMAGE_NAME))
                    .withDatabaseName(DB_NAME)
                    .withUsername(DB_USER)
                    .withPassword(PASSWORD);
}
