package com.javatechie.service;

import com.javatechie.dto.ProductRequestDTO;
import com.javatechie.dto.ProductResponseDTO;
import com.javatechie.entity.Product;
import com.javatechie.exception.ProductNotFoundException;
import com.javatechie.exception.ProductServiceBusinessException;
import com.javatechie.repository.ProductRepository;
import com.javatechie.util.ValueMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ProductService {

    private ProductRepository productRepository;


    public ProductResponseDTO createNewProduct(ProductRequestDTO productRequestDTO) throws ProductServiceBusinessException {
        ProductResponseDTO productResponseDTO;

        try {
            log.info("ProductService:createNewProduct execution started.");
            Product product = ValueMapper.convertToEntity(productRequestDTO);
            log.debug("ProductService:createNewProduct request parameters {}", ValueMapper.jsonAsString(productRequestDTO));

            Product productResults = productRepository.save(product);
            productResponseDTO = ValueMapper.convertToDTO(productResults);
            log.debug("ProductService:createNewProduct received response from Database {}", ValueMapper.jsonAsString(productRequestDTO));

        } catch (Exception ex) {
            log.error("Exception occurred while persisting product to database , Exception message {}", ex.getMessage());
            throw new ProductServiceBusinessException("Exception occurred while create a new product");
        }
        log.info("ProductService:createNewProduct execution ended.");
        return productResponseDTO;
    }

    @Cacheable(value = "product")
    public List<ProductResponseDTO> getProducts() throws ProductServiceBusinessException {
        List<ProductResponseDTO> productResponseDTOS = null;

        try {
            log.info("ProductService:getProducts execution started.");

            List<Product> productList = productRepository.findAll();

            if (!productList.isEmpty()) {
                productResponseDTOS = productList.stream()
                        .map(ValueMapper::convertToDTO)
                        .collect(Collectors.toList());
            } else {
                productResponseDTOS = Collections.emptyList();
            }

            log.debug("ProductService:getProducts retrieving products from database  {}", ValueMapper.jsonAsString(productResponseDTOS));

        } catch (Exception ex) {
            log.error("Exception occurred while retrieving products from database , Exception message {}", ex.getMessage());
            throw new ProductServiceBusinessException("Exception occurred while fetch all products from Database");
        }

        log.info("ProductService:getProducts execution ended.");
        return productResponseDTOS;
    }

    /**
     * this method will fetch product from DB by ID
     *
     * @param productId
     * @return product response from DB
     */
    @Cacheable(value = "product")
    public ProductResponseDTO getProductById(long productId) {
        ProductResponseDTO productResponseDTO;

        //String supplierCode="";
        try {
            log.info("ProductService:getProductById execution started.");

//            Product p=productRepository.findBySupplierCode(supplierCode);
//
//            Optional<Product> p1 = Optional.ofNullable(p);
//            if(p1.isPresent()){
//                //do operation
//            }else{
//                throw new Exception();
//            }


            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException("Product not found with id " + productId));
            productResponseDTO = ValueMapper.convertToDTO(product);

            log.debug("ProductService:getProductById retrieving product from database for id {} {}", productId, ValueMapper.jsonAsString(productResponseDTO));

        } catch (Exception ex) {
            log.error("Exception occurred while retrieving product {} from database , Exception message {}", productId, ex.getMessage());
            throw new ProductServiceBusinessException("Exception occurred while fetch product from Database " + productId);
        }

        log.info("ProductService:getProductById execution ended.");
        return productResponseDTO;
    }

    @Cacheable(value = "product")
    public Map<String, List<ProductResponseDTO>> getProductsByTypes() {
        try {
            log.info("ProductService:getProductsByTypes execution started.");

            Map<String, List<ProductResponseDTO>> productsMap =
                    productRepository.findAll().stream()
                            .map(ValueMapper::convertToDTO)
                            .filter(productResponseDTO -> productResponseDTO.getProductType() != null)
                            .collect(Collectors.groupingBy(ProductResponseDTO::getProductType));

            log.info("ProductService:getProductsByTypes execution ended.");
            return productsMap;

        } catch (Exception ex) {
            log.error("Exception occurred while retrieving product grouping by type from database , Exception message {}", ex.getMessage());
            throw new ProductServiceBusinessException("Exception occurred while fetch product from Database ");
        }
    }

    public Map<String, List<ProductResponseDTO>> getProductsByTypesBeforeJava8() {

        Map<String, List<ProductResponseDTO>> productsMap = new HashMap<>();
        List<String> productTypes = Arrays.asList("Electronics", "fashion", "Kitchen");//1 st iteration from DB

        List<Product> productList = productRepository.findAll(); //2 nd

        for (String type : productTypes) {
            List<ProductResponseDTO> productResponseDTOList = new ArrayList<>();
            for (Product product : productList) {
                if (type.equals(product.getProductType())) {
                    productResponseDTOList.add(ValueMapper.convertToDTO(product));
                }
                productsMap.put(type, productResponseDTOList);
            }
        }

        return productsMap;


    }
}
