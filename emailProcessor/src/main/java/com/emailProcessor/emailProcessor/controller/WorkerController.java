package com.emailProcessor.emailProcessor.controller;
import com.emailProcessor.basedomains.dto.Worker1Dto;
import com.emailProcessor.basedomains.dto.WorkerDto;
import com.emailProcessor.emailProcessor.controller.errors.BadRequestException;
import com.emailProcessor.emailProcessor.entity.Worker;
import com.emailProcessor.emailProcessor.repository.WorkerRepository;
import com.emailProcessor.emailProcessor.service.WorkerService;
import com.mongodb.lang.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link com.emailProcessor.emailProcessor.entity.Worker}.
 */
@RestController
@RequestMapping("/api/v1/workers")
public class WorkerController {

    private final Logger log = LoggerFactory.getLogger(WorkerController.class);

    private static final String ENTITY_NAME = "worker";
    private final WorkerService workerService;

    private final WorkerRepository workerRepository;

    public WorkerController(WorkerService workerService, WorkerRepository workerRepository) {
        this.workerService = workerService;
        this.workerRepository = workerRepository;
    }

    /**
     * {@code POST  /workers} : Create a new worker.
     *
     * @param workerDto the worker to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new worker, or with status {@code 400 (Bad Request)} if the worker has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<String> createWorker(@Validated @RequestBody Worker1Dto workerDto) throws URISyntaxException {
        log.debug("REST request to save Worker : {}", workerDto);

        try {
            ResponseEntity<String> result = workerService.saveWorker(workerDto);
            return result;
        } catch (BadRequestException e) {
            log.error("Bad request alert exception", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error saving user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving worker");
        }
    }

    /**
     * {@code PUT  /workers/:workerId} : Updates an existing worker.
     *
     * @param workerId the id of the worker to save.
     * @param worker the worker to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated worker,
     * or with status {@code 400 (Bad Request)} if the worker is not valid,
     * or with status {@code 500 (Internal Server Error)} if the worker couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{workerId}")
    public ResponseEntity<WorkerDto> updateWorker(
        @PathVariable(value = "workerId", required = false) final String workerId,
        @Validated @RequestBody Worker worker
    ) throws URISyntaxException {
        log.debug("REST request to update Worker : {}, {}", workerId, worker);
        if (worker.getWorkerId() == null) {
            throw new BadRequestException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(workerId, worker.getWorkerId())) {
            throw new BadRequestException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!workerRepository.existsById(workerId)) {
            throw new BadRequestException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        WorkerDto result = workerService.updateWorker(worker);
        return ResponseEntity
                .ok()
                .body(result);
    }

    /**
     * {@code PATCH  /workers/:workerId} : Partial updates given fields of an existing worker, field will ignore if it is null
     *
     * @param workerId the id of the worker to save.
     * @param worker the worker to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated worker,
     * or with status {@code 400 (Bad Request)} if the worker is not valid,
     * or with status {@code 404 (Not Found)} if the worker is not found,
     * or with status {@code 500 (Internal Server Error)} if the worker couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{workerId}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Optional<WorkerDto>> partialUpdateWorker(
        @PathVariable(value = "workerId", required = false) final String workerId,
        @NonNull @RequestBody Worker worker
    ) throws URISyntaxException {
        log.debug("REST request to partial update Worker partially : {}, {}", workerId, worker);
        if (worker.getWorkerId() == null) {
            throw new BadRequestException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(workerId, worker.getWorkerId())) {
            throw new BadRequestException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!workerRepository.existsById(workerId)) {
            throw new BadRequestException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<WorkerDto> result = workerService.partialUpdateWorker(worker);

        return ResponseEntity.ok(Optional.of(result.get()));
    }

    /**
     * {@code GET  /workers} : get all the workers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of workers in body.
     */
    @GetMapping("")
    @Cacheable(value = "workers", key = "'workers'")
    public List<Worker> getAllWorkers() {
        log.debug("REST request to get all Workers");
        return workerService.findAllWorkers();
    }

    /**
     * {@code GET  /workers/:id} : get the "id" worker.
     *
     * @param id the id of the worker to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the worker, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<WorkerDto> getWorker(@PathVariable("id") String id) {
        log.debug("REST request to get Worker : {}", id);
        Optional<WorkerDto> workerOptional = workerService.findOneWorker(id);
        return workerOptional
                .map(worker -> ResponseEntity.ok().body(worker))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * {@code DELETE  /workers/:id} : delete the "id" worker.
     *
     * @param id the id of the worker to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteWorker(@PathVariable("id") String id) {
        log.debug("REST request to delete Worker : {}", id);
        workerService.deleteWorker(id);
        return ResponseEntity.status(HttpStatus.OK).body("deleted worker");
    }

    @GetMapping("/clear_cache")
    @CacheEvict(value = "workers", allEntries = true )
    public String clearCache(){
        return "Cache has been cleared";
    }
}
