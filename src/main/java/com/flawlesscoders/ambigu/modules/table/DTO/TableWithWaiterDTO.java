package com.flawlesscoders.ambigu.modules.table.DTO;

import com.flawlesscoders.ambigu.modules.table.TableClientStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TableWithWaiterDTO {
    private String id;
    private String tableIdentifier;
    private String lastWaiterName;
    private String lastname_p;
    private boolean tableWaiter;
    private boolean isEnabled;
    private String workplanId;
    private TableClientStatus tableClientStatus;
}
