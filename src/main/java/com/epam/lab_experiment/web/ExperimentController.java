package com.epam.lab_experiment.web;


import com.epam.lab_experiment.repository.ExperimentRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("experiments")
public class ExperimentController {

    private final ExperimentRepository repository;

    public ExperimentController(ExperimentRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    List<Object> getExperiments() {
        return repository.findAll();
    }

}
