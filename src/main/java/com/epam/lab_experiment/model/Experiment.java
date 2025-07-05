package com.epam.lab_experiment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
@Entity
public class Experiment {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @NotBlank(message = "Title is not provided")
        private String title;
        @NotBlank(message = "Lead Researcher is not provided")
        private String leadResearcher;
        @NotBlank(message = "Method is not provided")
        private String method;
        @NotNull(message = "Experiment status is not provided")
        @Enumerated(EnumType.STRING)
        private ExperimentStatus status;
        @NotBlank(message = "Category is not provided")
        private String category;
        private LocalDate startDate;

        public Experiment(
                Long id,
                String title,
                String leadResearcher,
                String method,
                ExperimentStatus status,
                String category,
                LocalDate startDate
        ) {
                this.id = id;
                this.title = title;
                this.leadResearcher = leadResearcher;
                this.method = method;
                this.status = status;
                this.category = category;
                this.startDate = startDate;
        }

        public Experiment() {
        }

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public String getTitle() {
                return title;
        }

        public void setTitle(String title) {
                this.title = title;
        }

        public String getLeadResearcher() {
                return leadResearcher;
        }

        public void setLeadResearcher(String leadResearcher) {
                this.leadResearcher = leadResearcher;
        }

        public String getMethod() {
                return method;
        }

        public void setMethod(String method) {
                this.method = method;
        }

        public ExperimentStatus getStatus() {
                return status;
        }

        public void setStatus(ExperimentStatus status) {
                this.status = status;
        }

        public String getCategory() {
                return category;
        }

        public void setCategory(String category) {
                this.category = category;
        }

        public LocalDate getStartDate() {
                return startDate;
        }

        public void setStartDate(LocalDate startDate) {
                this.startDate = startDate;
        }
}
