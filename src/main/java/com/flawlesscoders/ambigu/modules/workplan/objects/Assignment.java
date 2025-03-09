package com.flawlesscoders.ambigu.modules.workplan.objects;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Assignment {
    private String table;
    private ArrayList<WaiterWorkplan> waiterWorkplan;
}
