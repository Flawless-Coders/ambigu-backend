package com.flawlesscoders.ambigu.modules.user.admin.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdatePasswordDTO {
    private String id;
    private String currentPassword;
    private String newPassword;
}
