package com.epam.lab_experiment.web;

import com.epam.lab_experiment.repository.ExperimentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.Collections;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExperimentController.class)
class ExperimentControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockitoBean
	private ExperimentRepository repository;

	@Test
	void contextLoads() throws Exception {
		doReturn(Collections.emptyList()).when(repository).findAll();

		mvc.perform(get("/experiments"))
				.andExpect(status().isOk())
				.andExpect(content().json("[]"));
	}

}
