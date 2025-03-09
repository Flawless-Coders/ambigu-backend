package com.flawlesscoders.ambigu.modules.workplan.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AssignmentDTO {
    private String table;
    private List<GetWaiterTableDTO> waiters;
}
