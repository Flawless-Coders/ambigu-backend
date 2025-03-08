package com.flawlesscoders.ambigu.modules.workplan.historyDisabled;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "disabled_tables_history")
public class DisabledTableHistory {
    private String id;
    private String tableIdentifier;
    private boolean isTableWaiter;
    private boolean isEnabled;
    private String workplanId; 
}
