package com.epam.lab_experiment.repository;

import com.epam.lab_experiment.model.Experiment;

import java.util.List;

public interface ExperimentRepository {

    List<Experiment> findAll();

    Experiment save(Experiment e);

}
