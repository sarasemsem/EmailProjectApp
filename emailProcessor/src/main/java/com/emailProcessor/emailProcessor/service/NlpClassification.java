package com.emailProcessor.emailProcessor.service;

import com.emailProcessor.basedomains.dto.*;
import com.emailProcessor.emailProcessor.configuration.Pipeline;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class NlpClassification {
    @Autowired
    private EmailService emailService;
    @Autowired
    private RelatedDataService relatedDataService;
    @Autowired
    private EmailProcessingResultService emailProcessingResultService;
    @Autowired
    private EmailClassification emailClassification;
    private static final Logger logger = (Logger) LoggerFactory.getLogger(NlpClassification.class);

    public void classification() {
        try {
            List<EmailDto> emails = emailService.getAllUntreatedEmails();
            emails.forEach(this::treatEmail);
        } catch (Exception e) {
            logger.error("An error occurred while processing emails", e);
        }
    }

    public void treatEmail(EmailDto email) {
        Set<String> urgentWords = new HashSet<>(Arrays.asList("urgent", "urgently", "asap", "immediate", "important", "critical", "priority"));
        try {
            StanfordCoreNLP stanfordCoreNLP = Pipeline.getPipeline();
            String emailContent = email.getContent();
            if (emailContent == null || emailContent.trim().isEmpty()) {
                // Handle empty content case
               //mark email as treated without processing
                email.setSpam(true);
                emailService.partialUpdate(email);
                return;
            }
            else {
                CoreDocument coreDocument = new CoreDocument(emailContent);
                stanfordCoreNLP.annotate(coreDocument);
                // Extracting individual words and processing them
                List<CoreLabel> coreLabelList = coreDocument.tokens();

                // Iterate through coreLabelList
                for (CoreLabel coreLabel : coreLabelList) {
                    String word = coreLabel.lemma();
                    if (word.matches("[a-zA-Z0-9]{2,}")) {
                        if (urgentWords.contains(word.toLowerCase())) {
                            email.setUrgent(true);
                            break;
                        }
                    }
                }
                EmailProcessingResultDto emailProcessingResult = emailClassification.getClassificationResult(coreLabelList);
                RelatedDataDto relatedDataDto = emailClassification.getRelatedData(coreLabelList);
                if (emailProcessingResult != null) {
                    if (emailProcessingResult.getRelatedActions()!=null && emailProcessingResult.getRelatedActions().getAction()!=null) {
                        System.out.println("related action is "+emailProcessingResult.getRelatedActions().getAction());
                        // Save to DB
                        EmailProcessingResultDto savedEmailProcessingResult = emailProcessingResultService.saveNewEmailProcessingResult(emailProcessingResult);
                        email.setResult(savedEmailProcessingResult);
                        email.setTreated(true);
                    }
                }
                if (relatedDataDto != null) {
                    RelatedDataDto relatedData = relatedDataService.saveRelatedData(relatedDataDto);
                    System.out.println("related data" + relatedData);
                    email.setRelatedData(relatedData);
                }
                // Update email with treated flag
                emailService.partialUpdate(email);
            }
            } catch(Exception e){
            logger.error("An error occurred while treating email", e);
        }
        }

}