package com.datn.datn_be.repository;

import com.datn.datn_be.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByName(String name);

    @Query("SELECT c.id FROM Category c WHERE c.parentId = :parentId")
    List<Integer> findSubcategoryIdsByParentId(@Param("parentId") Integer parentId);
}

