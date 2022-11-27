package com.javatechie;

import com.javatechie.controller.ProductController;
import com.javatechie.dto.ProductResponseDTO;
import com.javatechie.entity.Product;
import com.javatechie.repository.ProductRepository;
import com.javatechie.service.ProductService;
import com.javatechie.util.ValueMapper;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
class ProductServiceApplicationTests {

    private static final String ENDPOINT_URL = "/products";

    @InjectMocks
    private ProductController productController;


    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.productController).build();
    }

    @Test
    public void createNewProductTest() throws Exception {
        Product demoProduct = new Product(1, "demo", "desc", "type",1,1000,"SUP","SUP01");
        when(productRepository.save(any())).thenReturn(demoProduct);
        mockMvc.perform(MockMvcRequestBuilders
                        .post(ENDPOINT_URL)
                        .content(Objects.requireNonNull(ValueMapper.jsonAsString(demoProduct)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.results.id").exists());
    }

    @Test
    public void shouldReturnAllProductsFromDB() throws Exception {
        when(productRepository.findAll()).thenReturn(Arrays.asList(
                new Product(1, "demo1", "desc1", "type1",1,1000,"SUP1","SUP01"),
                new Product(2, "demo2", "desc2", "type2",2,2000,"SUP2","SUP02")
        ));
        mockMvc.perform(MockMvcRequestBuilders
                        .get(ENDPOINT_URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.*").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[0].id").value(1));
    }


}
