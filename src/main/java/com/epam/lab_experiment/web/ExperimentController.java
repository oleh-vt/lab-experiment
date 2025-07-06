package com.epam.lab_experiment.web;


import com.epam.lab_experiment.exception.ExperimentNotFoundException;
import com.epam.lab_experiment.model.Experiment;
import com.epam.lab_experiment.service.ExperimentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("experiments")
public class ExperimentController {

    private final ExperimentService experimentService;

    public ExperimentController(ExperimentService experimentService) {
        this.experimentService = experimentService;
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
        return experimentService.save(experiment);
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
        return experimentService.findAll(experiment, pageable);
    }

    @Operation(summary = "Update an existing experiment by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Experiment updated successfully"),
            @ApiResponse(responseCode = "404", description = "Experiment not found")
    })
    @PutMapping("{id}")
    Experiment update(
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
        return experimentService.update(id, experiment);
    }

    @Operation(summary = "Delete an experiment by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Experiment deleted"),
            @ApiResponse(responseCode = "404", description = "Experiment not found")
    })
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@Parameter(description = "ID of the experiment to delete") @PathVariable long id) {
        experimentService.delete(id);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getAllErrors().stream()
                .map(FieldError.class::cast)
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, String> handleValidationExceptions(ConstraintViolationException ex) {

        return ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        v -> {
                            Path path = v.getPropertyPath();
                            String name = null;
                            for (Path.Node node : path) {
                                name = node.getName();
                            }
                            return name;
                        },
                        ConstraintViolation::getMessage
                ));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ExperimentNotFoundException.class)
    public Map<String, String> handleNotFoundExceptions(ExperimentNotFoundException ex) {
        return Map.of("message", ex.getMessage());
    }
}
