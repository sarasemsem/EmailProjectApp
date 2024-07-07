package com.emailProcessor.emailProcessor.repository;
import com.emailProcessor.emailProcessor.entity.Keyword;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KeywordRepository extends MongoRepository<Keyword, String> {
    List<Keyword> findKeywordByWord(String word);
    List<Keyword> findKeywordsByCategories(String id);
}
