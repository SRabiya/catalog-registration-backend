package com.example.registration.service;

import com.example.registration.dto.CategoryDto;
import com.example.registration.dto.ProductDto;
import com.example.registration.entity.Category;
import com.example.registration.entity.Product;
import com.example.registration.entity.ProductImage;
import com.example.registration.repository.CategoryRepository;
import com.example.registration.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CatalogService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CatalogService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(cat -> new CategoryDto(cat.getId(), cat.getName()))
                .collect(Collectors.toList());
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToProductDto)
                .collect(Collectors.toList());
    }

    private ProductDto mapToProductDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        
        if (product.getCategory() != null) {
            dto.setCategoryName(product.getCategory().getName());
        }
        
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            dto.setImageUrl(product.getImages().get(0).getImageUrl());
        }
        
        return dto;
    }
}
