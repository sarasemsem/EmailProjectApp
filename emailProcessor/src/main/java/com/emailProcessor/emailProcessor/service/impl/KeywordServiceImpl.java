package com.emailProcessor.emailProcessor.service.impl;

import com.emailProcessor.basedomains.dto.EmailDto;
import com.emailProcessor.basedomains.dto.KeywordDto;
import com.emailProcessor.emailProcessor.entity.Category;
import com.emailProcessor.emailProcessor.entity.Keyword;
import com.emailProcessor.emailProcessor.repository.KeywordRepository;
import com.emailProcessor.emailProcessor.service.KeywordService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    private final KeywordRepository keywordRepository;
    private final ModelMapper modelMapper;

    @Override
    public Keyword save(KeywordDto keywordDto) {
        log.debug("Request to save Keyword : {}", keywordDto);
        Keyword savedKeyword = keywordRepository.insert(modelMapper.map(keywordDto, Keyword.class));
        mongoTemplate.update(Category.class)
                .matching(Criteria.where("categoryId").is(keywordDto.getCategoryId()))
                .apply(new Update().push("keywords").value(savedKeyword.getKeywordId())) // Corrected field name to "reviewsIds"
                .first();
        return savedKeyword;
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
                if (keyword.getWord() != null) {
                    existingKeyword.setWord(keyword.getWord());
                }
                if (keyword.getCreatedBy() != null) {
                    existingKeyword.setCreatedBy(keyword.getCreatedBy());
                }

                return existingKeyword;
            })
            .map(keywordRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Keyword> findAll() {
        log.debug("Request to get all Keywords");
        return keywordRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Keyword> findOne(String id) {
        log.debug("Request to get Keyword : {}", id);
        return keywordRepository.findById(id);
    }

    @Override
    public ResponseEntity<Void> delete(String id) {
        log.debug("Request to delete Keyword : {}", id);
        if (keywordRepository.existsById(id)) {
            keywordRepository.deleteById(id);
            return ResponseEntity.noContent().build(); // Indicate successful deletion with no body
        } else {
            return ResponseEntity.notFound().build(); // Indicate that the resource was not found
        }
    }
}