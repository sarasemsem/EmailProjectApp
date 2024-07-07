package com.emailProcessor.emailProcessor.service.impl;

import com.emailProcessor.basedomains.dto.*;
import com.emailProcessor.emailProcessor.entity.*;
import com.emailProcessor.emailProcessor.repository.CategoryRepository;
import com.emailProcessor.emailProcessor.repository.KeywordRepository;
import com.emailProcessor.emailProcessor.service.KeywordService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link com.emailProcessor.emailProcessor.entity.Keyword}.
 */
@Service
@Transactional
@AllArgsConstructor
public class KeywordServiceImpl implements KeywordService {
    private final Logger log = LoggerFactory.getLogger(KeywordServiceImpl.class);
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ModelMapper customModelMapper;
    private final KeywordRepository keywordRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public Keyword save(KeywordDto keywordDto) {
        try {
            log.debug("Request to save Keyword : {}", keywordDto);
            System.out.println("Request to save Keyword :" + keywordDto.toString());

            Keyword toSaveKeyword = new Keyword();
            toSaveKeyword.setWord(keywordDto.getWord());
            toSaveKeyword.setWeight(keywordDto.getWeight());

            if (keywordDto.getCreatedBy().getUserId() != null) {
                UserDto userDto = keywordDto.getCreatedBy();
                User user = modelMapper.map(userDto, User.class);
                toSaveKeyword.setCreatedBy(user);
            }
            if (keywordDto.getCategories() != null) {
                List<CategoryDto> categoriesDtos = keywordDto.getCategories();
                List<Category> categories = categoriesDtos.stream()
                        .map(categoryDto -> modelMapper.map(categoryDto, Category.class))
                        .toList();
                toSaveKeyword.setCategories(categories);
            }
            if (keywordDto.getTranslatedKeywords() != null) {
                List<TranslatedKeywordDto> translatedKeywordDtos = keywordDto.getTranslatedKeywords();
                List<TranslatedKeyword> translatedKeywords = translatedKeywordDtos.stream()
                        .map(translatedKeyword -> modelMapper.map(translatedKeyword, TranslatedKeyword.class))
                        .toList();
                toSaveKeyword.setTranslatedKeywords(translatedKeywords);
            }

            // Save the Keyword entity
            Keyword savedKeyword = keywordRepository.save(toSaveKeyword);

            // Update each Category document to include the newly created keyword
            for (CategoryDto category : keywordDto.getCategories()) {
                mongoTemplate.update(Category.class)
                        .matching(Criteria.where("categoryId").is(category.getCategoryId()))
                        .apply(new Update().push("keywords").value(savedKeyword.getKeywordId()))
                        .first();
            }
            //updateCachedList(modelMapper.map(savedKeyword, KeywordDto.class));
            return savedKeyword;
        } catch (Exception e) {
            log.error("Error occurred while saving Keyword: {}", e.getMessage());
            throw new RuntimeException("Error occurred while saving Keyword", e);
        }
    }



    @Override
    public Keyword update(Keyword keyword) {
        log.debug("Request to update Keyword : {}", keyword);
        return keywordRepository.save(keyword);
    }

    @Override
    public Optional<Keyword> partialUpdate(Keyword keyword) {
        log.debug("Request to partially update Keyword : {}", keyword);

        return keywordRepository
            .findById(keyword.getKeywordId())
            .map(existingKeyword -> {
                existingKeyword.setWord(keyword.getWord());
                existingKeyword.setCreatedBy(keyword.getCreatedBy());

                return existingKeyword;
            })
            .map(keywordRepository::save);
    }

    @Override
    public List<KeywordDto> findAllKeywords() {
        log.debug("Request to get all Keywords");
        try {
            List<Keyword> keywords = keywordRepository.findAll();
            if (!keywords.isEmpty()) {
                return convertToDto(keywords);
            } else {
                return Collections.emptyList(); // or handle null case as per your requirement
            }
        } catch (RuntimeException e) {
            log.error("Error occurred while fetching all Keywords: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch all Keywords", e);
        }
    }

    @Override
    public List<KeywordDto> convertToDto(List<Keyword> keywords) {
        return keywords.stream()
                .map(this::convertKeywordToDto)
                .collect(Collectors.toList());
    }
    @Override
    public KeywordDto convertKeywordToDto(Keyword keyword) {
        if (keyword == null) {
            return null;
        }
        KeywordDto dto = new KeywordDto();
        if (keyword.getKeywordId() != null) {
            dto.setKeywordId(keyword.getKeywordId());
        }
        if (keyword.getWord() != null) {
            dto.setWord(keyword.getWord());
        }
        if (keyword.getCreatedBy() != null) {
            UserDto createdByDto = getUserDto(keyword);
            dto.setCreatedBy(createdByDto);
        }
        dto.setWeight(keyword.getWeight());
        // Null check before mapping categories to CategoryDto
        if (keyword.getCategories() != null) {
            List<CategoryDto> categoryDtoList = keyword.getCategories().stream()
                    .map(category -> {
                        if (category != null) {
                            CategoryDto categoryDto = new CategoryDto();
                            if (category != null) {
                                categoryDto.setCategoryId(category.getCategoryId());
                                if (category.getTitle() != null) {
                                    categoryDto.setTitle(category.getTitle());
                                }
                                if (category.getDescription() != null) {
                                    categoryDto.setDescription(category.getDescription());
                                }
                                if (category.getKeywords() != null) {
                                    categoryDto.setKeywords(category.getKeywords());
                                }
                                if (category.getAction() != null) {
                                    ActionDto actionDto = getActionDto(category.getAction());
                                    categoryDto.setAction(actionDto);
                                }
                                // Map other fields if necessary
                                return categoryDto;
                            }
                            return categoryDto;
                        }
                        return null;
                    })
                    .collect(Collectors.toList());
            dto.setCategories(categoryDtoList);
        }
        if (keyword.getTranslatedKeywords() != null) {
            List<TranslatedKeywordDto> translatedKeywordDtoList = keyword.getTranslatedKeywords().stream()
                    .map(translatedKeyword -> modelMapper.map(translatedKeyword, TranslatedKeywordDto.class))
                    .collect(Collectors.toList());
            dto.setTranslatedKeywords(translatedKeywordDtoList);
        }
        return dto;
    }

    private static ActionDto getActionDto(Action action) {
        ActionDto actionDto =new ActionDto();
        if (action.getActionId() != null) {
            actionDto.setActionId(action.getActionId());
        }
        actionDto.setAction(action.getAction());
        if (action.getActionDate() != null) {
        actionDto.setActionDate(action.getActionDate());
        }
        if (action.getAction() != null) {
        actionDto.setParams(action.getParams());
        }
        if (action.getAction() != null) {
            actionDto.setAffected(action.getAffected());
        }
        if (action.getAction() != null) {
            actionDto.setState(action.getState());
        }
        if (action.getAction() != null) {
            actionDto.setEndPoint(action.getEndPoint());
        }
        return actionDto;
    }

    private static UserDto getUserDto(Keyword keyword) {
        UserDto createdByDto = new UserDto();
        User createdBy = keyword.getCreatedBy();

        // Map non-null fields of createdBy to UserDto
        if (createdBy.getUserId() != null) {
            createdByDto.setUserId(createdBy.getUserId());
        }
        if (createdBy.getFirstName() != null) {
            createdByDto.setFirstName(createdBy.getFirstName());
        }
        if (createdBy.getLastName() != null) {
            createdByDto.setLastName(createdBy.getLastName());
        }
        return createdByDto;
    }

    @Override
    public Optional<Keyword> findOne(String id) {
        log.debug("Request to get Keyword : {}", id);
        return keywordRepository.findById(id);
    }

    @Override
    public Optional<List<Keyword>> KeywordsByCategory(String id) {
        List<Keyword> keywords = keywordRepository.findKeywordsByCategories(id);
        System.out.println("the keywords by category:"+keywords);
        if (keywords != null) {
            return Optional.of(keywords);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Keyword> findKeywordByWord(String word) {
        return keywordRepository.findKeywordByWord(word);
    }

    @Override
    public ResponseEntity<String> delete(String id) {
        log.debug("Request to delete Keyword : {}", id);
        try {
            Optional<Keyword> keywordOptional = keywordRepository.findById(id);
            if (keywordOptional.isPresent()) {
                Keyword keyword = keywordOptional.get();

                // Ensure keyword.getCategories() is not null
                if (keyword.getCategories() != null) {
                    // Remove keyword ID from associated categories
                    for (Category category : keyword.getCategories()) {
                        if (category != null) {
                            if (category.getKeywords() != null) {
                                category.getKeywords().removeIf(keywordId -> keywordId.equals(id));
                            } else {
                                log.warn("Category keywords list is null for category: {}", category);
                            }
                        } else {
                            log.warn("Category is null in keyword: {}", keyword);
                        }
                    }

                    // Save the updated categories
                    categoryRepository.saveAll(keyword.getCategories());
                }

                // Delete the keyword
                keywordRepository.deleteById(id);

                return ResponseEntity.noContent().build(); // 204 No Content
            } else {
                return ResponseEntity.notFound().build(); // 404 Not Found
            }
        } catch (Exception e) {
            log.error("Error occurred while deleting Keyword: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete the resource"); // 500 Internal Server Error
        }
    }


    @Override
    public ResponseEntity<String> deleteRelatedKeyword(String id) {
        log.debug("Request to delete Keyword : {}", id);
        try {
            Optional<Keyword> keywordOptional = keywordRepository.findById(id);
            if (keywordOptional.isPresent()) {
                Keyword keyword = keywordOptional.get();
                // Delete the keyword
                keywordRepository.deleteById(id);
                return ResponseEntity.noContent().build(); // 204 No Content
            } else {
                return ResponseEntity.notFound().build(); // 404 Not Found
            }
        } catch (Exception e) {
            log.error("Error occurred while deleting Keyword: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete the resource"); // 500 Internal Server Error
        }
    }
    @CachePut(value = "keyword", key = "'allKeywords'")
    private List<KeywordDto> updateCachedList(KeywordDto keywordDto) {
        List<KeywordDto> cachedList = findAllKeywords();
        // Find the cachedList with the same ID as the saved/updated object
        for (int i = 0; i < cachedList.size(); i++) {
            if (cachedList.get(i).getKeywordId().equals(keywordDto.getKeywordId())) {
                // Update the cached list
                cachedList.set(i, keywordDto);
                break;
            }
        }
        return cachedList ;
    }

    @CacheEvict(value = "keyword", allEntries = true )
    public String clearCache(){
        return "Cache has been cleared";
    }
}
