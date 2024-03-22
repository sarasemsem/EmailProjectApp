package com.emailProcessor.basedomains.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailProcessingResultDto {
    private String id;
    private List<CategoryDto> proposedCategories;
    private List<CategoryDto> selectedCategories;
    private List<KeywordDto> foundKeywords;
}
