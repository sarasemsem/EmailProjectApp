package com.emailProcessor.emailProcessor.service.impl;

import com.emailProcessor.basedomains.dto.ActionDto;
import com.emailProcessor.basedomains.dto.ActionParamDto;
import com.emailProcessor.emailProcessor.entity.Action;
import com.emailProcessor.emailProcessor.entity.ActionParam;
import com.emailProcessor.emailProcessor.repository.ActionParamsRepository;
import com.emailProcessor.emailProcessor.service.ActionParamService;
import edu.stanford.nlp.ling.CoreLabel;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ActionParamServiceImp implements ActionParamService {
    private final Logger log = LoggerFactory.getLogger(ActionParamServiceImp.class);
    private final ModelMapper modelMapper;
    private final ActionParamsRepository actionParamsRepository;
    private final ActionServiceImp actionService ;


    @Override
    public ActionParamDto toActionParamDto(ActionParam actionParam) {
        ActionParamDto actionParamDto = new ActionParamDto();

        if (actionParam.getActionParamId() != null) {
            actionParamDto.setActionParamId(actionParam.getActionParamId());
        }
        if (actionParam.getAction() != null) {
            actionParamDto.setAction(actionService.getActionDto(actionParam.getAction()));
        }
        if (actionParam.getParams() != null) {
            actionParamDto.setParams(actionParam.getParams());
        }
        if (actionParam.getActionDate() != null) {
            log.info("action param dto are : " + actionParam.getActionDate());
            actionParamDto.setActionDate(Instant.now());
        }
        if (actionParam.getAffected() != null) {
            log.info("action param dto are : " + actionParam.getAffected());
            actionParamDto.setAffected(actionParam.getAffected());
        }

        return actionParamDto;
    }

    @Override
    public ActionParam toActionParamEntity(ActionParamDto actionParamDto) {
        log.info("convert actionParam to dto");

        ActionParam actionParam = new ActionParam();
        if (actionParamDto.getActionParamId() != null) {
            actionParam.setActionParamId(actionParamDto.getActionParamId());
        }
        if (actionParamDto.getAction() != null) {
            System.out.println("action is  :" +actionParamDto.getAction());
            Action action = actionService.ConvertActionDtoToEntity(actionParamDto.getAction());
            actionParam.setAction(action);
        }
        if (actionParamDto.getParams() != null) {
            System.out.println("param are : "+actionParamDto.getParams());
            actionParam.setParams(actionParamDto.getParams());
        }
        if (actionParamDto.getActionDate() != null) {
            System.out.println("param are : " + actionParamDto.getActionDate());
            actionParam.setActionDate(Instant.now());
        }
        if (actionParamDto.getAffected() != null) {
            System.out.println("param are : " + actionParamDto.getAffected());
            actionParam.setAffected(actionParamDto.getAffected());
        }
        return actionParam;
    }
    @Override
    public ActionParamDto saveActionParam(ActionParamDto actionParam) {
        log.debug("Request to save Action : {}", actionParam);
        ActionParam actionParam1 = toActionParamEntity(actionParam);
        ActionParam param = actionParamsRepository.save(actionParam1);
        return toActionParamDto(param);
    }

    @Override
    public ActionParamDto saveAllActionParam(ActionParamDto actionParams) {
        log.debug("Request to save ActionParams : {}", actionParams);

        // Map each ActionParamDto to ActionParam
        ActionParam actionParamList = toActionParamEntity(actionParams);

        // Save all ActionParams
        ActionParam savedActionParams = actionParamsRepository.save(actionParamList);

        // Map the saved ActionParam entities back to ActionParamDto

        return toActionParamDto(savedActionParams);
    }

    @Override
    public ActionParamDto updateActionParam(ActionParam actionParam) {
        log.debug("Request to update Action : {}", actionParam);
        ActionParam updatedActionParam1 = actionParamsRepository.save(actionParam);
        return modelMapper.map(updatedActionParam1, ActionParamDto.class);
    }

    @Override
    public Optional<ActionParam> partialUpdateActionParam(ActionParam actionParam) {
        return Optional.empty();
    }

    @Override
    public List<ActionParam> findAllActionParams() {
        log.debug("Request to get all Actions");
        return actionParamsRepository.findAll();
    }

    @Override
    public Optional<ActionParamDto> findOneActionParam(String id) {
        log.debug("Request to get Action : {}", id);
        Optional<ActionParam> actionParam = actionParamsRepository.findById(id);
        return Optional.of(toActionParamDto(actionParam.get()));
    }

    @Override
    public void deleteActionParam(String id) {
        log.debug("Request to delete Action : {}", id);
        actionParamsRepository.deleteById(id);
    }

    public Map<String, String> findParams(ActionDto action, List<CoreLabel> coreLabelList) {
        Map<String, String> params = null;
        if (action != null && action.getParams() != null) {
            for (String param : action.getParams()) {
                for (CoreLabel coreLabel : coreLabelList) {
                    String word = coreLabel.lemma();
                    if (word.matches("[a-zA-Z0-9]{2,}")) {
                        if (word.toLowerCase().equals(param.toLowerCase())) {
                            params.put(param, coreLabelList.get(coreLabel.index() + 1).word());
                        }
                    }
                }
            }
        }
        return params;
    }
}