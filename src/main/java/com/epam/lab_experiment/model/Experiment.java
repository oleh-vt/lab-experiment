package com.epam.lab_experiment.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record Experiment(
        Long id,
        @NotBlank(message = "Title is not provided")
        String title,
        @NotBlank(message = "Lead Researcher is not provided")
        String leadResearcher,
        @NotBlank(message = "Method is not provided")
        String method,
        @NotNull(message = "Experiment status is not provided")
        ExperimentStatus status,
        @NotBlank(message = "Category is not provided")
        String category,
        LocalDate startDate
) {
}
