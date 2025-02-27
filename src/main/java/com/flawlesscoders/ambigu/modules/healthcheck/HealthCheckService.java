package com.flawlesscoders.ambigu.modules.healthcheck;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HealthCheckService {

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    private final MongoClient mongoClient;

    public boolean MongoHealthCheck() {
        try {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            database.listCollectionNames().first();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ResponseEntity<String> healthCheck() {
        try{
            boolean isMongoUp = MongoHealthCheck();
            if(isMongoUp){
                return ResponseEntity.ok("Health check OK");
            } else {
                return ResponseEntity.status(503).body("Database is down");
            }
        } catch (Exception e) {
            return ResponseEntity.status(503).body("Health check failed");
        }
    }
}
