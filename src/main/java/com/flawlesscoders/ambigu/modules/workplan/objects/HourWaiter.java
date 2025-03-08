package com.flawlesscoders.ambigu.modules.workplan.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HourWaiter {
    private int hour;
    private int minute;
    private int second;

    // Convierte la hora a segundos desde la medianoche
    public int toSeconds() {
        return hour * 3600 + minute * 60 + second;
    }

    // Crea una instancia de HourWaiter a partir de segundos
    public static HourWaiter fromSeconds(int totalSeconds) {
        int hour = totalSeconds / 3600;
        int remainingSeconds = totalSeconds % 3600;
        int minute = remainingSeconds / 60;
        int second = remainingSeconds % 60;
        return new HourWaiter(hour, minute, second);
    }

    // Valida que la hora sea válida
    public static void validateHora(HourWaiter hora) {
        if (hora.getHour() < 0 || hora.getHour() > 23) {
            throw new IllegalArgumentException("Hora inválida: hour debe estar entre 0 y 23");
        }
        if (hora.getMinute() < 0 || hora.getMinute() > 59) {
            throw new IllegalArgumentException("Minuto inválido: minute debe estar entre 0 y 59");
        }
        if (hora.getSecond() < 0 || hora.getSecond() > 59) {
            throw new IllegalArgumentException("Segundo inválido: second debe estar entre 0 y 59");
        }
    }
}