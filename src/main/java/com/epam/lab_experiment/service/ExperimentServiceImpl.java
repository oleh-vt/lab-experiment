package com.epam.lab_experiment.service;

import com.epam.lab_experiment.exception.ExperimentNotFoundException;
import com.epam.lab_experiment.model.Experiment;
import com.epam.lab_experiment.repository.ExperimentRepository;
import com.epam.lab_experiment.repository.ExperimentSpecification;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class ExperimentServiceImpl implements ExperimentService {

    private final ExperimentRepository repository;
    private final Validator validator;

    public ExperimentServiceImpl(ExperimentRepository repository, Validator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @Override
    public Experiment save(Experiment e) {
        return repository.save(e);
    }

    @Override
    public Experiment update(long id, Experiment update) {
        Experiment existing = findOrThrow(id);
        merge(update, existing);
        validate(existing);
        return save(existing);
    }

    @Override
    public Page<Experiment> findAll(Experiment e, Pageable pageable) {
        return repository.findAll(ExperimentSpecification.build(e), pageable);
    }

    @Override
    public void delete(long id) {
        repository.delete(findOrThrow(id));
    }

    private Experiment findOrThrow(long id) {
        return repository.findById(id).orElseThrow(() -> new ExperimentNotFoundException(id));
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

    private void validate(Experiment e) {
        Set<ConstraintViolation<Experiment>> violations = validator.validate(e);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

}
