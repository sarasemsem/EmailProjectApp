package com.emailProcessor.emailProcessor.service.impl;

import com.emailProcessor.basedomains.dto.*;
import com.emailProcessor.emailProcessor.configuration.ActionParamEvent;
import com.emailProcessor.emailProcessor.entity.*;
import com.emailProcessor.emailProcessor.repository.ActionParamsRepository;
import com.emailProcessor.emailProcessor.repository.ActionRepository;
import com.emailProcessor.emailProcessor.repository.CategoryRepository;
import com.emailProcessor.emailProcessor.repository.EmailProcessingResultRepository;
import com.emailProcessor.emailProcessor.service.*;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class EmailProcessingResultImp implements EmailProcessingResultService {

    private final Logger log = LoggerFactory.getLogger(EmailProcessingResultImp.class);
    private final ModelMapper modelMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private KeywordService keywordService;
    @Autowired
    private ActionParamService actionParamService;
    @Autowired
    private ActionService actionService;
    @Autowired
    private EmailProcessingResultRepository emailProcessingResultRepository;
    private ActionParamsRepository actionParamsRepository;
    private final ActionRepository actionRepository;
    private final CategoryRepository categoryRepository;
    @Autowired
    private ActionParamsProducer actionParamsProducer;


    @Override
    public EmailProcessingResultDto saveNewEmailProcessingResult(EmailProcessingResultDto resultDto) throws Exception {
        log.debug("Request to save new result : {}", resultDto);
        EmailProcessingResult result = convertToEntity(resultDto);
        // Save the result entity
        EmailProcessingResult savedResult = emailProcessingResultRepository.save(result);

        return convertEmailProcessorToDto(savedResult);
    }



    @Override
    public EmailProcessingResultDto saveEmailProcessingResult(EmailProcessingResultDto resultDto) throws Exception {
        log.debug("Request to save the result : {}", resultDto);

        String categoryId = resultDto.getSelectedCategories().stream()
                .findFirst()
                .map(CategoryDto::getCategoryId)
                .orElseThrow(() -> new Exception("No selected category found"));

        Action action = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId))
                .getAction();
        if (action == null) {
            throw new Exception("No action found for category ID: " + categoryId);
        }

        // Convert List<String> to Map<String, String>
        List<String> params = resultDto.getParams() != null ? resultDto.getParams() : new ArrayList<>();
        Map<String, String> paramsMap = params.stream()
                .map(param -> param.split(":", 2)) // Split each string into key and value
                .filter(array -> array.length == 2) // Ensure only valid pairs are processed
                .collect(Collectors.toMap(
                        array -> array[0], // Key
                        array -> array[1], // Value
                        (existingValue, newValue) -> existingValue // Merge function to keep existing value in case of a duplicate key
                ));
        log.debug("params Map :" , paramsMap.size());

        ActionParamDto actionParamDto = new ActionParamDto();
        ActionDto actionDto = actionService.getActionDto(action);

        actionParamDto.setAction(actionDto);
        actionParamDto.setParams(paramsMap);
        actionParamDto.setActionDate(Instant.now());
        actionParamDto.setAffected(true);
        ActionParamDto actionParamDto1 = actionParamService.saveActionParam(actionParamDto);
        ActionParam actionParam = actionParamService.toActionParamEntity(actionParamDto1);

        if (actionParam.getAction().getState() && resultDto.getScore()>70) {
            // Produce the email to the Kafka topic
            ActionParamEvent actionParamEvent = new ActionParamEvent();
            actionParamEvent.setStatus("delivery");
            actionParamEvent.setMessage("new action coming-out");
            actionParamEvent.setActionParam(actionParam);

            // send to Kafka topic
            actionParamsProducer.sendMessage(actionParamEvent);
        }

            EmailProcessingResultDto toSaveResultDto = new EmailProcessingResultDto();
            toSaveResultDto.setSelectedCategories(resultDto.getSelectedCategories().stream().toList());
            toSaveResultDto.setScore(resultDto.getScore());
            toSaveResultDto.setRelatedActions(actionParamDto1);
            toSaveResultDto.setFoundKeywords(resultDto.getFoundKeywords());
            toSaveResultDto.setParams(resultDto.getParams());
            toSaveResultDto.setProposedCategories(resultDto.getProposedCategories());

        log.debug("Request to save the result :" , toSaveResultDto);
        EmailProcessingResult result = convertToEntity(toSaveResultDto);
        // Save the result entity
        EmailProcessingResult savedResult = emailProcessingResultRepository.save(result);

        return convertEmailProcessorToDto(savedResult);
    }

    @Override
    public EmailProcessingResult findById(String id) {
        EmailProcessingResult emailProcessingResult =
                emailProcessingResultRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException
                                ("Result not found with id: " + id));

        // If there are selected categories and no related actions
        if (emailProcessingResult.getSelectedCategories() != null && !emailProcessingResult.getSelectedCategories().isEmpty()) {
            if (emailProcessingResult.getRelatedActions() == null) {
                String actionsId = emailProcessingResult.getSelectedCategories().stream()
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("No selected category found"))
                        .getAction().getActionId();
                try {
                    Action action = actionRepository.findByActionId(actionsId);
                    if (action != null) {
                        ActionParam actionParam = new ActionParam();
                        actionParam.setAction(action);
                        ActionParam savedActionParam = actionParamsRepository.save(actionParam);
                        log.debug("saved param : ",savedActionParam.getActionParamId());

                        emailProcessingResult.setRelatedActions(savedActionParam);
                        partialUpdate(emailProcessingResult);
                    }
                } catch (ResourceNotFoundException exception) {
                    throw new ResourceNotFoundException("Action not found with id: " + actionsId, exception);
                }
            }
        }

        // Fetch related actions if not null
        if (emailProcessingResult.getRelatedActions() != null) {
            ActionParam actionParam = actionParamsRepository.
                    findById(emailProcessingResult.getRelatedActions().getActionParamId())
                    .orElseThrow(() -> new ResourceNotFoundException
                            ("ActionParam not found with id: " + emailProcessingResult.getRelatedActions().getActionParamId()));
          //  emailProcessingResult.setRelatedActions(actionParam);
            log.debug("parameters: " , actionParam.getAction());}
        log.debug("result actions: " , emailProcessingResult.getId());
        return emailProcessingResult;
    }

    @Override
    public Optional<EmailProcessingResult> partialUpdate(EmailProcessingResult processingResult) {
        log.debug("Request to partially update Result : {}", processingResult);
        if (processingResult.getRelatedActions() != null) {
            String id = processingResult.getRelatedActions().getActionParamId();
            actionService.deleteAction(id);
        }
        return emailProcessingResultRepository
                .findById(processingResult.getId())
                .map(result -> {
                    result.setScore(processingResult.getScore());
                    result.setRelatedActions(processingResult.getRelatedActions());
                    return processingResult;
                })
                .map(emailProcessingResultRepository::save);
    }

    @Override
    public EmailProcessingResultDto convertEmailProcessorToDto(EmailProcessingResult processingResult) {
        EmailProcessingResultDto dto = new EmailProcessingResultDto();

        if (processingResult.getId() != null) {
            dto.setId(processingResult.getId());
        }
        if (processingResult.getProposedCategories() != null) {
            List<CategoryDto> proposedCategoryDtos = processingResult.getProposedCategories().stream()
                    .map(categoryService::convertCategoryToDto)
                    .collect(Collectors.toList());
            dto.setProposedCategories(proposedCategoryDtos);
        }
        if (processingResult.getSelectedCategories() != null) {
            List<CategoryDto> selectedCategoryDtos = processingResult.getSelectedCategories().stream()
                    .map(categoryService::convertCategoryToDto)
                    .collect(Collectors.toList());
            dto.setSelectedCategories(selectedCategoryDtos);
        }
        if (processingResult.getFoundKeywords() != null) {
            List<KeywordDto> foundKeywords = processingResult.getFoundKeywords().stream()
                    .map(keywordService::convertKeywordToDto)
                    .collect(Collectors.toList());
            dto.setFoundKeywords(foundKeywords);
        }
        if (processingResult.getScore() != null) {
            dto.setScore(processingResult.getScore());
        }
        if (processingResult.getRelatedActions() != null) {
            ActionParamDto actionParamList = actionParamService.toActionParamDto(processingResult.getRelatedActions());
            System.out.println("this is get action result :"+actionParamList.toString());
            dto.setRelatedActions(actionParamList);
        }
        return dto;
    }
    @Override
    public EmailProcessingResult convertToEntity(EmailProcessingResultDto dto) {
        EmailProcessingResult processingResult = new EmailProcessingResult();

        if (dto.getId() != null) {
            processingResult.setId(dto.getId());
        }
        if (dto.getProposedCategories() != null) {
            List<Category> proposedCategories = dto.getProposedCategories().stream()
                    .map(categoryDto -> modelMapper.map(categoryDto, Category.class))
                    .collect(Collectors.toList());
            processingResult.setProposedCategories(proposedCategories);
        }
        if (dto.getSelectedCategories() != null) {
            List<Category> selectedCategories = dto.getSelectedCategories().stream()
                    .map(categoryDto -> modelMapper.map(categoryDto, Category.class))
                    .collect(Collectors.toList());
            processingResult.setSelectedCategories(selectedCategories);
        }
        if (dto.getFoundKeywords() != null) {
            List<Keyword> foundKeywords = dto.getFoundKeywords().stream()
                    .map(keywordDto -> modelMapper.map(keywordDto, Keyword.class))
                    .collect(Collectors.toList());
            processingResult.setFoundKeywords(foundKeywords);
        }
        if (dto.getScore() != null) {
            processingResult.setScore(dto.getScore());
        }
        if (dto.getRelatedActions() != null) {
            ActionParam relatedActions = actionParamService.toActionParamEntity(dto.getRelatedActions());
            processingResult.setRelatedActions(relatedActions);
        }
        return processingResult;
    }

    @Override
    public ResponseEntity<String> deleteEmailProcessingResult(String id) {
        log.debug("Request to delete Keyword : {}", id);
        try {
            Optional<EmailProcessingResult> processingResult = emailProcessingResultRepository.findById(id);
            if (processingResult.isPresent()) {
                EmailProcessingResult result = processingResult.get();
                Optional<ActionParam> actionParam = actionParamsRepository.findById(result.getRelatedActions().getActionParamId());
                actionParam.ifPresent(param -> actionParamsRepository.delete(param));
                // Delete the processingResult
                emailProcessingResultRepository.deleteById(id);
                return ResponseEntity.noContent().build(); // 204 No Content
            } else {
                return ResponseEntity.notFound().build(); // 404 Not Found
            }
        } catch (Exception e) {
            log.error("Error occurred while deleting processingResult: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete the resource"); // 500 Internal Server Error
        }
    }
}
