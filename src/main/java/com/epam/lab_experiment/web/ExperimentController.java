package com.epam.lab_experiment.web;


import com.epam.lab_experiment.model.Experiment;
import com.epam.lab_experiment.repository.ExperimentRepository;
import com.epam.lab_experiment.repository.ExperimentSpecification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Create a new experiment")
    @ApiResponse(
            responseCode = "201",
            description = "Experiment created successfully",
            content = @Content(schema = @Schema(implementation = Experiment.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Input validation error",
            content = @Content(examples = @ExampleObject("""
                    {
                        "title": "Title is not provided,
                        "leadResearcher: "Lead Researcher is not provided",
                        "method": "Method is not provided",
                        "status": "Experiment status is not provided"
                    }
                    """))
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Experiment create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Experiment data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Experiment.class))
            )
            @Valid @RequestBody Experiment experiment) {
        return repository.save(experiment);
    }

    @Operation(summary = "Get paginated list of experiments with optional filtering")
    @Parameter(name = "title", description = "Title text search filter")
    @Parameter(name = "leadResearcher", description = "Title text Lead Researcher")
    @Parameter(name = "method")
    @Parameter(name = "status")
    @Parameter(name = "category")
    @Parameter(name = "startDate", description = "Start date equal to or greater than")
    @ApiResponse(
            responseCode = "200",
            description = "List of experiments",
            content = @Content(schema = @Schema(implementation = Page.class))
    )
    @GetMapping
    Page<Experiment> getExperiments(
            @Parameter(description = "Filtering criteria") @ModelAttribute Experiment experiment,
            @Parameter(hidden = true) @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return repository.findAll(ExperimentSpecification.build(experiment), pageable);
    }

    @Operation(summary = "Update an existing experiment by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Experiment updated successfully"),
            @ApiResponse(responseCode = "404", description = "Experiment not found")
    })
    @PutMapping("{id}")
    ResponseEntity<Experiment> update(
            @Parameter(description = "ID of the experiment to update")
            @PathVariable
            long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated experiment data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Experiment.class))
            )
            @RequestBody
            Experiment experiment
    ) {
        return repository.findById(id)
                .map(existing -> {
                    merge(experiment, existing);
                    return existing;
                })
                .map(repository::save)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete an experiment by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Experiment deleted"),
            @ApiResponse(responseCode = "404", description = "Experiment not found")
    })
    @DeleteMapping("{id}")
    ResponseEntity<?> delete(@Parameter(description = "ID of the experiment to delete") @PathVariable long id) {
        return repository.findById(id)
                .map(exp -> {
                    repository.delete(exp);
                    return ResponseEntity.noContent().build();
                }).orElseGet(() -> ResponseEntity.notFound().build());
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
