package com.emailProcessor.emailProcessor.service;

import com.emailProcessor.basedomains.dto.ActionDto;
import com.emailProcessor.basedomains.dto.EmailProcessingResultDto;
import com.emailProcessor.basedomains.dto.KeywordDto;
import com.emailProcessor.basedomains.dto.RelatedDataDto;
import com.emailProcessor.emailProcessor.entity.Keyword;
import edu.stanford.nlp.ling.CoreLabel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface EmailClassification {
    EmailProcessingResultDto getClassificationResult(List<CoreLabel> coreLabelList);
    RelatedDataDto getRelatedData(List<CoreLabel> coreLabelList);
    Map<String, String> findParams(ActionDto action, List<CoreLabel> coreLabelList);
}
