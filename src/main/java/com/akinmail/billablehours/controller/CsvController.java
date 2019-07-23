package com.akinmail.billablehours.controller;

import com.akinmail.billablehours.service.CsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/csv")
public class CsvController {
    CsvService csvService;

    @Autowired
    public CsvController(CsvService csvService) {
        this.csvService = csvService;
    }

    @PostMapping(value = "/upload", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        return new ResponseEntity<>(csvService.processInputFile((FileInputStream) file.getInputStream()), HttpStatus.OK);
    }
}
