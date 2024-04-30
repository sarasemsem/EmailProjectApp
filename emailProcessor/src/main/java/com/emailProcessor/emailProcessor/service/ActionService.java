package com.emailProcessor.emailProcessor.service;
import com.emailProcessor.basedomains.dto.ActionDto;
import com.emailProcessor.emailProcessor.entity.Action;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public interface ActionService {
    Action saveAction(Action action);
    ActionDto updateAction(Action action);
    Optional<Action> partialUpdateAction(Action action);
    List<Action> findAllActions();
    Optional<ActionDto> findOneAction(String id);
    void deleteAction(String id);
}
