package com.emailProcessor.emailProcessor.service.impl;

import com.emailProcessor.basedomains.dto.*;
import com.emailProcessor.emailProcessor.entity.Keyword;
import com.emailProcessor.emailProcessor.service.*;
import edu.stanford.nlp.ling.CoreLabel;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EmailClassificationImpl implements EmailClassification {
    @Autowired
    private KeywordService keywordService;
    @Autowired
    private ActionService actionService;
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
        System.out.println("get classification result with coreLabel ");
        // Iterate through coreLabelList and extract category IDs
        for (CoreLabel coreLabel : coreLabelList) {
            String word = coreLabel.lemma();
            System.out.println("word is :");


            if (word.matches("[a-zA-Z0-9]{2,}")) {
                List<Keyword> keywords = keywordService.findKeywordByWord(word);
                for (Keyword keyword1 : keywords) {
                    System.out.println("i found this keyword is :" + keyword1.getKeywordId() + keyword1.getWord());
                    KeywordDto keyword = keywordService.convertKeywordToDto(keyword1);
                    System.out.println("aaaaaa keyword categories is :" + keyword.getCategories());
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
                String categoryId = entry.getKey();
                Optional<CategoryDto> category = categoryService.findOneCategory(categoryId);
                category.ifPresent(selectedCategories::add);
                count++;
                // Break after adding the top 2 categories
                if (count >= 2) {
                    break;
                }
            }

            emailProcessingResult.setProposedCategories(proposedCategories);
            emailProcessingResult.setSelectedCategories(selectedCategories);
            emailProcessingResult.setFoundKeywords(foundKeywords);
            emailProcessingResult.setScore(score/100);

            if (selectedCategories.stream().findFirst().isPresent()) {
                ActionDto getAction = selectedCategories.stream().findFirst().get().getAction();
                if (getAction != null ){
                    System.out.println("list of related actions: " +getAction);
                    ActionDto actionDto = actionService.findOneAction(getAction.getActionId());
                        ActionParamDto relatedActions = new ActionParamDto();
                        Map<String, String> params = findParams(actionDto, coreLabelList);

                        System.out.println("related actions.: "+actionDto);
                        System.out.println("related params.: "+params);

                        // Convert params to paramsMap if needed (though it's redundant in this case)
                        Map<String, String> paramsMap = params.entrySet().stream()
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        Map.Entry::getValue,
                                        (existingValue, newValue) -> existingValue // Merge function to keep the existing value in case of a duplicate key
                                ));


                        relatedActions.setAction(actionDto);
                        relatedActions.setParams(paramsMap);
                        relatedActions.setActionDate(Instant.now());
                        relatedActions.setAffected(true);
                        ActionParamDto savedActionParam = actionParamService.saveActionParam(relatedActions); // Assuming saveActionParam is a method that saves one ActionParamDto
                        emailProcessingResult.setRelatedActions(savedActionParam);


                }
            }
        }
        return emailProcessingResult;
    }



    @Override
    public RelatedDataDto getRelatedData(List<CoreLabel> coreLabelList) {
        System.out.println("get getRelatedData with coreLabel");
        Map<String, String> currencyMap = getStringStringMap();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.systemDefault());
        RelatedDataDto relatedDataDto = new RelatedDataDto();
        for (CoreLabel coreLabel : coreLabelList) {
            String word = coreLabel.lemma();
        // Check each word and extract data based on your predefined fields
        if (word.equalsIgnoreCase("account") && coreLabel.index() + 1 < coreLabelList.size()) {
            String nextWord = coreLabelList.get(coreLabel.index() + 1).word().toLowerCase();
            System.out.println("the word is"+word+" and the next word is:"+nextWord );
            if (nextWord.equalsIgnoreCase("number")&& coreLabel.index() + 2 < coreLabelList.size()) {
                String accountNumber = coreLabelList.get(coreLabel.index() + 2).word();
                System.out.println("the word is"+word+" and the next word is :"+ accountNumber );
                relatedDataDto.setAccount_number(accountNumber);
            } else if (nextWord.equalsIgnoreCase("type")&& coreLabel.index() + 2 < coreLabelList.size()) {
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
            if (word.equalsIgnoreCase("period")) {
                // Assuming period is followed by a date in the format dd/MM/yyyy
                if (coreLabel.index() + 1 < coreLabelList.size()) {
                    String periodDateStr = coreLabelList.get(coreLabel.index() + 1).word();
                    try {
                        Instant periodDate = LocalDate.parse(periodDateStr, formatter).atStartOfDay(ZoneId.systemDefault()).toInstant();
                        relatedDataDto.setPeriod(periodDate);
                        System.out.println("Period date set to: " + periodDate);
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid date format for period: " + periodDateStr);
                    }
                }
            }
        if (word.equalsIgnoreCase("amount")) {
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
        if ((word.equalsIgnoreCase("recipient") || word.equalsIgnoreCase("to")) && coreLabel.index() + 1 < coreLabelList.size()) {
            String nextWord = coreLabelList.get(coreLabel.index() + 1).word().toLowerCase();
            String recipientAccount ;
            if ((nextWord.equalsIgnoreCase("account") || nextWord.equalsIgnoreCase("this"))&& coreLabel.index() + 2 < coreLabelList.size()) {
                String thirdWord = coreLabelList.get(coreLabel.index() + 2).word().toLowerCase();
                if (thirdWord.equalsIgnoreCase("account") && coreLabel.index() + 3 < coreLabelList.size()) {
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
        Map<String, String> params = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.systemDefault());
        System.out.println("action in findParams is: " + action);

        if (action != null && action.getParams() != null) {
            System.out.println("and its params are: " + action.getParams());

            // Create a map of CoreLabels for quick lookup by index
            Map<Integer, CoreLabel> labelMap = coreLabelList.stream()
                    .collect(Collectors.toMap(CoreLabel::index, label -> label, (existing, replacement) -> existing));

            // Join the content into a single string for pattern matching
            String content = coreLabelList.stream().map(CoreLabel::word).collect(Collectors.joining(" "));

            // Define date pattern
            Pattern datePattern = Pattern.compile("\\b(\\d{2}/\\d{2}/\\d{4})\\b");

            for (String param : action.getParams()) {
                String[] paramWords = param.split(" ");
                String paramPattern = String.join("\\s+", paramWords) + "\\s+(\\S+)";
                Pattern pattern = Pattern.compile(paramPattern, Pattern.CASE_INSENSITIVE);
                System.out.println("pattern: " + pattern);
                System.out.println("content: " + content);

                Matcher matcher = pattern.matcher(content);
                System.out.println("matcher: " + matcher);

                if (matcher.find()) {
                    String value = matcher.group(1);
                    System.out.println("value: " + value);
                    params.put(param, value);
                } else if (param.equalsIgnoreCase("start date") || param.equalsIgnoreCase("end date")) {
                    Matcher dateMatcher = datePattern.matcher(content);
                    if (param.equalsIgnoreCase("start date") && dateMatcher.find()) {
                        String startDate = dateMatcher.group(1);
                        System.out.println("start date: " + startDate);
                        params.put(param, startDate);
                    } else if (param.equalsIgnoreCase("end date") && dateMatcher.find() && dateMatcher.find()) {
                        String endDate = dateMatcher.group(1);
                        System.out.println("end date: " + endDate);
                        params.put(param, endDate);
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
