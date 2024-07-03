package com.emailProcessor.emailProcessor.service.impl;

import com.emailProcessor.basedomains.dto.*;
import com.emailProcessor.emailProcessor.entity.Action;
import com.emailProcessor.emailProcessor.entity.Category;
import com.emailProcessor.emailProcessor.entity.Email;
import com.emailProcessor.emailProcessor.entity.Keyword;
import com.emailProcessor.emailProcessor.repository.ActionRepository;
import com.emailProcessor.emailProcessor.repository.CategoryRepository;
import com.emailProcessor.emailProcessor.service.CategoryService;
import com.emailProcessor.emailProcessor.service.KeywordService;
import lombok.AllArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryServiceImp implements CategoryService {
    private final Logger log = LoggerFactory.getLogger(CategoryService.class);
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;
    private final ActionRepository actionRepository;
    @Lazy
    @Autowired
    private KeywordService keywordService;

    @Autowired
    public void setKeywordService(KeywordService keywordService) {
        this.keywordService = keywordService;
    }

    @Override
    public CategoryDto saveCategory(CategoryDto categoryDto) {
        log.debug("Request to save Category : {}", categoryDto);
        Category category = modelMapper.map(categoryDto, Category.class);
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDto.class);
    }

    @Override
    public CategoryDto updateCategory(Category category) {
        log.debug("Request to update Category : {}", category);
        Category updatedCategory = categoryRepository.save(category);
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
                    if (category.getAction() != null) {
                        Optional<Action> action = actionRepository.findById(category.getAction().getActionId());
                        action.ifPresent(existingCategory::setAction);
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
        Optional<Category> category = categoryRepository.findById(id);
        return category.map(c -> convertCategoryToDto(c));
    }

    @Override
    public ResponseEntity<String> deleteCategory(String id) {
        log.debug("Request to delete Keyword : {}", id);
        try {
            Optional<Category> categorOptional = categoryRepository.findById(id);
            if (categorOptional.isPresent()) {
                Category category = categorOptional.get();

                // Ensure keyword.getCategories() is not null
                if (category.getKeywords() != null) {
                    // Remove keyword by ID
                    for (String keywordId : category.getKeywords()) {
                        if (keywordId != null) {
                            keywordService.deleteRelatedKeyword(keywordId);
                        } else {
                            log.warn("keyword is null in keyword: {}", keywordId);
                        }
                    }
                }
                // Delete the category
                categoryRepository.deleteById(id);
                return ResponseEntity.noContent().build(); // 204 No Content
            } else {
                return ResponseEntity.notFound().build(); // 404 Not Found
            }
        } catch (Exception e) {
            log.error("Error occurred while deleting Category: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete the resource"); // 500 Internal Server Error
        }
    }

    @Override
    public CategoryDto convertCategoryToDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        if (category != null) {
            categoryDto.setCategoryId(category.getCategoryId());
            if (category.getTitle() != null) {
                categoryDto.setTitle(category.getTitle());
            }
            if (category.getDescription() != null) {
                categoryDto.setDescription(category.getDescription());
            }
            if (category.getKeywords() != null) {
                categoryDto.setKeywords(category.getKeywords());
            }
            if (category.getAction() != null) {
                ActionDto actionDto = getActionDto(category.getAction());
                categoryDto.setAction(actionDto);
            }
            // Map other fields if necessary
            return categoryDto;
        }
        return categoryDto;
    }
    public ActionDto getActionDto(Action action) {
        ActionDto actionDto = new ActionDto();
        if (action.getActionId() != null) {
            actionDto.setActionId(action.getActionId());
        }
        actionDto.setAction(action.getAction());
        if (action.getActionDate() != null) {
            actionDto.setActionDate(action.getActionDate());
        }
        if (action.getAction() != null) {
            actionDto.setParams(action.getParams());
        }
        if (action.getAction() != null) {
            actionDto.setAffected(action.getAffected());
        }
        if (action.getAction() != null) {
            actionDto.setState(action.getState());
        }
        if (action.getAction() != null) {
            actionDto.setEndPoint(action.getEndPoint());
        }
        return actionDto;
    }
}