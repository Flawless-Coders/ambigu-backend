package com.flawlesscoders.ambigu.modules.user.admin;


import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


public interface AdminRepository extends MongoRepository<Admin, String> {
    @Query("{ '_class': 'com.flawlesscoders.ambigu.modules.user.admin.Admin', 'id': ?0 }")
    Optional<Admin> findAdminById(String id);
}
