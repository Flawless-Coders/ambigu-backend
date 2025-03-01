package com.flawlesscoders.ambigu.modules.workplan;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Assigment {
    private String table;
    private ArrayList<String> waiters;
}
