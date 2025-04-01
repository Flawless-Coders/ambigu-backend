package com.flawlesscoders.ambigu.modules.order.dto;

import lombok.Data;

@Data
public class DailyOrderCount {
    private String id; // YYYY-MM-DD
    private int count;
}