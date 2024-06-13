package com.emailProcessor.emailProcessor.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

@Configuration
public class GridFsTemplateConfig {
    @Autowired
    private MongoDatabaseFactory mongoDbFactory;

    @Autowired
    private MongoConverter mongoConverter;

    @Bean
    public GridFsTemplate gridFsTemplate() {
        return new GridFsTemplate(mongoDbFactory, mongoConverter);
    }
}
