package com.emailProcessor.emailProcessor.repository;

import com.emailProcessor.emailProcessor.entity.ActionParam;
import com.emailProcessor.emailProcessor.entity.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public class CustomActionParamRepositoryImpl implements CustomActionsParamRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<ActionParam> findActionParamWithAffectedTrueAndActionDateBetween(Instant startOfDay, Instant endOfDay) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("result.relatedActions.affected").is(true)
                        .and("result.relatedActions.actionDate").gte(startOfDay).lt(endOfDay))
        );

        AggregationResults<ActionParam> results = mongoTemplate.aggregate(agg, "actionParam", ActionParam.class);
        return results.getMappedResults();
    }
}