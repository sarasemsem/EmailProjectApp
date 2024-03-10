package com.emailProcessor.emailProcessor.service;
import com.emailProcessor.basedomains.dto.SenderDto;
import com.emailProcessor.emailProcessor.entity.Sender;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing
 */
@Service
public interface SenderService {
    /**
     * Save a sender.
     *
     * @param senderDto the entity to save.
     * @return the persisted entity.
     */
    ResponseEntity<String> saveSender(SenderDto senderDto);

    /**
     * Updates a sender.
     *
     * @param senderDto the entity to update.
     * @return the persisted entity.
     */
    SenderDto updateSender(SenderDto senderDto);

    /**
     * Partially updates a sender.
     *
     * @param sender the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Sender> partialUpdateSender(Sender sender);

    /**
     * Get all the senders.
     *
     * @return the list of entities.
     */
    List<Sender> findAllSenders();

    /**
     * Get the "id" sender.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Sender> findOneSender(String id);

    /**
     * Delete the "id" sender.
     *
     * @param id the id of the entity.
     */
    ResponseEntity<Void> deleteSender(String id);
}
