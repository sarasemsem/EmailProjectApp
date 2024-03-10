package com.emailProcessor.emailProcessor.service.impl;

import com.emailProcessor.basedomains.dto.CategoryDto;
import com.emailProcessor.emailProcessor.entity.Category;
import com.emailProcessor.emailProcessor.entity.Sender;
import com.emailProcessor.emailProcessor.repository.CategoryRepository;
import com.emailProcessor.emailProcessor.service.CategoryService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CategoryServiceImp implements CategoryService {
    private final Logger log = LoggerFactory.getLogger(CategoryService.class);
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;

    @Override
    public ResponseEntity<String> saveCategory(Category category) {
        log.debug("Request to save Category : {}", category);
        Category savedCategory= categoryRepository.save(category);
        if (savedCategory.getCategoryId() != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Sender saved successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to insert the Sender");
        }
    }

    @Override
    public CategoryDto updateCategory(Category category) {
        log.debug("Request to update Category : {}", category);
        Category updatedCategory= categoryRepository.save(category);
        return modelMapper.map(updatedCategory, CategoryDto.class);
    }

    @Override
    public Optional<Category> partialUpdateCategory(Category category) {
        log.debug("Request to partially update Category : {}", category);

        return categoryRepository
                .findById(category.getCategoryId())
                .map(existingCategory -> {
                    if (category.getTitle() != null) {
                        existingCategory.setTitle(category.getTitle());
                    }
                    if (category.getDescription() != null) {
                        existingCategory.setDescription(category.getDescription());
                    }

                    return existingCategory;
                })
                .map(categoryRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> findAllCategories() {
        log.debug("Request to get all Categories");
        return categoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryDto> findOneCategory(String id) {
        log.debug("Request to get Category : {}", id);
        Optional<Category> category= categoryRepository.findById(id);
        return Optional.of(modelMapper.map(category, CategoryDto.class));
    }

    @Override
    public void deleteCategory(String id) {
        log.debug("Request to delete Category : {}", id);
        categoryRepository.deleteById(id);
    }
}
