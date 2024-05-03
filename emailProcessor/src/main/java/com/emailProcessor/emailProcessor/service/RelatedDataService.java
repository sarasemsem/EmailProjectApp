package com.emailProcessor.emailProcessor.service;
import com.emailProcessor.basedomains.dto.RelatedDataDto;
import com.emailProcessor.emailProcessor.entity.RelatedData;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface RelatedDataService {
    RelatedDataDto saveRelatedData(RelatedDataDto relatedDataDto);
    RelatedDataDto updateRelatedData(RelatedData relatedData);
    Optional<RelatedData> partialUpdateRelatedData(RelatedData relatedData);
    List<RelatedData> findAllRelatedData();
    Optional<RelatedDataDto> findOneRelatedData(String id);
    void deleteRelatedData(String id);
}
