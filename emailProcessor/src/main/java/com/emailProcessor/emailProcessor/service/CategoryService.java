package com.emailProcessor.emailProcessor.service;
import com.emailProcessor.basedomains.dto.CategoryDto;
import com.emailProcessor.emailProcessor.entity.Category;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public interface CategoryService {

    CategoryDto saveCategory(Category category);

    CategoryDto updateCategory(Category category);

    Optional<Category> partialUpdateCategory(Category category);

    /**
     * Get all the categories.
     *
     * @return the list of entities.
     */
    List<Category> findAllCategories();

    /**
     * Get the "id" category.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CategoryDto> findOneCategory(String id);

    /**
     * Delete the "id" category.
     *
     * @param id the id of the entity.
     */
    void deleteCategory(String id);
}
