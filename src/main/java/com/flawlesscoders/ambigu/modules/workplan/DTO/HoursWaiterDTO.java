package com.flawlesscoders.ambigu.modules.workplan.DTO;

import com.flawlesscoders.ambigu.modules.workplan.objects.HourWaiter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoursWaiterDTO {
    private HourWaiter horaInicio; // Nuevo horario de inicio
    private HourWaiter horaFin; 
}
