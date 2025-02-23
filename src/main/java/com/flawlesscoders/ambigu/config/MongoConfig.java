package com.flawlesscoders.ambigu.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Value;

@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.database}") // Nombre de la base de datos desde application.properties
    private String databaseName;

    @Bean
    public GridFSBucket gridFSBucket(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        return GridFSBuckets.create(database);
    }

}

