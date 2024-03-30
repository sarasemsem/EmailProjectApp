package com.emailProcessor.emailProcessor.service;
import com.emailProcessor.basedomains.dto.CredentialsDto;
import com.emailProcessor.basedomains.dto.Worker1Dto;
import com.emailProcessor.basedomains.dto.WorkerDto;
import com.emailProcessor.emailProcessor.entity.Worker;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing
 */
@Service
public interface WorkerService {

    WorkerDto login(CredentialsDto credentialsDto);
    /**
     * Save a worker.
     *
     * @param worker the entity to save.
     * @return the persisted entity.
     */
    ResponseEntity<String> saveWorker(Worker1Dto worker);

    /**
     * Updates a worker.
     *
     * @param worker the entity to update.
     * @return the persisted entity.
     */
    WorkerDto updateWorker(Worker worker);

    /**
     * Partially updates a worker.
     *
     * @param worker the entity to update partially.
     * @return the persisted entity.
     */
    Optional<WorkerDto> partialUpdateWorker(Worker worker);

    /**
     * Get all the workers.
     *
     * @return the list of entities.
     */
    List<Worker> findAllWorkers();

    /**
     * Get the "id" worker.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<WorkerDto> findOneWorker(String id);

    /**
     * Delete the "id" worker.
     *
     * @param id the id of the entity.
     */
    void deleteWorker(String id);

    WorkerDto findByLogin(String login);
}
