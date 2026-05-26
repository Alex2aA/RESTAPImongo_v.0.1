package com.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

    @Value("${mongo.uri.reader}")
    private String readerUri;

    @Value("${mongo.uri.editor}")
    private String editorUri;

    @Value("${mongo.uri.admin}")
    private String adminUri;

    @Bean
    public MongoClient readerMongoClient() {
        return MongoClients.create(readerUri);
    }

    @Bean
    public MongoClient editorMongoClient() {
        return MongoClients.create(editorUri);
    }

    @Bean
    public MongoClient adminMongoClient() {
        return MongoClients.create(adminUri);
    }

    @Bean
    public MongoTemplate readerMongoTemplate() {
        return new MongoTemplate(readerMongoClient(), "company_db");
    }

    @Bean
    public MongoTemplate editorMongoTemplate() {
        return new MongoTemplate(editorMongoClient(), "company_db");
    }

    @Bean
    public MongoTemplate adminMongoTemplate() {
        return new MongoTemplate(adminMongoClient(), "company_db");
    }

}
