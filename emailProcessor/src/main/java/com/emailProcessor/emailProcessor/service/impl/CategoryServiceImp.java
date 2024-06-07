package com.emailProcessor.emailProcessor.service.impl;

import com.emailProcessor.basedomains.dto.ActionDto;
import com.emailProcessor.basedomains.dto.CategoryDto;
import com.emailProcessor.basedomains.dto.EmailDto;
import com.emailProcessor.emailProcessor.entity.Action;
import com.emailProcessor.emailProcessor.entity.Category;
import com.emailProcessor.emailProcessor.entity.Email;
import com.emailProcessor.emailProcessor.repository.ActionRepository;
import com.emailProcessor.emailProcessor.repository.CategoryRepository;
import com.emailProcessor.emailProcessor.service.CategoryService;
import lombok.AllArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
        return category.map(c -> modelMapper.map(c, CategoryDto.class));
    }

    @Override
    public void deleteCategory(String id) {
        log.debug("Request to delete Category : {}", id);
        categoryRepository.deleteById(id);
    }


}