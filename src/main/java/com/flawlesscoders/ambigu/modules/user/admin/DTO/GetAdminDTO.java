package com.flawlesscoders.ambigu.modules.user.admin.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetAdminDTO {
    private String id;
    private String name;
    private String lastname_p;
    private String lastname_m;
    private String email;
    private String avatarBase64;
}
