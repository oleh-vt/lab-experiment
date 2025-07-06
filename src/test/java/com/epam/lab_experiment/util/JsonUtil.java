package com.epam.lab_experiment.util;

import com.epam.lab_experiment.model.Experiment;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JsonUtil {

    private final JacksonTester<Experiment> jacksonTester;

    public JsonUtil(JacksonTester<Experiment> jacksonTester) {
        this.jacksonTester = jacksonTester;
    }

    public String toJson(Experiment exp) {
        try {
            return jacksonTester.write(exp).getJson();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Experiment fromJson(String json) {
        try {
            return jacksonTester.parseObject(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
