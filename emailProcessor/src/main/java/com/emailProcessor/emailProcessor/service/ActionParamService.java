package com.emailProcessor.emailProcessor.service;
import com.emailProcessor.basedomains.dto.ActionDto;
import com.emailProcessor.basedomains.dto.ActionParamDto;
import com.emailProcessor.emailProcessor.entity.ActionParam;
import edu.stanford.nlp.ling.CoreLabel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
@Service
public interface ActionParamService {
    ActionParamDto toActionParamDto(ActionParam actionParam);
    ActionParam toActionParamEntity(ActionParamDto actionParamDto);
    ActionParamDto saveActionParam(ActionParamDto actionParamDto);
    public ActionParamDto saveAllActionParam(ActionParamDto actionParams);
    ActionParamDto updateActionParam(ActionParam actionParam);
    Optional<ActionParam> partialUpdateActionParam(ActionParam actionParam);
    List<ActionParam> findAllActionParams();
    Optional<ActionParamDto> findOneActionParam(String id);
    void deleteActionParam(String id);
    Map<String, String> findParams(ActionDto action, List<CoreLabel> coreLabelList) ;
}
