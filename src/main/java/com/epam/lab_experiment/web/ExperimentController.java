package com.epam.lab_experiment.web;


import com.epam.lab_experiment.model.Experiment;
import com.epam.lab_experiment.repository.ExperimentRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("experiments")
public class ExperimentController {

    private final ExperimentRepository repository;

    public ExperimentController(ExperimentRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Experiment create(@Valid @RequestBody Experiment experiment) {
        return repository.save(experiment);
    }

    @GetMapping
    Iterable<Experiment> getExperiments() {
        return repository.findAll();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getAllErrors().stream()
                .map(FieldError.class::cast)
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
    }
}
