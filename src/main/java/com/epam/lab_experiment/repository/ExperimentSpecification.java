package com.epam.lab_experiment.repository;

import com.epam.lab_experiment.model.Experiment;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public final class ExperimentSpecification {

    public static final String TITLE = "title";
    public static final String CATEGORY = "category";
    public static final String METHOD = "method";
    public static final String LEAD_RESEARCHER = "leadResearcher";
    public static final String STATUS = "status";
    public static final String START_DATE = "startDate";

    private ExperimentSpecification() {
    }

    public static Specification<Experiment> build(Experiment e) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (e.getTitle() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get(TITLE)), likePattern(e.getTitle())));
            }

            if (e.getCategory() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(CATEGORY), e.getCategory()));
            }

            if (e.getMethod() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(METHOD), e.getMethod()));
            }

            if (e.getLeadResearcher() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get(LEAD_RESEARCHER)), likePattern(e.getLeadResearcher())));
            }

            if (e.getStatus() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(STATUS), e.getStatus()));
            }

            if (e.getStartDate() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get(START_DATE), e.getStartDate()));
            }

            return predicate;
        };
    }

    private static String likePattern(String str) {
        return "%" + str.toLowerCase() + "%";
    }
}
