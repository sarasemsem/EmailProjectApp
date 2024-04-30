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
    public Action saveAction(Action action) {
        log.debug("Request to save Action : {}", action);
        return actionRepository.save(action);
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
                    if (action.getAction() != null) {
                        existingAction.setAction(action.getAction());
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
                    if (action.getUpdatedBy() != null) {
                        existingAction.setUpdatedBy(action.getUpdatedBy());
                    }
                    if (action.getState() != null) {
                        existingAction.setState(action.getState());
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
