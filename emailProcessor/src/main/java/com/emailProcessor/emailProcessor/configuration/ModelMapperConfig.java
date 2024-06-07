package com.emailProcessor.emailProcessor.configuration;

import com.emailProcessor.basedomains.dto.ActionDto;
import com.emailProcessor.basedomains.dto.ActionParamDto;
import com.emailProcessor.emailProcessor.entity.Action;
import com.emailProcessor.emailProcessor.entity.ActionParam;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean(name = "customModelMapper")
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Configure mapping for Action to ActionDto
        modelMapper.createTypeMap(Action.class, ActionDto.class);

        // Configure mapping for ActionParam to ActionParamDto
        TypeMap<ActionParam, ActionParamDto> actionParamTypeMap = modelMapper.createTypeMap(ActionParam.class, ActionParamDto.class);
        actionParamTypeMap.addMappings(mapper -> mapper.map(ActionParam::getAction, ActionParamDto::setAction));
        actionParamTypeMap.addMappings(mapper -> mapper.map(ActionParam::getParams, ActionParamDto::setParams));

        return modelMapper;
    }
}