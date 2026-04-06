package com.datn.datn_be.service;

import com.datn.datn_be.dto.CategoryResponse;
import com.datn.datn_be.dto.CreateCategoryRequest;
import com.datn.datn_be.dto.UpdateCategoryRequest;
import com.datn.datn_be.entity.Category;
import com.datn.datn_be.exception.ClientSideException;
import com.datn.datn_be.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Create new category
     */
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        // Validate required fields
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new ClientSideException(400, "Category name is required");
        }

        // Validate parent category exists if provided
        if (request.getParentId() != null) {
            Optional<Category> parentOptional = categoryRepository.findById(request.getParentId());
            if (parentOptional.isEmpty()) {
                throw new ClientSideException(400, "Parent category not found");
            }
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setParentId(request.getParentId());

        Category savedCategory = categoryRepository.save(category);
        return mapCategoryToResponse(savedCategory);
    }

    /**
     * Get category by ID
     */
    public CategoryResponse getCategoryById(Integer categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);

        if (categoryOptional.isEmpty()) {
            throw new ClientSideException(404, "Category not found");
        }

        return mapCategoryToResponse(categoryOptional.get());
    }

    /**
     * Get all categories
     */
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::mapCategoryToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get root categories (parentId = null)
     */
    public List<CategoryResponse> getRootCategories() {
        return categoryRepository.findAll()
                .stream()
                .filter(c -> c.getParentId() == null)
                .map(this::mapCategoryToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get subcategories by parent ID
     */
    public List<CategoryResponse> getSubcategoriesByParentId(Integer parentId) {
        // Validate parent exists
        Optional<Category> parentOptional = categoryRepository.findById(parentId);
        if (parentOptional.isEmpty()) {
            throw new ClientSideException(404, "Parent category not found");
        }

        return categoryRepository.findAll()
                .stream()
                .filter(c -> c.getParentId() != null && c.getParentId().equals(parentId))
                .map(this::mapCategoryToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update category
     */
    @Transactional
    public CategoryResponse updateCategory(Integer categoryId, UpdateCategoryRequest request) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);

        if (categoryOptional.isEmpty()) {
            throw new ClientSideException(404, "Category not found");
        }

        Category category = categoryOptional.get();

        // Update name if provided
        if (request.getName() != null && !request.getName().isEmpty()) {
            category.setName(request.getName());
        }

        // Update parent if provided
        if (request.getParentId() != null) {
            // Validate parent exists
            Optional<Category> parentOptional = categoryRepository.findById(request.getParentId());
            if (parentOptional.isEmpty()) {
                throw new ClientSideException(400, "Parent category not found");
            }
            category.setParentId(request.getParentId());
        } else if (request.getParentId() == null) {
            // Allow setting parentId to null (making it a root category)
            category.setParentId(null);
        }

        Category updatedCategory = categoryRepository.save(category);
        return mapCategoryToResponse(updatedCategory);
    }

    /**
     * Delete category
     */
    @Transactional
    public void deleteCategory(Integer categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);

        if (categoryOptional.isEmpty()) {
            throw new ClientSideException(404, "Category not found");
        }

        // Check if category has subcategories
        List<Category> subcategories = categoryRepository.findAll()
                .stream()
                .filter(c -> c.getParentId() != null && c.getParentId().equals(categoryId))
                .collect(Collectors.toList());

        if (!subcategories.isEmpty()) {
            throw new ClientSideException(400, "Cannot delete category with subcategories. Please delete subcategories first.");
        }

        categoryRepository.deleteById(categoryId);
    }

    /**
     * Map Category entity to CategoryResponse DTO
     */
    private CategoryResponse mapCategoryToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setCategoryId(category.getId());
        response.setName(category.getName());
        response.setParentId(category.getParentId());
        return response;
    }

    /**
     * Get all categories with hierarchical structure (including subcategories)
     */
    public List<com.datn.datn_be.dto.CategoryDetailResponse> getAllCategoriesWithHierarchy() {
        List<Category> allCategories = categoryRepository.findAll();

        // Get only root categories and build hierarchy
        return allCategories.stream()
                .filter(c -> c.getParentId() == null)
                .map(c -> mapCategoryToDetailResponse(c, allCategories))
                .collect(Collectors.toList());
    }

    /**
     * Get root categories with their subcategories
     */
    public List<com.datn.datn_be.dto.CategoryDetailResponse> getRootCategoriesWithSubcategories() {
        List<Category> allCategories = categoryRepository.findAll();

        return allCategories.stream()
                .filter(c -> c.getParentId() == null)
                .map(c -> mapCategoryToDetailResponse(c, allCategories))
                .collect(Collectors.toList());
    }

    /**
     * Map Category to CategoryDetailResponse with recursive subcategories
     */
    private com.datn.datn_be.dto.CategoryDetailResponse mapCategoryToDetailResponse(
            Category category, List<Category> allCategories) {
        com.datn.datn_be.dto.CategoryDetailResponse response = new com.datn.datn_be.dto.CategoryDetailResponse();
        response.setCategoryId(category.getId());
        response.setName(category.getName());
        response.setParentId(category.getParentId());

        // Get subcategories recursively
        List<com.datn.datn_be.dto.CategoryDetailResponse> subcategories = allCategories.stream()
                .filter(c -> c.getParentId() != null && c.getParentId().equals(category.getId()))
                .map(c -> mapCategoryToDetailResponse(c, allCategories))
                .collect(Collectors.toList());

        response.setSubcategories(subcategories);
        return response;
    }

    /**
     * Get all category IDs including the parent and all subcategories recursively
     */
    public List<Integer> getAllCategoryIdsIncludingSubcategories(Integer categoryId) {
        List<Integer> categoryIds = new java.util.ArrayList<>();
        categoryIds.add(categoryId);

        // Recursively add all subcategories
        addSubcategoryIds(categoryId, categoryIds);

        return categoryIds;
    }

    /**
     * Recursively add subcategory IDs
     */
    private void addSubcategoryIds(Integer parentId, List<Integer> categoryIds) {
        List<Integer> subcategoryIds = categoryRepository.findSubcategoryIdsByParentId(parentId);
        for (Integer subcategoryId : subcategoryIds) {
            categoryIds.add(subcategoryId);
            addSubcategoryIds(subcategoryId, categoryIds);
        }
    }
}
