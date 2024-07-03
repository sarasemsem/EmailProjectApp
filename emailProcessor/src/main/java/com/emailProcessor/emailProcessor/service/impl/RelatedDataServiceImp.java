package com.emailProcessor.emailProcessor.service.impl;

import com.emailProcessor.basedomains.dto.RelatedDataDto;
import com.emailProcessor.emailProcessor.entity.RelatedData;
import com.emailProcessor.emailProcessor.repository.RelatedDataRepository;
import com.emailProcessor.emailProcessor.service.RelatedDataService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RelatedDataServiceImp implements RelatedDataService {
    private final Logger log = LoggerFactory.getLogger(RelatedDataServiceImp.class);
    private final ModelMapper modelMapper;
    private final RelatedDataRepository relatedDataRepository;
    @Override
    public RelatedDataDto saveRelatedData(RelatedDataDto relatedDataDto) {
        Assert.notNull(relatedDataDto, "Source cannot be null");
        log.debug("Request to save relatedData : {}", relatedDataDto);
        RelatedData relatedData= modelMapper.map(relatedDataDto, RelatedData.class);
        RelatedData relatedDataDto1= relatedDataRepository.save(relatedData);
        return modelMapper.map(relatedDataDto1, RelatedDataDto.class);
    }

    @Override
    public RelatedDataDto updateRelatedData(RelatedData relatedData) {
        log.debug("Request to update relatedData : {}", relatedData);
        RelatedData updatedRelatedData= relatedDataRepository.save(relatedData);
        return modelMapper.map(updatedRelatedData, RelatedDataDto.class);
    }

    @Override
    public Optional<RelatedData> partialUpdateRelatedData(RelatedData relatedData) {
        log.debug("Request to partially update relatedData : {}", relatedData);
        return relatedDataRepository
                .findById(relatedData.getRelatedDataId())
                .map(existingAction -> {
                    if (relatedData.getAccount_type() != null) {
                        existingAction.setAccount_type(relatedData.getAccount_type());
                    }
                    if (relatedData.getAccount_number() != null) {
                        existingAction.setAccount_number(relatedData.getAccount_number());
                    }
                    if (relatedData.getAmount() >0 ) {
                        existingAction.setAmount(relatedData.getAmount());
                    }
                    if (relatedData.getRelatedDataId() != null) {
                        existingAction.setRelatedDataId(relatedData.getRelatedDataId());
                    }
                    if (relatedData.getPeriod() != null) {
                        existingAction.setPeriod(relatedData.getPeriod());
                    }
                    if (relatedData.getCurrency() != null) {
                        existingAction.setCurrency(relatedData.getCurrency());
                    }
                    if (relatedData.getRecipient_account() != null) {
                        existingAction.setRecipient_account(relatedData.getRecipient_account());
                    }

                    return existingAction;
                })
                .map(relatedDataRepository::save);
    }

    @Override
    public List<RelatedData> findAllRelatedData() {
        log.debug("Request to get all relatedDatas");
        return relatedDataRepository.findAll();
    }

    @Override
    public Optional<RelatedDataDto> findOneRelatedData(String id) {
        log.debug("Request to get relatedData : {}", id);
        Optional<RelatedData> relatedData= relatedDataRepository.findById(id);
        return Optional.of(modelMapper.map(relatedData, RelatedDataDto.class));
}

    @Override
    public void deleteRelatedData(String id) {
        log.debug("Request to delete relatedData : {}", id);
        relatedDataRepository.deleteById(id);
    }
}
