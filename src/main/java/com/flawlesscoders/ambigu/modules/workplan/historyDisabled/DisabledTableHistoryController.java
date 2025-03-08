package com.flawlesscoders.ambigu.modules.workplan.historyDisabled;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/disabledTableHistory")
public class DisabledTableHistoryController {
    public final DisabledTableHistoryService disabledTableHistoryService;

    @Operation(summary = "Obtener las mesas deshabilitadas solo del workplan", description = "Obtener las mesas deshabilitadas solo del workplan activo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de mesas deshabilitadas en workplan obtenida correctamente"),
        @ApiResponse(responseCode = "400", description = "Error al obtener la lista de mesas deshabilitadas"),
        @ApiResponse(responseCode = "404", description = "No hay mesas deshabilitadas en un plan activo"),
        @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })
    @GetMapping("/getDisabledTablesInAWorkplan/{workplanId}")
    public ResponseEntity<List<DisabledTableHistory>> findAllDisabledTableInAworkplan(@PathVariable String workplanId){
        return ResponseEntity.ok(disabledTableHistoryService.getDisabledTablesInAWorkplan(workplanId));
    }
}
