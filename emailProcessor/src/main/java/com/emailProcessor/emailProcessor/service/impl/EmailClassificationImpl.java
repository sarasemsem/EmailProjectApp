package com.emailProcessor.emailProcessor.service.impl;

import com.emailProcessor.basedomains.dto.*;
import com.emailProcessor.emailProcessor.entity.Keyword;
import com.emailProcessor.emailProcessor.service.*;
import edu.stanford.nlp.ling.CoreLabel;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EmailClassificationImpl implements EmailClassification {
    @Autowired
    private KeywordService keywordService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ActionParamService actionParamService;

    @Override
    public EmailProcessingResultDto getClassificationResult(List<CoreLabel> coreLabelList) {
        // Create EmailProcessingResult object and set categories and keywords
        EmailProcessingResultDto emailProcessingResult = new EmailProcessingResultDto();
        Map<String, Double> categoryScores = new HashMap<>();
        List<KeywordDto> foundKeywords = new ArrayList<>();
        System.out.println("get classification result with coreLabel ="+coreLabelList);
        // Iterate through coreLabelList and extract category IDs
        for (CoreLabel coreLabel : coreLabelList) {
            String word = coreLabel.lemma();
            System.out.println("word is :"+ word);


            if (word.matches("[a-zA-Z0-9]{2,}")) {
                Optional<Keyword> keywordOptional = keywordService.findKeywordByWord(word);
                if (keywordOptional.isPresent()) {
                    System.out.println("i found this keyword is :"+keywordOptional.get().getKeywordId()+keywordOptional.get().getWord());
                    Keyword keyword1 = keywordOptional.get();
                    KeywordDto keyword = keywordService.convertKeywordToDto(keyword1);
                    System.out.println("aaaaaa keyword categories is :"+ keyword.getCategories());
                    if (!foundKeywords.contains(keyword)) {
                        foundKeywords.add(keyword);
                    }
                    if (keyword.getCategories() != null) {
                        for (CategoryDto category : keyword.getCategories()) {
                            double weight = keyword.getWeight();
                            categoryScores.put(category.getCategoryId(), categoryScores.getOrDefault(category.getCategoryId(), 0.0) + weight);
                        }
                    }
                }
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
            double score = (double) 0;
            int count = 0;
            for (Map.Entry<String, Double> entry : sortedFilteredScores) {
                score= score + entry.getValue();
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

            emailProcessingResult.setProposedCategories(proposedCategories);
            emailProcessingResult.setSelectedCategories(selectedCategories);
            emailProcessingResult.setFoundKeywords(foundKeywords);
            emailProcessingResult.setScore(score/100);

            ActionDto actions = selectedCategories.stream().findFirst().get().getAction();
            if (actions != null ){
                System.out.println("list of related actions: " +actions);
                ActionParamDto relatedActions = new ActionParamDto();
                    Map<String, String> params = findParams(actions, coreLabelList);
                    System.out.println("related actions.: "+actions);
                    System.out.println("related params.: "+params);
                    relatedActions.setAction(actions);
                    relatedActions.setParams(params);

                    ActionParamDto savedActionParam = actionParamService.saveActionParam(relatedActions); // Assuming saveActionParam is a method that saves one ActionParamDto
                emailProcessingResult.setRelatedActions(savedActionParam);}
        }
        return emailProcessingResult;
    }

    @Override
    public RelatedDataDto getRelatedData(List<CoreLabel> coreLabelList) {
        System.out.println("get getRelatedData with coreLabel ="+coreLabelList);
        Map<String, String> currencyMap = getStringStringMap();
        RelatedDataDto relatedDataDto = new RelatedDataDto();
        for (CoreLabel coreLabel : coreLabelList) {
            String word = coreLabel.lemma();
        // Check each word and extract data based on your predefined fields
        if (word.equals("account") && coreLabel.index() + 1 < coreLabelList.size()) {
            String nextWord = coreLabelList.get(coreLabel.index() + 1).word().toLowerCase();
            System.out.println("the word is"+word+" and the next word is:"+nextWord );
            if (nextWord.equals("number")&& coreLabel.index() + 2 < coreLabelList.size()) {
                String accountNumber = coreLabelList.get(coreLabel.index() + 2).word();
                System.out.println("the word is"+word+" and the next word is :"+ accountNumber );
                relatedDataDto.setAccount_number(accountNumber);
            } else if (nextWord.equals("type")&& coreLabel.index() + 2 < coreLabelList.size()) {
                String accountType = coreLabelList.get(coreLabel.index() + 2).word();
                relatedDataDto.setAccount_type(accountType);
            }
        }
        for (String type : getAccountTypeSet()) {
            if (type.equalsIgnoreCase(word)) {
                System.out.println("Found matching type: " + type);
                relatedDataDto.setAccount_type(type);
                break;
            }
        }
        if (word.matches(".*\\d.*") && word.length() == 10) {
            relatedDataDto.setAccount_number(word);
        }
        if(word.equals("period")) {
            // Assuming period is represented as a timestamp
            // You need to convert the timestamp string to Instant, assuming it's in a specific format
            // Example: period = Instant.parse(coreLabelList.get(coreLabel.index() + 2).word());
        }
        if (word.equals("amount")) {
            // Assuming "amount" is followed by the amount value
            double amount = Double.parseDouble(coreLabelList.get(coreLabel.index() + 1).word());
            relatedDataDto.setAmount(amount);
        }

        for (Map.Entry<String, String> entry : currencyMap.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(word)) {
                System.out.println("in currency:" + word);
                relatedDataDto.setCurrency(entry.getValue());
                break;
            }else if (entry.getValue().equalsIgnoreCase(word)){
                System.out.println("in currency:" + word);
                relatedDataDto.setCurrency(entry.getValue());
                break; // Exit the loop once a match is found
            }
        }
        if ((word.equals("recipient") || word.equals("to")) && coreLabel.index() + 1 < coreLabelList.size()) {
            String nextWord = coreLabelList.get(coreLabel.index() + 1).word().toLowerCase();
            String recipientAccount ;
            if ((nextWord.equals("account") || nextWord.equals("this"))&& coreLabel.index() + 2 < coreLabelList.size()) {
                String thirdWord = coreLabelList.get(coreLabel.index() + 2).word().toLowerCase();
                if (thirdWord.equals("account") && coreLabel.index() + 3 < coreLabelList.size()) {
                    recipientAccount = coreLabelList.get(coreLabel.index() + 3).word();
                }else{
                    recipientAccount = coreLabelList.get(coreLabel.index() + 2).word();}
                relatedDataDto.setRecipient_account(recipientAccount);
            }
        }
    }
        return null;
    }
    @Override
    public Map<String, String> findParams(ActionDto action, List<CoreLabel> coreLabelList) {
        Map<String, String> params = null;
        if (action != null && action.getParams() != null) {
            for (String param : action.getParams()) {
                for (CoreLabel coreLabel : coreLabelList) {
                    String word = coreLabel.lemma();
                    if (word.matches("[a-zA-Z0-9]{2,}")) {
                        if (word.toLowerCase().equals(param.toLowerCase())) {
                            params.put(param, coreLabelList.get(coreLabel.index() + 1).word());
                        }
                    }
                }
            }
        }
        return params;
    }
    private static Map<String, String> getStringStringMap() {
        Map<String, String> currencyMap = new HashMap<>();
        currencyMap.put("united states dollar", "usd");
        currencyMap.put("euro", "eur");
        currencyMap.put("japanese yen", "jpy");
        currencyMap.put("british pound sterling", "gbp");
        currencyMap.put("australian dollar", "aud");
        currencyMap.put("canadian dollar", "cad");
        currencyMap.put("swiss franc", "chf");
        currencyMap.put("chinese yuan", "cny");
        currencyMap.put("swedish krona", "sek");
        currencyMap.put("new zealand dollar", "nzd");
        return currencyMap;
    }
    private static Set<String> getAccountTypeSet() {
        Set<String> accountTypeSet = new HashSet<>();
        accountTypeSet.add("checking account");
        accountTypeSet.add("savings account");
        accountTypeSet.add("credit card account");
        return accountTypeSet;
    }
}