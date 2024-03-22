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
    private void treatEmail(EmailDto email) {
        try {
            StanfordCoreNLP stanfordCoreNLP = Pipeline.getPipeline();
            String emailContent = email.getContent();
            CoreDocument coreDocument = new CoreDocument(emailContent);
            stanfordCoreNLP.annotate(coreDocument);

            // Extracting individual words and processing them
            List<CoreLabel> coreLabelList = coreDocument.tokens();
            List<String> categoryIds = new ArrayList<>();
            List<KeywordDto> foundKeywords = new ArrayList<>();

            // Iterate through coreLabelList and extract category IDs
            for (CoreLabel coreLabel : coreLabelList) {
                String word = coreLabel.lemma();
                if (word.matches("[a-zA-Z0-9]{2,}")) {
                    Optional<KeywordDto> keyword = keywordService.findKeywordByWord(word);
                    keyword.ifPresent(k -> {
                                foundKeywords.add(k);
                                categoryIds.addAll(k.getCategories().stream()
                                        .map(CategoryDto::getCategoryId)
                                        .toList());
                            }
                    );
                }
            }
            if (!categoryIds.isEmpty()) {
            // Iterate over the list and print each element
            System.out.println("Contents of categoryIds list:");
            for (String categoryId : categoryIds) {
                System.out.println(categoryId);
            }

            // Creat a map to store category counts
            Map<String, Integer> categoryCounts = new HashMap<>();

            // Iterate through categoryIds and count occurrences
            for (String categoryId : categoryIds) {
                categoryCounts.put(categoryId, categoryCounts.getOrDefault(categoryId, 0) + 1);
            }
            // Iterate over the map entries and print each key-value pair
            System.out.println("Contents of categoryCounts map:");
            for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
                System.out.println("Category ID: " + entry.getKey() + ", Count: " + entry.getValue());
            }

            // Create lists to store proposed and selected categories
            List<CategoryDto> proposedCategories = new ArrayList<>();
            List<CategoryDto> selectedCategories = new ArrayList<>();

            // Sort the category counts by value in descending order
            List<Map.Entry<String, Integer>> sortedCategoryCounts = new ArrayList<>(categoryCounts.entrySet());
            sortedCategoryCounts.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

            // Iterate over the sorted list of entries and print each key-value pair
            System.out.println("Sorted category counts:");
            for (Map.Entry<String, Integer> entry : sortedCategoryCounts) {
                System.out.println("Category ID: " + entry.getKey() + ", Count: " + entry.getValue());

                String categoryId = entry.getKey();
                Optional<CategoryDto> category = categoryService.findOneCategory(categoryId);
                category.ifPresent(proposedCategories::add);
            }


            // Add categories to proposedCategories and selectedCategories
            int count = 0;
            for (Map.Entry<String, Integer> entry : sortedCategoryCounts) {
                if (count < 2) {
                    System.out.println(count);
                    String categoryId = entry.getKey();
                    System.out.println("categoryId :" + categoryId);
                    Optional<CategoryDto> category = categoryService.findOneCategory(categoryId);
                    System.out.println(category.get());
                    category.ifPresent(selectedCategories::add);
                    count++;
                }
                // Break after adding the top 2 categories
                if (count >= 2) {
                    break;
                }
            }
            // Iterate over the selectedCategories list and print each CategoryDto object
            System.out.println("Selected categories:");
            for (CategoryDto categoryDto : selectedCategories) {
                System.out.println(categoryDto); // Assuming CategoryDto has overridden toString() method
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
