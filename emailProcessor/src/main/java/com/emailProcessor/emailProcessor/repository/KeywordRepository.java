package com.emailProcessor.emailProcessor.repository;
import com.emailProcessor.emailProcessor.entity.Keyword;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeywordRepository extends MongoRepository<Keyword, String> {
    Keyword findKeywordByWord(String word);
}
