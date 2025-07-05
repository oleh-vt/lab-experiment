package com.epam.lab_experiment;

import org.springframework.boot.SpringApplication;

public class TestLabExperimentApplication {

	public static void main(String[] args) {
		SpringApplication.from(LabExperimentApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
