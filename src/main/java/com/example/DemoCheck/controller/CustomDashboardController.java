package com.example.DemoCheck.controller;

import com.example.DemoCheck.entity.ProductLine;
import com.example.DemoCheck.repository.ProductLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/custom")
@CrossOrigin(origins = "*")
public class CustomDashboardController {

    @Autowired
    private ProductLineRepository repository;

    // 1. Search By Image Upload
    @PostMapping("/search-image")
    public ResponseEntity<?> searchByImage(@RequestParam("file") MultipartFile file) {
        try {
            byte[] fileBytes = file.getBytes();
            Optional<ProductLine> match = repository.findByImage(fileBytes);

            if (match.isPresent()) {
                return ResponseEntity.ok(match.get());
            }
            return ResponseEntity.status(404).body(Collections.singletonMap("message", "No exact image match found in the database."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("message", "Error processing image."));
        }
    }

    // 2. Analytics Aggregation
    @GetMapping("/analytics")
    public ResponseEntity<List<Map<String, Object>>> getAnalytics() {
        List<Object[]> results = repository.getProductCounts();
        List<Map<String, Object>> response = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("productLine", row[0]);
            map.put("count", row[1]);
            response.add(map);
        }

        return ResponseEntity.ok(response);
    }
}