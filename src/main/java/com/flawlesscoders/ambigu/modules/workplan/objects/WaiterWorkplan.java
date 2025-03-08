package com.flawlesscoders.ambigu.modules.workplan.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WaiterWorkplan {
    public String waiter;
    public HourWaiter horaInicio;
    public HourWaiter horaFin;
}
