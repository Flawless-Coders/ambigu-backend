package com.flawlesscoders.ambigu.modules.user.admin;

import org.springframework.data.mongodb.core.mapping.Document;

import com.flawlesscoders.ambigu.modules.user.base.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Document(collection="users")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Schema(description = "Model that represents the admin user")
public class Admin extends User {
    
}
