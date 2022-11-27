package com.javatechie.controller;

import com.javatechie.dto.APIResponse;
import com.javatechie.dto.ProductRequestDTO;
import com.javatechie.dto.ProductResponseDTO;
import com.javatechie.service.ProductService;
import com.javatechie.util.ValueMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
@Slf4j
public class ProductController {


    public static final String SUCCESS = "Success";
    private ProductService productService;



    @PostMapping
    public ResponseEntity<APIResponse> createNewProduct(@RequestBody @Valid ProductRequestDTO productRequestDTO) {

        log.info("ProductController::createNewProduct request body {}", ValueMapper.jsonAsString(productRequestDTO));

        ProductResponseDTO productResponseDTO = productService.createNewProduct(productRequestDTO);
        //Builder Design pattern

        APIResponse<ProductResponseDTO> responseDTO = APIResponse
                .<ProductResponseDTO>builder()
                .status(SUCCESS)
                .results(productResponseDTO)
                .build();

        log.info("ProductController::createNewProduct response {}", ValueMapper.jsonAsString(responseDTO));

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<APIResponse> getProducts() {

        List<ProductResponseDTO> products = productService.getProducts();
        //Builder Design pattern (to avoid complex object creation headache)
        APIResponse<List<ProductResponseDTO>> responseDTO = APIResponse
                .<List<ProductResponseDTO>>builder()
                .status(SUCCESS)
                .results(products)
                .build();

        log.info("ProductController::getProducts response {}", ValueMapper.jsonAsString(responseDTO));

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable long productId) {

        log.info("ProductController::getProduct by id  {}", productId);

        ProductResponseDTO productResponseDTO = productService.getProductById(productId);
        APIResponse<ProductResponseDTO> responseDTO = APIResponse
                .<ProductResponseDTO>builder()
                .status(SUCCESS)
                .results(productResponseDTO)
                .build();

        log.info("ProductController::getProduct by id  {} response {}", productId,ValueMapper
                .jsonAsString(productResponseDTO));

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/types")
    public ResponseEntity<APIResponse> getProductsGroupByType() {

        Map<String, List<ProductResponseDTO>> products = productService.getProductsByTypes();
        APIResponse<Map<String, List<ProductResponseDTO>>> responseDTO = APIResponse
                .<Map<String, List<ProductResponseDTO>>>builder()
                .status(SUCCESS)
                .results(products)
                .build();

        log.info("ProductController::getProductsGroupByType by types  {}", ValueMapper.jsonAsString(responseDTO));

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }


}
