package com.epam.lab_experiment.repository;


import com.epam.lab_experiment.model.Experiment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExperimentRepository extends CrudRepository<Experiment, Long>, PagingAndSortingRepository<Experiment, Long> {

}
