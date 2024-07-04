package com.emailProcessor.emailProcessor.repository;

import com.emailProcessor.emailProcessor.entity.ActionParam;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
@Repository
public interface CustomActionsParamRepository {
    @Query("{'affectedActions': {$exists: true}, 'actionDate': {$gte: ?0, $lt: ?1}}")
    List<ActionParam> findActionParamWithAffectedTrueAndActionDateBetween(Instant startOfDay, Instant endOfDay);

}
