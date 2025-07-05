package com.epam.lab_experiment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class LabExperimentApplicationTests {

	@Test
	void contextLoads() {
	}

}
