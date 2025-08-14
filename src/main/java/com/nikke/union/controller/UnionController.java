package com.nikke.union.controller;

import com.nikke.union.service.UnionCsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UnionController {

    private final UnionCsvService unionCsvService;

    @Autowired
    public UnionController(UnionCsvService unionCsvService) {
        this.unionCsvService = unionCsvService;
    }

    @GetMapping(value = "/api/union/data.csv", produces = "text/plain; charset=UTF-8")
    public String getUnionData() {
        return unionCsvService.getUnionCsvString();
    }
}
