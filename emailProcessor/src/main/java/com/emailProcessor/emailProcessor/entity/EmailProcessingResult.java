package com.emailProcessor.emailProcessor.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "email_processing_results")
public class EmailProcessingResult {
    @MongoId
    private String id;
    @DBRef
    private List<Category> proposedCategories;
    @DBRef
    private List<Category> selectedCategories;
    @DBRef
    private List<Keyword> foundKeywords;
}
