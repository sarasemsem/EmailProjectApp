package com.emailProcessor.emailProcessor.controller;

import com.emailProcessor.basedomains.dto.ActionDto;
import com.emailProcessor.basedomains.dto.CategoryDto;
import com.emailProcessor.basedomains.dto.CustomResponse;
import com.emailProcessor.emailProcessor.controller.errors.BadRequestException;
import com.emailProcessor.emailProcessor.entity.Action;
import com.emailProcessor.emailProcessor.entity.Category;
import com.emailProcessor.emailProcessor.repository.ActionRepository;
import com.emailProcessor.emailProcessor.service.ActionService;
import com.emailProcessor.emailProcessor.service.CategoryService;
import com.mongodb.lang.NonNull;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing {@link ActionDto}.
 */
@RestController
@RequestMapping("api/v1/actions")
@RequiredArgsConstructor
public class ActionController {

    private final Logger log = LoggerFactory.getLogger(ActionController.class);

    private static final String ENTITY_NAME = "action";


    private final ModelMapper modelMapper;
    private final ActionService actionService;
    private final CategoryService categoryService;
    private final ActionRepository actionRepository;

    /**
     * {@code POST  /actions} : Create a new action.
     *
     * @param actionDto the action to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new action, or with status {@code 400 (Bad Request)} if the action has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CustomResponse> createAction(@Validated @RequestBody ActionDto actionDto) throws Exception {
        log.debug("REST request to save action : {}", actionDto);
        if (actionDto.getActionId() != null) {
            throw new Exception("A new action cannot already have an ID");
        }
        try {
            Action toCreateAction = modelMapper.map(actionDto, Action.class);
            Action result = actionService.saveAction(toCreateAction);

            // Update each category with the newly saved action
            for (CategoryDto categoryDto : actionDto.getCategories()) {
                Category category = modelMapper.map(categoryDto, Category.class);
                category.setAction(result);
                categoryService.partialUpdateCategory(category);
            }

            CustomResponse customResponse = new CustomResponse(result, HttpStatus.CREATED.value(), "action saved successfully");
            clearCache();
            return ResponseEntity.status(HttpStatus.CREATED).body(customResponse);
        }catch (Exception e) {
            log.error("Error saving action", e);
            CustomResponse customResponse = new CustomResponse(actionDto, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error saving action");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(customResponse);
        }
    }

    /**
     * {@code PUT  /actions/:actionId} : Updates an existing action.
     *
     * @param actionId the id of the action to save.
     * @param action the action to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated action,
     * or with status {@code 400 (Bad Request)} if the action is not valid,
     * or with status {@code 500 (Internal Server Error)} if the action couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{actionId}")
    public ResponseEntity<ActionDto> updateAction(
        @PathVariable(value = "actionId", required = false) final String actionId,
        @Validated @RequestBody Action action
    ) throws URISyntaxException {
        log.debug("REST request to update action : {}, {}", actionId, action);
        if (action.getActionId() == null) {
            throw new BadRequestException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(actionId, action.getActionId())) {
            throw new BadRequestException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!actionRepository.existsById(actionId)) {
            throw new BadRequestException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ActionDto result = actionService.updateAction(action);
        return  ResponseEntity
                .ok()
                .body(result);
    }

    /**
     * {@code PATCH  /actions/:actionId} : Partial updates given fields of an existing action, field will ignore if it is null
     *
     * @param actionId the id of the action to save.
     * @param action   the action to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated action,
     * or with status {@code 400 (Bad Request)} if the action is not valid,
     * or with status {@code 404 (Not Found)} if the action is not found,
     * or with status {@code 500 (Internal Server Error)} if the action couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */

    @PatchMapping(value = "/{actionId}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Optional<Action>> partialUpdateAction(
        @PathVariable(value = "actionId", required = false) final String actionId,
        @NonNull
        @RequestBody Action action
    ) throws URISyntaxException {
        log.debug("REST request to partial update action partially : {}, {}", actionId, action);
        if (action.getActionId() == null) {
            throw new BadRequestException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(actionId, action.getActionId())) {
            throw new BadRequestException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!actionRepository.existsById(actionId)) {
            throw new BadRequestException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Action> result = actionService.partialUpdateAction(action);

        return ResponseEntity.ok(Optional.of(result.get()));
    }

    /**
     * {@code GET  /actions} : get all the actions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of actions in body.
     */
    @GetMapping("")
    //@Cacheable(value = "actions", key = "'actions'")
    public List<ActionDto> getAllActions() {
        log.debug("REST request to get all actions");
        List<Action> actions = actionService.findAllActions();
        System.out.println(actions.toString());
        return actions.stream()
                .map(action -> modelMapper.map(action, ActionDto.class))
                .collect(Collectors.toList());


    }

    /**
     * {@code GET  /actions/:id} : get the "id" action.
     *
     * @param id the id of the action to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the action, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ActionDto> getAction(@PathVariable("id") String id) {
        log.debug("REST request to get action : {}", id);
        Optional<ActionDto> action = actionService.findOneAction(id);
        if (action.isPresent()) {
            log.debug("action found: {}", action.get());
            return ResponseEntity.ok(action.get());
        } else {
            log.debug("action not found for id: {}", action);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * {@code DELETE  /actions/:id} : delete the "id" action.
     *
     * @param id the id of the action to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAction(@PathVariable("id") String id) {
        log.debug("REST request to delete action : {}", id);
        clearCache();
        actionService.deleteAction(id);
        clearCache();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/clear_cache")
    @CacheEvict(value = "actions", allEntries = true )
    public String clearCache(){
        return "Cache has been cleared";
    }
}
