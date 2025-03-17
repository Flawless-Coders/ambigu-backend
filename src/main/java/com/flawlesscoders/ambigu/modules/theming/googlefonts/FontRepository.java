package com.flawlesscoders.ambigu.modules.theming.googlefonts;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface FontRepository extends MongoRepository<Fonts, String> {
    
}
