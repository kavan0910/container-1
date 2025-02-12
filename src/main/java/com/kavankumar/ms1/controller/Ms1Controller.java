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
    public Map<String, String> storeFile(@RequestBody Request request) {
        final String STORAGE_LOCATION = "./kavan/files/";
        if (request.getFile() == null) {
            HashMap<String, String> map = new HashMap<>();
            map.put("file", request.getFile());
            map.put("error", "Invalid JSON input.");
            return map;
        }
        try {
            Path filePath = Paths.get(STORAGE_LOCATION, request.getFile());
            System.out.println("Storing file to the storage." + filePath);
            Files.write(filePath, request.getData().getBytes());
            HashMap<String, String> map = new HashMap<>();
            map.put("file", request.getFile());
            map.put("message", "Success.");
            return map;
        } catch (Exception e) {
            System.out.println(e);
            HashMap<String, String> map = new HashMap<>();
            map.put("file", request.getFile());
            map.put("error", "Error while storing the file to the storage.");
            return map;
        }
    }

    @PostMapping("/calculate")
    public Map<String, Object> calculate(@RequestBody CalculateRequest request) {
        final String STORAGE_LOCATION = "./kavan/files/";

        try {
            if(request.getFile() == null) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("file", request.getFile());
                map.put("error", "Invalid JSON input.");
                return map;
            }

            BufferedReader csv_file = new BufferedReader(new FileReader(STORAGE_LOCATION + request.getFile()));
            if (!isCSVFormat(STORAGE_LOCATION + request.getFile(), ',')) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("file", request.getFile());
                map.put("error", "Invalid CSV format.");
                return map;
            }
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.postForObject("http://service-2:6000/sum", request, Map.class);

            return response;
        } catch (Exception e) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("file", request.getFile());
            map.put("error", "File not found.");
            return map;

        }

//        BufferedReader csv_file = new BufferedReader(new FileReader(STORAGE_LOCATION + request.getFile()));
//        if (!isCSVFormat(STORAGE_LOCATION + request.getFile(), ',')) {
//            HashMap<String, Object> map = new HashMap<>();
//            map.put("file", request.getFile());
//            map.put("error", "Input file not in CSV format.");
//            return map;
//        }
//        RestTemplate restTemplate = new RestTemplate();
//        Map<String, Object> response = restTemplate.postForObject("http://service-2:6000/sum", request, Map.class);
//
//        return response;
    }

    public static boolean isCSVFormat(String filePath, char delimiter) {
        Charset charset = StandardCharsets.UTF_8;
        try (CSVReader br = new CSVReader(new FileReader(filePath, charset))) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}