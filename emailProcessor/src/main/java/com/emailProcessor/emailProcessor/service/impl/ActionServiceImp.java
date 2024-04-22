package com.emailProcessor.emailProcessor.service.impl;

import com.emailProcessor.basedomains.dto.ActionDto;
import com.emailProcessor.emailProcessor.entity.Action;
import com.emailProcessor.emailProcessor.repository.ActionRepository;
import com.emailProcessor.emailProcessor.service.ActionService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class ActionServiceImp implements ActionService {
    private final Logger log = LoggerFactory.getLogger(ActionServiceImp.class);
    private final ModelMapper modelMapper;
    private final ActionRepository actionRepository;
    @Override
    public ResponseEntity<String> saveAction(Action action) {
        log.debug("Request to save Action : {}", action);
        Action savedAction= actionRepository.save(action);
        if (savedAction.getActionId() != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Sender saved successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to insert the Sender");
        }
    }

    @Override
    public ActionDto updateAction(Action action) {
        log.debug("Request to update Action : {}", action);
        Action updatedAction= actionRepository.save(action);
        return modelMapper.map(updatedAction, ActionDto.class);
    }

    @Override
    public Optional<Action> partialUpdateAction(Action action) {
        log.debug("Request to partially update Action : {}", action);
        return actionRepository
                .findById(action.getActionId())
                .map(existingAction -> {
                    if (action.getType() != null) {
                        existingAction.setType(action.getType());
                    }
                    if (action.getDescriptionAct() != null) {
                        existingAction.setDescriptionAct(action.getDescriptionAct());
                    }
                    if (action.getActionDate() != null) {
                        existingAction.setActionDate(action.getActionDate());
                    }
                    if (action.getUpdatedAt() != null) {
                        existingAction.setUpdatedAt(action.getUpdatedAt());
                    }
                    if (action.getAffected() != null) {
                        existingAction.setAffected(action.getAffected());
                    }
                    if (action.getTreatedBy() != null) {
                        existingAction.setTreatedBy(action.getTreatedBy());
                    }

                    return existingAction;
                })
                .map(actionRepository::save);
    }

    @Override
    public List<Action> findAllActions() {
        log.debug("Request to get all Actions");
        return actionRepository.findAll();
    }

    @Override
    public List<Action> findAllActionsWhereLinkedCategoryIsNull() {
        log.debug("Request to get all actions where LinkedCategory is null");
        return StreamSupport
                .stream(actionRepository.findAll().spliterator(), false)
                .filter(action -> action.getLinkedCategory() == null)
                .toList();
    }

    @Override
    public Optional<ActionDto> findOneAction(String id) {
        log.debug("Request to get Action : {}", id);
        Optional<Action> action= actionRepository.findById(id);
        return Optional.of(modelMapper.map(action, ActionDto.class));
}

    @Override
    public void deleteAction(String id) {
        log.debug("Request to delete Action : {}", id);
        actionRepository.deleteById(id);
    }
}
