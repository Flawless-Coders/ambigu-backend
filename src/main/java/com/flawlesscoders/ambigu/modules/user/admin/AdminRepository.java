package com.flawlesscoders.ambigu.modules.user.admin;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.flawlesscoders.ambigu.modules.user.waiter.Waiter;

public interface AdminRepository extends MongoRepository<Admin, String> {
    @Query("{ '_class': 'com.flawlesscoders.ambigu.modules.user.admin.Admin' }")
    List<Waiter> findAllAdmins();

    @Query("{ '_class': 'com.flawlesscoders.ambigu.modules.user.admin.Admin', 'id': ?0 }")
    List<Admin> findAdminById(String id);
}
