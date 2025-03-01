package com.flawlesscoders.ambigu.modules.table.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TableWithWaiterDTO {
    private String id;
    private String tableIdentifier;
    private String lastWaiterName;
    private boolean tableWaiter;
    private String workplanId;
}
