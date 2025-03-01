package com.flawlesscoders.ambigu.modules.table.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TableWithWaiterDTO {
    private String tableName;
    private String lastWaiterName;
}
