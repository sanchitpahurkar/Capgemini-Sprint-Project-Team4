package com.example.DemoCheck.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OfficeRestApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllOffices_shouldReturnData() throws Exception {

        mockMvc.perform(get("/offices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.offices.length()").isNotEmpty())

                .andExpect(jsonPath("$._embedded.offices[0].city").exists())
                .andExpect(jsonPath("$._embedded.offices[0].phone").exists())
                .andExpect(jsonPath("$._embedded.offices[0].country").exists());;
    }
}