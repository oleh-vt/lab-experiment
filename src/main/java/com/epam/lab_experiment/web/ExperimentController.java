package com.epam.lab_experiment.web;


import com.epam.lab_experiment.model.Experiment;
import com.epam.lab_experiment.repository.ExperimentRepository;
import com.epam.lab_experiment.repository.ExperimentSpecification;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
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

    @GetMapping()
    Page<Experiment> getExperiments(
            @ModelAttribute Experiment experiment,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Experiment> all = repository.findAll(ExperimentSpecification.build(experiment), pageable);
        return all;
    }

    @PutMapping("{id}")
    ResponseEntity<Experiment> update(@PathVariable long id, @RequestBody Experiment experiment) {
        return repository.findById(id)
                .map(existing -> {
                    merge(experiment, existing);
                    return existing;
                })
                .map(repository::save)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getAllErrors().stream()
                .map(FieldError.class::cast)
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
    }

    private void merge(Experiment update, Experiment existing) {
        Optional.ofNullable(update.getTitle())
                .ifPresent(existing::setTitle);
        Optional.ofNullable(update.getLeadResearcher())
                .ifPresent(existing::setLeadResearcher);
        Optional.ofNullable(update.getMethod())
                .ifPresent(existing::setMethod);
        Optional.ofNullable(update.getCategory())
                .ifPresent(existing::setCategory);
        Optional.ofNullable(update.getStatus())
                .ifPresent(existing::setStatus);
        existing.setStartDate(update.getStartDate());
    }
}
