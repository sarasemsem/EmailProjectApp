package com.emailProcessor.emailProcessor.service;
import com.emailProcessor.basedomains.dto.LinkedActionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
/**
 * Service Interface for managing
 */
@Service
public interface LinkedCategoryService {
    /**
     * Save a linkedCategory.
     *
     * @param linkedActionDto the entity to save.
     * @return the persisted entity.
     */
    ResponseEntity<LinkedActionDto> saveLinkedCategory(LinkedActionDto linkedActionDto);

    /**
     * Updates a linkedCategory.
     *
     * @param linkedActionDto the entity to update.
     * @return the persisted entity.
     */
    LinkedActionDto updateLinkedCategory(LinkedActionDto linkedActionDto);

    /**
     * Partially updates a linkedCategory.
     *
     * @param linkedActionDto the entity to update partially.
     * @return the persisted entity.
     */
    Optional<LinkedActionDto> UpdateLinkedCategory(LinkedActionDto linkedActionDto);

    /**
     * Get all the linkedCategories.
     *
     * @return the list of entities.
     */
    List<LinkedActionDto> findAllLinkedCategories();

    /**
     * Get all the LinkedCategory where Category is {@code null}.
     *
     * @return the {@link List} of entities.
     */
    List<LinkedActionDto> findAllLinkedCategoryWhereCategoryIsNull();

    /**
     * Get the "id" linkedCategory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<LinkedActionDto> findOneLinkedCategory(String id);

    /**
     * Delete the "id" linkedCategory.
     *
     * @param id the id of the entity.
     */
    void deleteLinkedCategory(String id);
}
