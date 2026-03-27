package com.example.DemoCheck.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import org.springframework.transaction.annotation.Transactional;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductApiTest {
    @Autowired
    private MockMvc mockMvc;

    // ================================================ GET Products API ================================================

    // valid request test
    @Test
    void testGetProducts_byPage_returnsPage() throws Exception {
        mockMvc.perform(get("/products")
                .param("page", "0")
                .param("size", "20")
                .param("sort", "productName,asc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page.size").value(20))
            .andExpect(jsonPath("$.page.number").value(0))
            .andExpect(jsonPath("$._embedded.products").exists());
    }

    // ------------------ pagination tests starts here ------------------

    // testing first page fetch
    @Test
    void testFirstPage() throws Exception {
        mockMvc.perform(get("/products?page=0&size=20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page.number").value(0));
    }

    // testing middle page fetch
    @Test
    void testMiddlePage() throws Exception {
        mockMvc.perform(get("/products?page=3&size=20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page.number").value(3));
    }

    // testing last page fetch
    @Test
    void testLastPage() throws Exception {
        mockMvc.perform(get("/products?page=5&size=20"))
            .andExpect(status().isOk());
    }

    // testing page out of bounds
    // should return empty products list
    @Test
    void testOutOfBoundsPage() throws Exception {
        mockMvc.perform(get("/products?page=555&size=20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.products").isEmpty());
    }

    // negative page number 
    @Test
    void testNegativePage() throws Exception {
        mockMvc.perform(get("/products?page=-1&size=20"))
            // .andExpect(status().isBadRequest()); 
            /*
            when we give negative page number as param, then spring internally converts 
            that negative page number to 0
            -1, -3, -78 -> 0

            Hence, when we use isBadRequest(), test fails, because spring is returning 
            status = 200 here

            hence we should use isOk() and validate page number as 0
            */


            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page.number").value(0));
    }

    // size = 0 edge case
    @Test
    void testSizeZero() throws Exception {
        mockMvc.perform(get("/products?page=0&size=0"))
            .andExpect(status().isOk());
    }

    // ------------------ sorting tests starts here ------------------

    // sorting on a field name
    @Test
    void testAscendingProductSorting() throws Exception {
        String response = mockMvc.perform(get("/products?sort=productName,asc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.products").isArray())
            .andReturn()
            .getResponse()
            .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);

        JsonNode products = root.path("_embedded").path("products");

        if (products.size() < 2) return;

        String first = products.get(0).path("productName").asString();
        String second = products.get(1).path("productName").asString();    

        assertTrue(first.compareToIgnoreCase(second) <= 0);
    }

    // reverse sorting on a field name
    @Test
    void testDescendingProductSorting() throws Exception {
        String response = mockMvc.perform(get("/products?sort=productName,desc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.products").isArray())
            .andReturn()
            .getResponse()
            .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);

        JsonNode products = root.path("_embedded").path("products");

        if (products.size() < 2) return;

        String first = products.get(0).path("productName").asString();
        String second = products.get(1).path("productName").asString();    

        assertTrue(first.compareToIgnoreCase(second) >= 0);
    }

    // invalid sort field provided
    @Test
    void testInvalidSortField() throws Exception {
        mockMvc.perform(get("/products?sort=invalidField,asc"))
            // .andExpect(status().isBadRequest());

            /*
            ideally sending invalid field should throw bad request 400, but spring internally handles it
            it ignores the incorrect value and fallback to unsorted result
            */
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.products").exists());
    }

    // ================================================ GET Products Search API ================================================

    // search with a valid keyboard
    @Test
    void testSearch_validKeyword_result() throws Exception {
        mockMvc.perform(get("/products/search/searchByNameOrLine")
                .param("name", "car")
                .param("line", "car")
                .param("page", "0")
                .param("size", "20")
                .param("projection", "productView"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.products").isNotEmpty());
    }

    // search with a no match keyword
    @Test
    void testSearch_noMatch() throws Exception {
        mockMvc.perform(get("/products/search/searchByNameOrLine")
                .param("line", "metro")
                .param("page", "0")
                .param("size", "20")
                .param("projection", "productView"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.products").isEmpty());
    }

    // search with a partial match
    @Test
    void testSearch_PartialMatch() throws Exception {
        mockMvc.perform(get("/products/search/searchByNameOrLine")
                .param("name", "ca")
                .param("line", "ca"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.products").isNotEmpty());
    }


    // ================================================ GET Products By Id API ================================================

    // valid ID
    @Test
    void testGetProductByValidId() throws Exception {
        mockMvc.perform(get("/products/S10_1678"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productName").exists());
    }

    // invalid id
    @Test
    void testGetProductByInvalidId() throws Exception {
        mockMvc.perform(get("/products/S10_1678025"))
            .andExpect(status().isNotFound());
    }

    // null or empty ID
    @Test
    void testGetProductById_Empty() throws Exception {
        mockMvc.perform(get("/products/"))
            .andExpect(status().isNotFound());
    }


    // ================================================ Projection Tests ================================================

    // checks valid projection
    @Test
    void testProjection_ProductView() throws Exception {
        mockMvc.perform(get("/products")
                .param("projection", "productView"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.products[0].productName").exists())
            .andExpect(jsonPath("$._embedded.products[0].buyPrice").exists());
    }

    // checking non-member field of projection in response
    @Test
    void testProjection_NoHiddenFieldResponse() throws Exception {
        mockMvc.perform(get("/products")
                .param("projection", "productView"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.products[0].productCode").doesNotExist());
    }




    // ================================================ POST Product tests ================================================
    @Test
    void testCraeteProduct_Valid() throws Exception {
        String json = """
            {
                "productCode": "S10_9999",
                "productName": "Test Product",
                "productLine": "Cars",
                "productVendor": "Test Vendor",
                "quantityInStock": 100,
                "buyPrice": 200.0,
                "MSRP": 300.0
            }
            """;

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"));
    }
}
