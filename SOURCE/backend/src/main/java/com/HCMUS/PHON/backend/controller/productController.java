package com.HCMUS.PHON.backend.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.HCMUS.PHON.backend.dto.ApiResponseDTO;
import com.HCMUS.PHON.backend.model.Products;
import com.HCMUS.PHON.backend.service.CloudinaryService;
import com.HCMUS.PHON.backend.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CloudinaryService cloudinaryService;

    private <T> ResponseEntity<ApiResponseDTO<T>> buildResponse(int code, String message, T data) {
        return ResponseEntity.status(code).body(new ApiResponseDTO<>(code, message, data));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponseDTO<Products>> updateProduct(@RequestParam Long id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam List<MultipartFile> files,
            @RequestParam double price,
            @RequestParam int quantity,
            @RequestParam List<String> category,
            @RequestParam String brand) {

        List<String> urlImage = new ArrayList<>();
        for (MultipartFile file : files) {
            String url = cloudinaryService.uploadFile(file);
            urlImage.add(url);
        }

        Products updatedProduct = new Products();
        updatedProduct.setId(id);
        updatedProduct.setName(name);
        updatedProduct.setDescription(description);
        updatedProduct.setImages(urlImage);
        updatedProduct.setPrice(price);
        updatedProduct.setQuantity(quantity);
        updatedProduct.setCategory(category);
        updatedProduct.setBrand(brand);

        Products product = productService.updateProduct(updatedProduct);
        if (product != null) {
            return buildResponse(HttpStatus.OK.value(), "Product updated successfully", product);
        } else {
            return buildResponse(HttpStatus.NOT_FOUND.value(), "Product not found", null);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponseDTO<Products>> createProduct(@RequestParam String name,
            @RequestParam String description,
            @RequestParam List<MultipartFile> files,
            @RequestParam double price,
            @RequestParam int quantity,
            @RequestParam List<String> category,
            @RequestParam String brand) {

        List<String> urlImage = new ArrayList<>();
        for (MultipartFile file : files) {
            String url = cloudinaryService.uploadFile(file);
            urlImage.add(url);
        }

        Products newProduct = new Products();
        newProduct.setName(name);
        newProduct.setDescription(description);
        newProduct.setImages(urlImage);
        newProduct.setPrice(price);
        newProduct.setQuantity(quantity);
        newProduct.setCategory(category);
        newProduct.setBrand(brand);

        Products createdProduct = productService.createProduct(newProduct);
        return buildResponse(HttpStatus.CREATED.value(), "Product created successfully", createdProduct);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponseDTO<List<Products>>> getAllProducts() {
        List<Products> products = productService.getAllProducts();
        return buildResponse(HttpStatus.OK.value(), "Success", products);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponseDTO<List<Products>>> getProductByName(@RequestParam String keyword) {
        List<Products> products = productService.findProductsByName(keyword);
        if (!products.isEmpty()) {
            return buildResponse(HttpStatus.OK.value(), "Success", products);
        } else {
            return buildResponse(HttpStatus.NOT_FOUND.value(), "No products found", null);
        }
    }

    @GetMapping("/filtering")
    public ResponseEntity<ApiResponseDTO<List<Products>>> getFilteredProductByCategory(@RequestParam List<String> categories) {
        List<Products> products = productService.filterProductByCategory(categories);
        if (!products.isEmpty()) {
            return buildResponse(HttpStatus.OK.value(), "Success", products);
        } else {
            return buildResponse(HttpStatus.NOT_FOUND.value(), "No products found for categories", null);
        }
    }

    @GetMapping("/price")
    public ResponseEntity<ApiResponseDTO<List<Products>>> getFilteredProductByPriceRange(
            @RequestParam double minPrice,
            @RequestParam double maxPrice) {

        List<Products> products = productService.filterProductByPriceRange(minPrice, maxPrice);
        if (!products.isEmpty()) {
            return buildResponse(HttpStatus.OK.value(), "Success", products);
        } else {
            return buildResponse(HttpStatus.NOT_FOUND.value(), "No products found in price range", null);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ApiResponseDTO<Products>> getProductById(@PathVariable Long id) {
        Products product = productService.getProductById(id);
        if (product != null) {
            return buildResponse(HttpStatus.OK.value(), "Success", product);
        } else {
            return buildResponse(HttpStatus.NOT_FOUND.value(), "Product not found", null);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponseDTO<Object>> deleteProductById(@PathVariable Long id) {
        Products product = productService.getProductById(id);
        if (product == null) {
            return buildResponse(HttpStatus.NOT_FOUND.value(), "Product not found", null);
        } else {
            productService.deleteProduct(id);
            return buildResponse(HttpStatus.OK.value(), "Product deleted successfully", null);
        }
    }
}
