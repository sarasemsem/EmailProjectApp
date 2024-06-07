package com.emailProcessor.basedomains.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailProcessingResultDto implements Serializable {
    private String id;
    private List<CategoryDto> proposedCategories;
    private List<CategoryDto> selectedCategories;
    private List<KeywordDto> foundKeywords;
    private Double score ;
    //private List<ActionDto> action;
    private List<ActionParamDto> relatedActions;
}
