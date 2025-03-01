package com.flawlesscoders.ambigu.modules.user.waiter.DTO;

import com.flawlesscoders.ambigu.modules.user.waiter.Shift;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class GetWaiterTableDTO {
    private String name;
    private String lastname_p;
    private Shift shift;
}
