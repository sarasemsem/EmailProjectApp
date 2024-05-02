package com.emailProcessor.emailProcessor.service;
import com.emailProcessor.basedomains.dto.KeywordDto;
import com.emailProcessor.emailProcessor.entity.Keyword;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.emailProcessor.emailProcessor.entity.Keyword}.
 */
public interface KeywordService {
    /**
     * Save a keyword.
     *
     * @param keywordDto the entity to save.
     * @return the persisted entity.
     */
    Keyword save(KeywordDto keywordDto);

    /**
     * Updates a keyword.
     *
     * @param keyword the entity to update.
     * @return the persisted entity.
     */
    Keyword update(Keyword keyword);

    /**
     * Partially updates a keyword.
     *
     * @param keyword the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Keyword> partialUpdate(Keyword keyword);

    /**
     * Get all the keywords.
     *
     * @return the list of entities.
     */
    List<KeywordDto> findAllKeywords();

    /**
     * Get the "id" keyword.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Keyword> findOne(String id);
    Optional<List<Keyword>> KeywordsByCategory(String id);
    Optional<KeywordDto> findKeywordByWord(String word);

    /**
     * Delete the "id" keyword.
     *
     * @param id the id of the entity.
     */
    ResponseEntity<String> delete(String id);

}
