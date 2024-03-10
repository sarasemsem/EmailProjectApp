package com.emailProcessor.emailProcessor.service.impl;

import com.emailProcessor.basedomains.dto.LinkedActionDto;
import com.emailProcessor.emailProcessor.service.LinkedCategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LinkedCategoryServiceImp implements LinkedCategoryService {
    @Override
    public ResponseEntity<LinkedActionDto> saveLinkedCategory(LinkedActionDto linkedActionDto) {
        return null;
    }

    @Override
    public LinkedActionDto updateLinkedCategory(LinkedActionDto linkedActionDto) {
        return null;
    }

    @Override
    public Optional<LinkedActionDto> UpdateLinkedCategory(LinkedActionDto linkedActionDto) {
        return Optional.empty();
    }

    @Override
    public List<LinkedActionDto> findAllLinkedCategories() {
        return null;
    }

    @Override
    public List<LinkedActionDto> findAllLinkedCategoryWhereCategoryIsNull() {
        return null;
    }

    @Override
    public Optional<LinkedActionDto> findOneLinkedCategory(String id) {
        return Optional.empty();
    }

    @Override
    public void deleteLinkedCategory(String id) {

    }
}
