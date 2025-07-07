package com.epam.lab_experiment.util;

import com.epam.lab_experiment.model.Experiment;
import com.epam.lab_experiment.model.ExperimentStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestDataUtil {

    public static final long ID = 1L;

    public static final String EXPERIMENTS_ENDPOINT = "/experiments";
    public static final String EXPERIMENT_ID_ENDPOINT = "/experiments/{id}";

    public static final Experiment EXPERIMENT_1 = Experiment.builder()
            .id(1L)
            .title("COVID-19 Vaccine Efficacy Study")
            .leadResearcher("Dr. Lena Hofmann")
            .method("Randomized Control Trial")
            .status(ExperimentStatus.COMPLETED)
            .category("Immunology")
            .startDate(LocalDate.of(2024, 11, 20))
            .build();
    public static final Experiment EXPERIMENT_2 = Experiment.builder()
            .id(2L)
            .title("Protein Folding Simulation")
            .leadResearcher("Dr. Alice Morgan")
            .method("Molecular Dynamics")
            .status(ExperimentStatus.PLANNED)
            .category("Biophysics")
            .startDate(LocalDate.of(2025, 9, 1))
            .build();
    public static final Experiment UNSAVED_EXPERIMENT = TestDataUtil.toBuilder(EXPERIMENT_1)
            .id(null)
            .startDate(null)
            .build();

    public static Experiment.ExperimentBuilder toBuilder(Experiment e) {
        return Experiment.builder()
                .id(e.getId())
                .title(e.getTitle())
                .leadResearcher(e.getLeadResearcher())
                .category(e.getCategory())
                .method(e.getMethod())
                .status(e.getStatus())
                .startDate(e.getStartDate());
    }
}
