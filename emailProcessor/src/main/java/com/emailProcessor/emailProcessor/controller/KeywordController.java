package com.emailProcessor.emailProcessor.controller;
import com.emailProcessor.basedomains.dto.CustomResponse;
import com.emailProcessor.basedomains.dto.KeywordDto;
import com.emailProcessor.emailProcessor.controller.errors.BadRequestException;
import com.emailProcessor.emailProcessor.entity.*;
import com.emailProcessor.emailProcessor.repository.KeywordRepository;
import com.emailProcessor.emailProcessor.service.KeywordService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.net.URISyntaxException;
import java.util.*;

/**
 * REST controller for managing {@link com.emailProcessor.emailProcessor.entity.Keyword}.
 */
@RestController
@RequestMapping("/api/v1/keywords")
@RequiredArgsConstructor
public class KeywordController {

    private final Logger log = LoggerFactory.getLogger(KeywordController.class);
    private static final String ENTITY_NAME = "keyword";
    private final KeywordService keywordService;
    private final KeywordRepository keywordRepository;
    private final ModelMapper modelMapper;

    @PostMapping("")
    public ResponseEntity<CustomResponse> createKeyword(@Validated @RequestBody KeywordDto keywordDto) throws URISyntaxException {
        log.debug("REST request to save Keyword : {}", keywordDto);
        System.out.println("REST request to save Keyword : {}"+ keywordDto);
        try {
            Keyword result = keywordService.save(keywordDto);
            // Create a custom response object with both data and HTTP status
            CustomResponse customResponse = new CustomResponse(result, HttpStatus.CREATED.value(), "Keyword saved successfully");
            clearCache();
            return ResponseEntity.status(HttpStatus.CREATED).body(customResponse);
        }catch (Exception e) {
            log.error("Error saving Keyword", e);
            CustomResponse customResponse = new CustomResponse(keywordDto, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error saving keyword");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(customResponse);
        }
    }

    @PutMapping("/{keywordId}")
    public ResponseEntity<Keyword> updateKeyword(
        @PathVariable(value = "keywordId", required = false) final String keywordId,
        @Valid @RequestBody Keyword keyword
    ) throws URISyntaxException {
        log.debug("REST request to update Keyword : {}, {}", keywordId, keyword);
        if (keyword.getKeywordId() == null) {
            throw new BadRequestException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(keywordId, keyword.getKeywordId())) {
            throw new BadRequestException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!keywordRepository.existsById(keywordId)) {
            throw new BadRequestException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        Keyword result = keywordService.update(keyword);
        return ResponseEntity
                .ok()
                .body(result);
    }

    @PatchMapping(value = "/{keywordId}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Keyword> partialUpdateKeyword(
        @PathVariable(value = "keywordId", required = false) final String keywordId,
        @NotNull @RequestBody Keyword keyword
    ) throws URISyntaxException {
        log.debug("REST request to partial update Keyword partially : {}, {}", keywordId, keyword);
        if (keyword.getKeywordId() == null) {
            throw new BadRequestException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(keywordId, keyword.getKeywordId())) {
            throw new BadRequestException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!keywordRepository.existsById(keywordId)) {
            throw new BadRequestException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Keyword> result = keywordService.partialUpdate(keyword);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("")
    public List<KeywordDto> getAllKeywords() {
        log.debug("REST request to get all Keywords");
        List<KeywordDto> keywords = keywordService.findAllKeywords();
        System.out.println(keywords);
        return keywords;
    }

    /**
     * {@code GET  /keywords/:id} : get the "id" keyword.
     *
     * @param id the id of the keyword to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the keyword, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Keyword> getKeyword(@PathVariable("id") String id) {
        log.debug("REST request to get Keyword : {}", id);
        Optional<Keyword> keyword = keywordService.findOne(id);
        return keyword.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("keywordsByCategory/{id}")
    public ResponseEntity<Optional<List<Keyword>>> getKeywordsByCategory(@PathVariable("id") String id) {
        log.debug("REST request to get Keyword : {}", id);
        Optional<List<Keyword>> keyword = keywordService.KeywordsByCategory(id);
        return keyword.map(keywords -> ResponseEntity.ok(Optional.of(keywords))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * {@code DELETE  /keywords/:id} : delete the "id" keyword.
     *
     * @param id the id of the keyword to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteKeyword(@PathVariable("id") String id) {
        log.debug("REST request to delete Keyword : {}", id);
        // Attempt to delete the keyword
        log.debug("REST request to delete Keyword : {}", id);
        return keywordService.delete(id);
    }

    @GetMapping("/clear_cache")
    @CacheEvict(value = "keyword", allEntries = true )
    public String clearCache(){
        return "Cache has been cleared";
    }
}
