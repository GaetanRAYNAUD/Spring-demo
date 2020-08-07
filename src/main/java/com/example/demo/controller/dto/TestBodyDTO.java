package com.example.demo.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "TestBody")
public class TestBodyDTO {

    private String testString;

    public String getTestString() {
        return testString;
    }

    public void setTestString(String testString) {
        this.testString = testString;
    }

    @Override
    public String toString() {
        return "TestBodyDTO{" +
               "testString='" + testString + '\'' +
               '}';
    }
}
