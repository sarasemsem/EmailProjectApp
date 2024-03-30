package com.emailProcessor.emailProcessor.controller;

import com.emailProcessor.basedomains.dto.CustomResponse;
import com.emailProcessor.basedomains.dto.SenderDto;
import com.emailProcessor.emailProcessor.controller.errors.BadRequestException;
import com.emailProcessor.emailProcessor.entity.Sender;
import com.emailProcessor.emailProcessor.repository.SenderRepository;
import com.emailProcessor.emailProcessor.service.SenderService;
import com.mongodb.lang.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
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
 * REST controller for managing
 */
@RestController
@RequestMapping("/api/v1/senders")
public class SenderController {

    private final Logger log = LoggerFactory.getLogger(SenderController.class);
    private static final String ENTITY_NAME = "sender";
    private final SenderService senderService;
    private final SenderRepository senderRepository;

    public SenderController(SenderService senderService, SenderRepository senderRepository) {
        this.senderService = senderService;
        this.senderRepository = senderRepository;
    }

    /**
     * {@code POST  /senders} : Create a new sender.
     *
     * @param senderDto the sender to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sender, or with status {@code 400 (Bad Request)} if the sender has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CustomResponse> createSender(@Validated @RequestBody SenderDto senderDto) {
        log.debug("REST request to save Sender : {}", senderDto);

            Sender result = senderService.saveSender(senderDto);
            CustomResponse customResponse = new CustomResponse(result, HttpStatus.CREATED.value(), "sender saved successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(customResponse);

    }

    /**
     * {@code PUT  /senders/:senderId} : Updates an existing sender.
     *
     * @param senderId the id of the sender to save.
     * @param senderDto the sender to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sender,
     * or with status {@code 400 (Bad Request)} if the sender is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sender couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{senderId}")
    public ResponseEntity<SenderDto> updateSender(
        @PathVariable(value = "senderId", required = false) final String senderId,
        @Validated @RequestBody SenderDto senderDto
    ) throws URISyntaxException {
        log.debug("REST request to update Sender : {}, {}", senderId, senderDto);
        if (senderDto.getSenderId() == null) {
            throw new BadRequestException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(senderId, senderDto.getSenderId())) {
            throw new BadRequestException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!senderRepository.existsById(senderId)) {
            throw new BadRequestException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SenderDto result = senderService.updateSender(senderDto);
        return ResponseEntity
            .ok()
            .body(result);
    }

    /**
     * {@code PATCH  /senders/:senderId} : Partial updates given fields of an existing sender, field will ignore if it is null
     *
     * @param senderId  the id of the sender to save.
     * @param sender the sender to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sender,
     * or with status {@code 400 (Bad Request)} if the sender is not valid,
     * or with status {@code 404 (Not Found)} if the sender is not found,
     * or with status {@code 500 (Internal Server Error)} if the sender couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */

    @PatchMapping(value = "/{senderId}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Optional<Sender>> partialUpdateSender(
        @PathVariable(value = "senderId", required = false) final String senderId,
        @NonNull
        @RequestBody Sender sender
    ) throws URISyntaxException {
        log.debug("REST request to partial update Sender partially : {}, {}", senderId, sender);
        System.out.println("REST request to partial update Sender partially :"+ senderId+ sender);
        sender.setSenderId(senderId);
        //Optional<Sender> result1 = senderService.partialUpdateSender(senderDto);
        if (sender.getSenderId() == null) {
            throw new BadRequestException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(senderId, sender.getSenderId())) {
            throw new BadRequestException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!senderRepository.existsById(senderId)) {
            throw new BadRequestException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Sender> result = senderService.partialUpdateSender(sender);

        return ResponseEntity.ok(Optional.of(result.get()));
    }

    /**
     * {@code GET  /senders} : get all the senders.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of senders in body.
     */
    @GetMapping("")
    public List<Sender> getAllSenders() {
        log.debug("REST request to get all Senders");
        return senderService.findAllSenders();
    }

    /**
     * {@code GET  /senders/:id} : get the "id" sender.
     *
     * @param senderId the id of the sender to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sender, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{senderId}")
    public ResponseEntity<Sender> getSender(@PathVariable String senderId) {
        log.debug("REST request to get Sender : {}", senderId);
        Optional<Sender> sender = senderService.findOneSender(senderId);

        if (sender.isPresent()) {
            log.debug("Sender found: {}", sender.get());
            return ResponseEntity.ok(sender.get());
        } else {
            log.debug("Sender not found for id: {}", senderId);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * {@code DELETE  /senders/:id} : delete the "id" sender.
     *
     * @param id the id of the sender to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSender(@PathVariable("id") String id) {
        log.debug("REST request to delete Sender : {}", id);
        clearCache();
        System.out.println("REST request to delete Sender : ");
         senderService.deleteSender(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

    @GetMapping("/clear_cache")
    @CacheEvict(value = "sender", allEntries = true )
    public String clearCache(){
        return "Cache has been cleared";
    }
}
