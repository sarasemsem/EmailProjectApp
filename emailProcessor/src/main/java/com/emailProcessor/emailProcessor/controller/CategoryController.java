package com.emailProcessor.emailProcessor.controller;
import com.emailProcessor.basedomains.dto.CategoryDto;
import com.emailProcessor.emailProcessor.controller.errors.BadRequestException;
import com.emailProcessor.emailProcessor.entity.Category;
import com.emailProcessor.emailProcessor.repository.CategoryRepository;
import com.emailProcessor.emailProcessor.service.CategoryService;
import com.mongodb.lang.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link com.emailProcessor.basedomains.dto.CategoryDto}.
 */
@RestController
@RequestMapping("api/v1/categories")
public class CategoryController {

    private final Logger log = LoggerFactory.getLogger(CategoryController.class);

    private static final String ENTITY_NAME = "category";



    private final CategoryService categoryService;

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryService categoryService, CategoryRepository categoryRepository) {
        this.categoryService = categoryService;
        this.categoryRepository = categoryRepository;
    }

    /**
     * {@code POST  /categories} : Create a new category.
     *
     * @param category the category to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new category, or with status {@code 400 (Bad Request)} if the category has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<String> createCategory(@Validated @RequestBody Category category) throws Exception {
        log.debug("REST request to save Category : {}", category);
        if (category.getCategoryId() != null) {
            throw new Exception("A new category cannot already have an ID");
        }

        try {
            ResponseEntity<String> result = categoryService.saveCategory(category);
            return result;
        } catch (BadRequestException e) {
            log.error("Bad request alert exception", e);
            return ResponseEntity.badRequest().body("Bad request: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error saving sender", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving sender");
        }
    }

    /**
     * {@code PUT  /categories/:categoryId} : Updates an existing category.
     *
     * @param categoryId the id of the category to save.
     * @param category the category to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated category,
     * or with status {@code 400 (Bad Request)} if the category is not valid,
     * or with status {@code 500 (Internal Server Error)} if the category couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(
        @PathVariable(value = "categoryId", required = false) final String categoryId,
        @Validated @RequestBody Category category
    ) throws URISyntaxException {
        log.debug("REST request to update Category : {}, {}", categoryId, category);
        if (category.getCategoryId() == null) {
            throw new BadRequestException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(categoryId, category.getCategoryId())) {
            throw new BadRequestException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!categoryRepository.existsById(categoryId)) {
            throw new BadRequestException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CategoryDto result = categoryService.updateCategory(category);
        return  ResponseEntity
                .ok()
                .body(result);
    }

    /**
     * {@code PATCH  /categories/:categoryId} : Partial updates given fields of an existing category, field will ignore if it is null
     *
     * @param categoryId the id of the category to save.
     * @param category   the category to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated category,
     * or with status {@code 400 (Bad Request)} if the category is not valid,
     * or with status {@code 404 (Not Found)} if the category is not found,
     * or with status {@code 500 (Internal Server Error)} if the category couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{categoryId}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Optional<Category>> partialUpdateCategory(
        @PathVariable(value = "categoryId", required = false) final String categoryId,
        @NonNull
        @RequestBody Category category
    ) throws URISyntaxException {
        log.debug("REST request to partial update Category partially : {}, {}", categoryId, category);
        if (category.getCategoryId() == null) {
            throw new BadRequestException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(categoryId, category.getCategoryId())) {
            throw new BadRequestException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!categoryRepository.existsById(categoryId)) {
            throw new BadRequestException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Category> result = categoryService.partialUpdateCategory(category);

        return ResponseEntity.ok(Optional.of(result.get()));
    }

    /**
     * {@code GET  /categories} : get all the categories.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of categories in body.
     */
    @GetMapping("")
    public List<Category> getAllCategories() {
        log.debug("REST request to get all Categories");
        return categoryService.findAllCategories();
    }

    /**
     * {@code GET  /categories/:id} : get the "id" category.
     *
     * @param id the id of the category to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the category, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable("id") String id) {
        log.debug("REST request to get Category : {}", id);
        Optional<CategoryDto> category = categoryService.findOneCategory(id);
        if (category.isPresent()) {
            log.debug("Sender found: {}", category.get());
            return ResponseEntity.ok(category.get());
        } else {
            log.debug("Sender not found for id: {}", category);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * {@code DELETE  /categories/:id} : delete the "id" category.
     *
     * @param id the id of the category to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable("id") String id) {
        log.debug("REST request to delete Category : {}", id);
        categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.OK).body("Saving sender");
    }
}
