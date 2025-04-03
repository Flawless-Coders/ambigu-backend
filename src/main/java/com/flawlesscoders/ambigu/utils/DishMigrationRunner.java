package com.flawlesscoders.ambigu.utils;

import com.flawlesscoders.ambigu.modules.dish.Dish;
import com.flawlesscoders.ambigu.utils.Base64ImageMigrationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
@Profile("migrate") // Solo se ejecutará si el perfil "migrate" está activo
public class DishMigrationRunner implements CommandLineRunner {

    @Autowired
    private Base64ImageMigrationService migrationService;

    @Override
    public void run(String... args) {
        migrationService.migrate(
            Dish.class,
            "imageBase64",   // campo base64 en tu modelo
            "imageId",       // campo nuevo que guardarás
            "name"           // campo que usarás como nombre de archivo
        );
    }
}
