package com.flawlesscoders.ambigu.utils;

import com.flawlesscoders.ambigu.modules.category.Category;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
@Profile("migrate-category") 
public class CategoryMigrationRunner implements CommandLineRunner {

    @Autowired
    private Base64ImageMigrationService migrationService;

    @Override
    public void run(String... args) {
        migrationService.migrate(
            Category.class,
            "imageBase64",      // campo base64 actual
            "imageId",    // nuevo campo con el ObjectId de GridFS
            "name"        // nombre para el archivo en GridFS
        );
    }
}