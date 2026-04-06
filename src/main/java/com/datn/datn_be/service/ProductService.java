package com.datn.datn_be.service;

import com.datn.datn_be.dto.CreateProductRequest;
import com.datn.datn_be.dto.PaginationResponse;
import com.datn.datn_be.dto.ProductImageResponse;
import com.datn.datn_be.dto.ProductResponse;
import com.datn.datn_be.dto.UpdateProductRequest;
import com.datn.datn_be.entity.Product;
import com.datn.datn_be.entity.ProductImage;
import com.datn.datn_be.exception.ClientSideException;
import com.datn.datn_be.repository.ProductImageRepository;
import com.datn.datn_be.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryService categoryService;

    /**
     * Create new product
     */
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        // Validate required fields
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new ClientSideException(400, "Product name is required");
        }
        if (request.getPrice() == null) {
            throw new ClientSideException(400, "Product price is required");
        }

        Product product = new Product();
        product.setName(request.getName());
        product.setSku(request.getSku());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategoryId(request.getCategoryId());
        product.setBrand(request.getBrand());
        product.setStatus("ACTIVE");
        product.setCreatedAt(Instant.now());
        product.setDescriptionDetail(request.getDescriptionDetail());

        Product savedProduct = productRepository.save(product);
        if (request.getImageUrls() != null) {
            for (String url : request.getImageUrls()) {
                if (url != null && !url.trim().isEmpty()) {
                    ProductImage image = new ProductImage();
                    image.setProductId(savedProduct.getId());
                    image.setImageUrl(url.trim());
                    productImageRepository.save(image);
                }
            }
        }
        return mapProductToResponse(savedProduct);
    }

    /**
     * Get product by ID
     */
    public ProductResponse getProductById(String productId) {
        UUID productUUID = UUID.fromString(productId);
        Optional<Product> productOptional = productRepository.findById(productUUID);

        if (productOptional.isEmpty()) {
            throw new ClientSideException(404, "Product not found");
        }

        return mapProductToResponse(productOptional.get());
    }

    /**
     * Get all products with pagination
     */
    public PaginationResponse<ProductResponse> getAllProducts(int page, int pageSize, String sortBy, String sortDirection) {
        // Validate pagination parameters
        if (page < 0) page = 0;
        if (pageSize <= 0) pageSize = 10;
        if (pageSize > 100) pageSize = 100; // Max page size

        Sort.Direction direction = "DESC".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortBy != null ? sortBy : "createdAt"));

        Page<Product> productPage = productRepository.findAll(pageable);

        List<ProductResponse> content = productPage.getContent()
                .stream()
                .map(this::mapProductToResponse)
                .collect(Collectors.toList());

        PaginationResponse<ProductResponse> response = new PaginationResponse<>();
        response.setPage(page);
        response.setPageSize(pageSize);
        response.setTotalElements(productPage.getTotalElements());
        response.setTotalPages(productPage.getTotalPages());
        response.setContent(content);
        response.setHasNext(productPage.hasNext());
        response.setHasPrevious(productPage.hasPrevious());

        return response;
    }

    /**
     * Search products by name
     */
    public PaginationResponse<ProductResponse> searchProducts(String keyword, int page, int pageSize) {
        if (page < 0) page = 0;
        if (pageSize <= 0) pageSize = 10;
        if (pageSize > 100) pageSize = 100;

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Product> productPage = productRepository.findByNameContaining(keyword, pageable);

        List<ProductResponse> content = productPage.getContent()
                .stream()
                .map(this::mapProductToResponse)
                .collect(Collectors.toList());

        PaginationResponse<ProductResponse> response = new PaginationResponse<>();
        response.setPage(page);
        response.setPageSize(pageSize);
        response.setTotalElements(productPage.getTotalElements());
        response.setTotalPages(productPage.getTotalPages());
        response.setContent(content);
        response.setHasNext(productPage.hasNext());
        response.setHasPrevious(productPage.hasPrevious());

        return response;
    }

    /**
     * Get products by category including all subcategories
     */
    public PaginationResponse<ProductResponse> getProductsByCategory(Integer categoryId, int page, int pageSize) {
        if (page < 0) page = 0;
        if (pageSize <= 0) pageSize = 10;
        if (pageSize > 100) pageSize = 100;

        // Get all category IDs including subcategories
        List<Integer> categoryIds = categoryService.getAllCategoryIdsIncludingSubcategories(categoryId);

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Product> productPage = productRepository.findByCategoryIdIn(categoryIds, pageable);

        List<ProductResponse> content = productPage.getContent()
                .stream()
                .map(this::mapProductToResponse)
                .collect(Collectors.toList());

        PaginationResponse<ProductResponse> response = new PaginationResponse<>();
        response.setPage(page);
        response.setPageSize(pageSize);
        response.setTotalElements(productPage.getTotalElements());
        response.setTotalPages(productPage.getTotalPages());
        response.setContent(content);
        response.setHasNext(productPage.hasNext());
        response.setHasPrevious(productPage.hasPrevious());

        return response;
    }

    /**
     * Update product
     */
    @Transactional
    public ProductResponse updateProduct(String productId, UpdateProductRequest request) {
        UUID productUUID = UUID.fromString(productId);
        Optional<Product> productOptional = productRepository.findById(productUUID);

        if (productOptional.isEmpty()) {
            throw new ClientSideException(404, "Product not found");
        }

        Product product = productOptional.get();

        // Update fields if provided
        if (request.getName() != null && !request.getName().isEmpty()) {
            product.setName(request.getName());
        }
        if (request.getSku() != null && !request.getSku().isEmpty()) {
            product.setSku(request.getSku());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getCategoryId() != null) {
            product.setCategoryId(request.getCategoryId());
        }
        if (request.getBrand() != null && !request.getBrand().isEmpty()) {
            product.setBrand(request.getBrand());
        }
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            product.setStatus(request.getStatus());
        }
        if (request.getDescriptionDetail() != null) {
            product.setDescriptionDetail(request.getDescriptionDetail());
        }

        Product updatedProduct = productRepository.save(product);
        if (request.getImageUrls() != null) {
            productImageRepository.deleteByProductId(productUUID);
            for (String url : request.getImageUrls()) {
                if (url != null && !url.trim().isEmpty()) {
                    ProductImage image = new ProductImage();
                    image.setProductId(updatedProduct.getId());
                    image.setImageUrl(url.trim());
                    productImageRepository.save(image);
                }
            }
        }
        return mapProductToResponse(updatedProduct);
    }

    /**
     * Delete product
     */
    @Transactional
    public void deleteProduct(String productId) {
        UUID productUUID = UUID.fromString(productId);
        Optional<Product> productOptional = productRepository.findById(productUUID);

        if (productOptional.isEmpty()) {
            throw new ClientSideException(404, "Product not found");
        }

        productImageRepository.deleteByProductId(productUUID);
        productRepository.deleteById(productUUID);
    }

    /**
     * Map Product entity to ProductResponse DTO
     */
    private ProductResponse mapProductToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setProductId(product.getId().toString());
        response.setName(product.getName());
        response.setSku(product.getSku());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setCategoryId(product.getCategoryId());
        response.setBrand(product.getBrand());
        response.setStatus(product.getStatus());
        response.setCreatedAt(product.getCreatedAt() != null ? product.getCreatedAt().toString() : null);
        response.setDescriptionDetail(product.getDescriptionDetail());
        List<String> imageUrls = productImageRepository.findByProductId(product.getId())
                .stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());
        response.setImageUrls(imageUrls);
        return response;
    }

    // ── Image management methods ─────────────────────────────────────────────

    public List<ProductImageResponse> getProductImages(String productId) {
        UUID productUUID = UUID.fromString(productId);
        return productImageRepository.findByProductId(productUUID)
                .stream()
                .map(img -> new ProductImageResponse(img.getId(), img.getImageUrl()))
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductImageResponse addProductImage(String productId, String imageUrl) {
        UUID productUUID = UUID.fromString(productId);
        if (!productRepository.existsById(productUUID)) {
            throw new ClientSideException(404, "Product not found");
        }
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new ClientSideException(400, "Image URL is required");
        }
        ProductImage image = new ProductImage();
        image.setProductId(productUUID);
        image.setImageUrl(imageUrl.trim());
        ProductImage saved = productImageRepository.save(image);
        return new ProductImageResponse(saved.getId(), saved.getImageUrl());
    }

    @Transactional
    public void deleteProductImage(Integer imageId) {
        if (!productImageRepository.existsById(imageId)) {
            throw new ClientSideException(404, "Image not found");
        }
        productImageRepository.deleteById(imageId);
    }

    @Transactional
    public List<ProductImageResponse> replaceProductImages(String productId, List<String> imageUrls) {
        UUID productUUID = UUID.fromString(productId);
        if (!productRepository.existsById(productUUID)) {
            throw new ClientSideException(404, "Product not found");
        }
        productImageRepository.deleteByProductId(productUUID);
        List<ProductImageResponse> result = new java.util.ArrayList<>();
        if (imageUrls != null) {
            for (String url : imageUrls) {
                if (url != null && !url.trim().isEmpty()) {
                    ProductImage image = new ProductImage();
                    image.setProductId(productUUID);
                    image.setImageUrl(url.trim());
                    ProductImage saved = productImageRepository.save(image);
                    result.add(new ProductImageResponse(saved.getId(), saved.getImageUrl()));
                }
            }
        }
        return result;
    }

    /**
     * Filter products by brand
     */
    public PaginationResponse<ProductResponse> filterProductsByBrand(String brand, int page, int pageSize) {
        if (page < 0) page = 0;
        if (pageSize <= 0) pageSize = 10;
        if (pageSize > 100) pageSize = 100;

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Product> products = productRepository.findAll()
                .stream()
                .filter(p -> p.getBrand() != null && p.getBrand().equalsIgnoreCase(brand))
                .collect(Collectors.toList());

        int totalElements = products.size();
        int totalPages = (totalElements + pageSize - 1) / pageSize;
        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalElements);

        List<ProductResponse> content = products.subList(startIndex, endIndex)
                .stream()
                .map(this::mapProductToResponse)
                .collect(Collectors.toList());

        PaginationResponse<ProductResponse> response = new PaginationResponse<>();
        response.setPage(page);
        response.setPageSize(pageSize);
        response.setTotalElements(totalElements);
        response.setTotalPages(totalPages);
        response.setContent(content);
        response.setHasNext(page < totalPages - 1);
        response.setHasPrevious(page > 0);

        return response;
    }

    /**
     * Filter products by price range
     */
    public PaginationResponse<ProductResponse> filterProductsByPriceRange(
            java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice, int page, int pageSize) {
        if (page < 0) page = 0;
        if (pageSize <= 0) pageSize = 10;
        if (pageSize > 100) pageSize = 100;

        List<Product> products = productRepository.findAll()
                .stream()
                .filter(p -> p.getPrice().compareTo(minPrice) >= 0 && p.getPrice().compareTo(maxPrice) <= 0)
                .collect(Collectors.toList());

        int totalElements = products.size();
        int totalPages = (totalElements + pageSize - 1) / pageSize;
        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalElements);

        List<ProductResponse> content = products.subList(startIndex, endIndex)
                .stream()
                .map(this::mapProductToResponse)
                .collect(Collectors.toList());

        PaginationResponse<ProductResponse> response = new PaginationResponse<>();
        response.setPage(page);
        response.setPageSize(pageSize);
        response.setTotalElements(totalElements);
        response.setTotalPages(totalPages);
        response.setContent(content);
        response.setHasNext(page < totalPages - 1);
        response.setHasPrevious(page > 0);

        return response;
    }

    /**
     * Filter products by status
     */
    public PaginationResponse<ProductResponse> filterProductsByStatus(String status, int page, int pageSize) {
        if (page < 0) page = 0;
        if (pageSize <= 0) pageSize = 10;
        if (pageSize > 100) pageSize = 100;

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Product> products = productRepository.findAll()
                .stream()
                .filter(p -> p.getStatus() != null && p.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());

        int totalElements = products.size();
        int totalPages = (totalElements + pageSize - 1) / pageSize;
        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalElements);

        List<ProductResponse> content = products.subList(startIndex, endIndex)
                .stream()
                .map(this::mapProductToResponse)
                .collect(Collectors.toList());

        PaginationResponse<ProductResponse> response = new PaginationResponse<>();
        response.setPage(page);
        response.setPageSize(pageSize);
        response.setTotalElements(totalElements);
        response.setTotalPages(totalPages);
        response.setContent(content);
        response.setHasNext(page < totalPages - 1);
        response.setHasPrevious(page > 0);

        return response;
    }

    /**
     * Filter products by category and price range
     */
    public PaginationResponse<ProductResponse> filterProductsByCategoryAndPrice(
            Integer categoryId, java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice, int page, int pageSize) {
        if (page < 0) page = 0;
        if (pageSize <= 0) pageSize = 10;
        if (pageSize > 100) pageSize = 100;

        List<Product> products = productRepository.findAll()
                .stream()
                .filter(p -> p.getCategoryId() != null && p.getCategoryId().equals(categoryId))
                .filter(p -> p.getPrice().compareTo(minPrice) >= 0 && p.getPrice().compareTo(maxPrice) <= 0)
                .collect(Collectors.toList());

        int totalElements = products.size();
        int totalPages = (totalElements + pageSize - 1) / pageSize;
        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalElements);

        List<ProductResponse> content = products.subList(startIndex, endIndex)
                .stream()
                .map(this::mapProductToResponse)
                .collect(Collectors.toList());

        PaginationResponse<ProductResponse> response = new PaginationResponse<>();
        response.setPage(page);
        response.setPageSize(pageSize);
        response.setTotalElements(totalElements);
        response.setTotalPages(totalPages);
        response.setContent(content);
        response.setHasNext(page < totalPages - 1);
        response.setHasPrevious(page > 0);

        return response;
    }
}
