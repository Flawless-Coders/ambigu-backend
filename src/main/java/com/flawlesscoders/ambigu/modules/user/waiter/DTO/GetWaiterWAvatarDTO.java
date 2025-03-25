package com.flawlesscoders.ambigu.modules.user.waiter.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetWaiterWAvatarDTO {
    private String id;
    private String name;
    private String lastname_p;
    private String lastname_m;
    private String email;
    private String avatarBase64;
    private String phone;
    private boolean isLeader;
    private float avgRating;
}