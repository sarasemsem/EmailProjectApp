package com.emailProcessor.emailProcessor.service;

import com.emailProcessor.basedomains.dto.CategoryDto;
import com.emailProcessor.basedomains.dto.EmailDto;
import com.emailProcessor.basedomains.dto.EmailProcessingResultDto;
import com.emailProcessor.basedomains.dto.KeywordDto;
import com.emailProcessor.emailProcessor.configuration.Pipeline;
import com.emailProcessor.emailProcessor.entity.Category;
import com.emailProcessor.emailProcessor.entity.EmailProcessingResult;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NLPService {
    @Autowired
    private EmailService emailService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private EmailProcessingResultService emailProcessingResultService;
    @Autowired
    private KeywordService keywordService;

    public void treatment() {
        try {
            List<EmailDto> emails = emailService.getAllUntreatedEmails();
            emails.forEach(this::treatEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void treatEmail(EmailDto email) {
        try {
            StanfordCoreNLP stanfordCoreNLP = Pipeline.getPipeline();
            String emailContent = email.getContent();
            CoreDocument coreDocument = new CoreDocument(emailContent);
            stanfordCoreNLP.annotate(coreDocument);

            // Extracting individual words and processing them
            List<CoreLabel> coreLabelList = coreDocument.tokens();
            Map<String, Double> categoryScores = new HashMap<>();
            List<KeywordDto> foundKeywords = new ArrayList<>();

            // Iterate through coreLabelList and extract category IDs
            for (CoreLabel coreLabel : coreLabelList) {
                String word = coreLabel.lemma();
                if (word.matches("[a-zA-Z0-9]{2,}")) {
                    Optional<KeywordDto> keyword = keywordService.findKeywordByWord(word);
                    keyword.ifPresent(k -> {
                        foundKeywords.add(k);
                        if (k.getCategories() != null) {
                            for (CategoryDto category : k.getCategories()) {
                                double weight = k.getWeight();
                                categoryScores.put(category.getCategoryId(), categoryScores.getOrDefault(category.getCategoryId(), 0.0) + weight);
                            }
                        }
                    });
                }
            }

            if (!categoryScores.isEmpty()) {
                // Normalize the scores to percentages
                double maxPossibleScore = foundKeywords.size(); // Assuming each keyword has a maximum weight of 1
                Map<String, Double> normalizedScores = new HashMap<>();
                for (Map.Entry<String, Double> entry : categoryScores.entrySet()) {
                    double normalizedScore = (entry.getValue() / maxPossibleScore) * 100.0;
                    normalizedScores.put(entry.getKey(), normalizedScore);
                }

                // Filter out categories with normalized scores below 50%
                Map<String, Double> filteredScores = normalizedScores.entrySet().stream()
                        .filter(entry -> entry.getValue() >= 50.0)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                // Sort the filtered scores in descending order
                List<Map.Entry<String, Double>> sortedFilteredScores = new ArrayList<>(filteredScores.entrySet());
                sortedFilteredScores.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

                // Select top categories based on filtered scores
                List<CategoryDto> proposedCategories = new ArrayList<>();
                List<CategoryDto> selectedCategories = new ArrayList<>();
                int count = 0;
                for (Map.Entry<String, Double> entry : sortedFilteredScores) {
                    if (count < 2) {
                        String categoryId = entry.getKey();
                        Optional<CategoryDto> category = categoryService.findOneCategory(categoryId);
                        category.ifPresent(selectedCategories::add);
                        count++;
                    }
                    // Break after adding the top 2 categories
                    if (count >= 2) {
                        break;
                    }
                }

                // Create EmailProcessingResult object and set categories and keywords
                EmailProcessingResultDto emailProcessingResult = new EmailProcessingResultDto();
                emailProcessingResult.setProposedCategories(proposedCategories);
                emailProcessingResult.setSelectedCategories(selectedCategories);
                emailProcessingResult.setFoundKeywords(foundKeywords);

                // Save the EmailProcessingResult to MongoDB
                EmailProcessingResultDto savedEmailProcessingResult = emailProcessingResultService.saveEmailProcessingResult(emailProcessingResult);

                // Update email with treated flag
                email.setResult(savedEmailProcessingResult);
                email.setTreated(true);
                emailService.partialUpdate(email);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
