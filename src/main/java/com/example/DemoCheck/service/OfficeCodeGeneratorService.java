package com.example.DemoCheck.service;

import com.example.DemoCheck.repository.OfficeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class OfficeCodeGeneratorService {

    private final OfficeRepository officeRepository;

    public String generateOfficeCode() {

        long timePart = System.currentTimeMillis() % 100000; // 5 digits
        int randomPart = ThreadLocalRandom.current().nextInt(100, 999); // 3 digits

        return "OF" + timePart + randomPart; // total = 2 + 5 + 3 = 10 ✔
    }
}
