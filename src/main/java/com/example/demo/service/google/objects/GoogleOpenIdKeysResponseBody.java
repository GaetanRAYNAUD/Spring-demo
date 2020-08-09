package com.example.demo.service.google.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleOpenIdKeysResponseBody {

    @JsonProperty("keys")
    private List<Map<String, Object>> keys;

    public List<Map<String, Object>> getKeys() {
        return keys;
    }

    public void setKeys(List<Map<String, Object>> keys) {
        this.keys = keys;
    }
}
