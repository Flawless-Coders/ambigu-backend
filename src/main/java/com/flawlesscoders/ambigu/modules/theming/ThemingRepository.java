package com.flawlesscoders.ambigu.modules.theming;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ThemingRepository extends MongoRepository<Theming, String> {
    //TRAER EL TEMA POR DEFECTO con el id default_them
    @Query("{ 'id' : 'default_theme' }")
    Theming find();
}
