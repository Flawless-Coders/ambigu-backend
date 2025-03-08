package com.flawlesscoders.ambigu.modules.workplan.historyDisabled;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.flawlesscoders.ambigu.modules.workplan.Workplan;
import com.flawlesscoders.ambigu.modules.workplan.WorkplanRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class DisabledTableHistoryService {
    private final WorkplanRepository workplanRepository;
    private final DisabledTableHistoryRepository disabledTableHistoryRepository;

    public List<DisabledTableHistory> getDisabledTablesInAWorkplan(String workplanId) {
        try {
            // Verificar si el Workplan existe
            Workplan workplan = workplanRepository.findById(workplanId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el Workplan con ID: " + workplanId));
    
            // Obtener las mesas desactivadas en el historial que pertenecen al Workplan
            List<DisabledTableHistory> disabledTablesHistory = disabledTableHistoryRepository.findByWorkplanId(workplan.getId());
    
            if (disabledTablesHistory.isEmpty()) {
                return null;
            }
    
            return disabledTablesHistory;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al obtener las mesas deshabilitadas en el Workplan");
        }
    }

    
}
