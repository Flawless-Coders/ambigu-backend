package com.flawlesscoders.ambigu.modules.user.waiter.DTO;

import com.flawlesscoders.ambigu.modules.user.waiter.Shift;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetWaiterDTO {
    private String id;
    private String name;
    private String lastname_p;
    private String lastname_m;
    private String email;
    private String phone;
    private boolean isLeader;
    private boolean status;
    private float avgRating;
}
