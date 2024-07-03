package com.emailProcessor.emailProcessor.entity;
import com.emailProcessor.basedomains.dto.ActionParamDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "email_processing_results")
public class EmailProcessingResult implements Serializable  {
    @MongoId
    private String id;
    @DBRef(lazy = true)
    private List<Category> proposedCategories;
    @DBRef(lazy = true)
    private List<Category> selectedCategories;
    @DBRef(lazy = true)
    private List<Keyword> foundKeywords;
    private Double score;
    @DBRef(lazy = true)
    private ActionParam relatedActions;

    @Override
    public String toString() {
        return "EmailProcessingResultDto{" +
                "id='" + id + '\'' +
                ", proposedCategories=" + proposedCategories +
                ", selectedCategories=" + selectedCategories +
                ", foundKeywords=" + foundKeywords +
                ", score=" + score +
                ", relatedActions=" + relatedActions +
                '}';
    }
}
