package com.emailProcessor.emailProcessor.service;
import com.emailProcessor.basedomains.dto.ActionDto;
import com.emailProcessor.emailProcessor.entity.Action;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ActionService {

    Action saveAction(Action action);
    ActionDto updateAction(Action action);
    Optional<Action> partialUpdateAction(Action action);
    List<Action> findAllActions();
    ActionDto findOneAction(String id);
    void deleteAction(String id);
    ActionDto getActionDto(Action action);
    Action ConvertActionDtoToEntity(ActionDto actionDto);

}
