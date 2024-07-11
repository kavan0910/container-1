package com.kavankumar.ms1.controller;
import com.kavankumar.ms1.request.CalculateRequest;
import com.kavankumar.ms1.request.Request;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.opencsv.CSVReader;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class Ms1Controller {
//    public Map<String, String> storeFile(@RequestBody Request request) {

    @PostMapping("/store-file")
        public ResponseEntity<Map<String, String>> storeFile(Request request) {
        final String STORAGE_LOCATION = "/app/kavan/files/";
        String fileName = request.getFile();
        String data = request.getData();

        if(request.getFile() == null || request.getData() == null) {
            return new ResponseEntity<>(Map.of("file", null, "error", "Invalid JSON input."), HttpStatus.BAD_REQUEST);
        }


        File file = new File(STORAGE_LOCATION + fileName);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(data);
            return new ResponseEntity<>(Map.of("file", fileName, "message", "Success."), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(Map.of("file", fileName, "error", "Error while storing the file to the storage."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/calculate")
    public ResponseEntity<Map<String, Object>> calculate(CalculateRequest request) {
        final String STORAGE_LOCATION = "/app/kavan/files/";
//        final String STORAGE_LOCATION = "./";

        String fileName = request.getFile();
        String product = request.getProduct();

        if(request.getFile() == null || request.getProduct() == null) {
            return new ResponseEntity<>(Map.of("file", null, "error", "Invalid JSON input."), HttpStatus.BAD_REQUEST);
        }

        File file = new File(STORAGE_LOCATION + fileName);
        if (!file.exists()) {
            return new ResponseEntity<>(Map.of("file", fileName, "error", "File not found."), HttpStatus.NOT_FOUND);
        }
        RestTemplate restTemplate = new RestTemplate();
        Map<String,Object> response = restTemplate.postForObject("http://service-2:6000/sum", request, Map.class);

        return ResponseEntity.ok(response);
    }

    public static boolean isCSVFormat(String filePath, char delimiter) {
        Charset charset = StandardCharsets.UTF_8;
        try (CSVReader br = new CSVReader(new FileReader(filePath,charset))) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}