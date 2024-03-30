package com.emailProcessor.emailProcessor.service.impl;

import com.emailProcessor.basedomains.dto.CategoryDto;
import com.emailProcessor.basedomains.dto.EmailDto;
import com.emailProcessor.emailProcessor.entity.Category;
import com.emailProcessor.emailProcessor.entity.Sender;
import com.emailProcessor.emailProcessor.repository.CategoryRepository;
import com.emailProcessor.emailProcessor.service.CategoryService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachePut;
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
    public CategoryDto saveCategory(Category category) {
        log.debug("Request to save Category : {}", category);
        Category savedCategory =categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDto.class);
    }

    @Override
    //@CachePut(value = "categories", key = "'categories'")
    public CategoryDto updateCategory(Category category) {
        log.debug("Request to update Category : {}", category);
        Category updatedCategory = categoryRepository.save(category);
        return modelMapper.map(updatedCategory, CategoryDto.class);
    }

    @Override
    //@CachePut(value = "categories", key = "'categories'")
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
    public List<Category> findAllCategories() {
        log.debug("Request to get all Categories");
        return categoryRepository.findAll();
    }

    @Override
    public Optional<CategoryDto> findOneCategory(String id) {
        log.debug("Request to get Category : {}", id);
        System.out.println("the Id of category is" + id);
        Optional<Category> category = categoryRepository.findById(id);
        return category.map(c -> modelMapper.map(c, CategoryDto.class));
    }

    @Override
    public void deleteCategory(String id) {
        log.debug("Request to delete Category : {}", id);
        categoryRepository.deleteById(id);
    }

    //@CachePut(value = "categories", key = "'categories'")
    private List<CategoryDto> updateCachedEmailList(CategoryDto categoryDto) {
        List<Category> categories = findAllCategories();
        List<CategoryDto> cachedCategory = categories.stream()
                .map(c -> modelMapper.map(c, CategoryDto.class))
                .toList();
        // Find the email with the same ID as the saved/updated email
        for (int i = 0; i < cachedCategory.size(); i++) {
            if (cachedCategory.get(i).getCategoryId().equals(categoryDto.getCategoryId())) {
                // Update the email in the cached list
                cachedCategory.set(i, categoryDto);
                break;
            }
        }
        return cachedCategory ;
    }
}