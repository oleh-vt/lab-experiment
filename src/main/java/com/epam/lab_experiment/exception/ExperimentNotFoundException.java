package com.epam.lab_experiment.exception;

public class ExperimentNotFoundException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE = "Experiment id is not found. ID = %d";

    public ExperimentNotFoundException(long id) {
        super(MESSAGE_TEMPLATE.formatted(id));
    }
}
