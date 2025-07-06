package com.epam.lab_experiment.service;

import com.epam.lab_experiment.model.Experiment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExperimentService {
    Experiment save(Experiment e);
    Experiment update(long id, Experiment e);
    Page<Experiment> findAll(Experiment e, Pageable pageable);
    void delete(long id);
}
