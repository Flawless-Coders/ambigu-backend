package com.flawlesscoders.ambigu.modules.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderFeedbackDTO {
    
    private Integer qualification;

    private String comment;
}
