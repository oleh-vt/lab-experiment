package com.epam.lab_experiment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class Experiment {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @NotBlank(message = "Title is not provided")
        private String title;
        @NotBlank(message = "Lead Researcher is not provided")
        private String leadResearcher;
        @NotBlank(message = "Method is not provided")
        private String method;
        @NotNull(message = "Experiment status is not provided")
        @Enumerated(EnumType.STRING)
        private ExperimentStatus status;
        @NotBlank(message = "Category is not provided")
        private String category;
        private LocalDate startDate;
}
