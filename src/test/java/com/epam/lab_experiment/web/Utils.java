package com.epam.lab_experiment.web;

import com.epam.lab_experiment.model.Experiment;
import com.epam.lab_experiment.model.ExperimentStatus;

import java.time.LocalDate;

public class Utils {

    public static ExperimentBuilder experimentBuilder() {
        return new ExperimentBuilder();
    }

    public static ExperimentBuilder experimentBuilder(Experiment e) {
        return new ExperimentBuilder()
                .id(e.getId())
                .title(e.getTitle())
                .leadResearcher(e.getLeadResearcher())
                .method(e.getMethod())
                .status(e.getStatus())
                .category(e.getCategory())
                .startDate(e.getStartDate());
    }

    public static class ExperimentBuilder {
        private Long id;
        private String title;
        private String leadResearcher;
        private String method;
        private ExperimentStatus status;
        private String category;
        private LocalDate startDate;

        private ExperimentBuilder() {

        }

        public ExperimentBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ExperimentBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ExperimentBuilder leadResearcher(String leadResearcher) {
            this.leadResearcher = leadResearcher;
            return this;
        }

        public ExperimentBuilder method(String method) {
            this.method = method;
            return this;
        }

        public ExperimentBuilder status(ExperimentStatus status) {
            this.status = status;
            return this;
        }

        public ExperimentBuilder category(String category) {
            this.category = category;
            return this;
        }

        public ExperimentBuilder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public Experiment build() {
            return new Experiment(id, title, leadResearcher, method, status, category, startDate);
        }
    }

}
