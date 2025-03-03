package com.flawlesscoders.ambigu.modules.workplan;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.flawlesscoders.ambigu.modules.table.Table;
import com.flawlesscoders.ambigu.modules.table.DTO.TableWithWaiterDTO;
import com.flawlesscoders.ambigu.modules.workplan.DTO.AssignmentDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workplan")
public class WorkplanController {
    public final WorkplanService workplanService;

    @Operation(summary = "Crea el plan de trabajo", description = "Registra un nuevo plan de trabajo para posterior poder ir registrando las asignaciones.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Plan de trabajo creado correctamente",
                     content = { @Content(mediaType = "application/json", 
                                          schema = @Schema(implementation = Workplan.class)) }),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })

    @PostMapping()
    public ResponseEntity<Workplan> initializeWorkplan(@Validated @RequestBody Workplan workplan) {
        return ResponseEntity.ok(workplanService.initializeWorkplan(workplan.getName()));
    }

    @Operation(summary = "Agrega asignaciones al plan de trabajo", description = "Registra las asignaciones de mesas a los meseros en el plan de trabajo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Asignación agregada correctamente",
                     content = { @Content(mediaType = "application/json", 
                                          schema = @Schema(implementation = Workplan.class)) }),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })
    @PutMapping("/{workplanId}/assignments/{tableId}/{waiterId}")
    public ResponseEntity<Workplan> addAssignment(
            @PathVariable String workplanId,
            @PathVariable String tableId,
            @PathVariable String waiterId) {
        Workplan updatedWorkplan = workplanService.addAssignmentToWorkplan(workplanId, tableId, waiterId);
        return ResponseEntity.ok(updatedWorkplan);
    }

    @Operation(summary = "Obtener todos los planes de trabajo en búsqueda", description = "Retorna una lista de todos los planes de trabajo en búsqueda")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente",
                     content = { @Content(mediaType = "application/json", 
                                          schema = @Schema(implementation = Table.class)) }),
        @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })
    @GetMapping("/AllButInSearch")
    public ResponseEntity<List<Workplan>> findAllWorkplansToSearch(){
        return ResponseEntity.ok(workplanService.findAllWorkplansToSearch());
    }

    @Operation(summary = "Obtener todos los planes de trabajo", description = "Retorna una lista de todos los planes de trabajo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente",
                     content = { @Content(mediaType = "application/json", 
                                          schema = @Schema(implementation = Table.class)) }),
        @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })
    @GetMapping()
    public ResponseEntity<List<Workplan>> findAllWorkplans(){
        return ResponseEntity.ok(workplanService.findAllWorkplans());
    }

    @Operation(summary = "Finalizar el workplan", description = "El líder de mesero puede finalizar el plan, liberando todas las mesas de sus meseros y permitiendo crear otro workplan")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Plan de trabajo finalizado correctamente"),
        @ApiResponse(responseCode = "400", description = "No hay un plan de trabajo activo"),
        @ApiResponse(responseCode = "404", description = "Plan de trabajo no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })
    @PutMapping("/finalize")
    public ResponseEntity<Boolean> killPresentWorkplan(){
        return ResponseEntity.ok(workplanService.killPresentWorkplan());
    }

    @Operation(summary = "Quitar mesero de una mesa sin cliente", description = "Quitar un mesero de una mesa y verifica que no tenga un cliente en curso")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado de la mesa actualizado, mesa sin mesero"),
        @ApiResponse(responseCode = "400", description = "No se puede quitar el mesero de esta mesa porque esta ocupada por un cliente"),
        @ApiResponse(responseCode = "404", description = "Mesa no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })
    @PutMapping("{workplanId}/removeWaiter/{tableId}")
    public ResponseEntity<String> removeWaiterToTable(@PathVariable String workplanId, @PathVariable String tableId) {
        return ResponseEntity.ok(workplanService.removeWaiterToTable(workplanId, tableId));
    }

    @Operation(summary = "Seleccionar como favorito o quitar", description = "Agregar o quitar plan de trabajo a favoritos por su id de workplan")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Plan de trabajo cambiado de favoritos correctamente"),
        @ApiResponse(responseCode = "400", description = "Error al hacer el cambio de favoritos"),
        @ApiResponse(responseCode = "404", description = "Plan de trabajo no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })
    @PutMapping("/changeFavorite/{workplanId}")
    public ResponseEntity<String> changeFavorite(@PathVariable String workplanId){
        return ResponseEntity.ok(workplanService.changeFavorite(workplanId));
    }

    @Operation(summary = "Obtener todos los planes de trabajo favoritos", description = "Retorna una lista de todos los planes de trabajo favoritos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente"),
        @ApiResponse(responseCode = "400", description = "Error al agregar la asignación"),
        @ApiResponse(responseCode = "404", description = "Plan de trabajo no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })
    @GetMapping("/getFavoriteWorkplans")
    public ResponseEntity<List<Workplan>> findAllFavorites() {
        return ResponseEntity.ok(workplanService.findAllFavorites());
    }

    @Operation(summary = "Obtener detalles de cada mesa en un plan de trabajo", description = "Se muestra el plan de trabajo específico por id de mesa")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Detalles de mesa obtenidos correctamente"),
        @ApiResponse(responseCode = "400", description = "Error al obtener detalles de mesa en plan de trabajo"),
        @ApiResponse(responseCode = "404", description = "Mesa no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })
    @GetMapping("/{workplanId}/table/{tableId}")
    public ResponseEntity<AssignmentDTO> detailsTableInAWorkplanDTO(@PathVariable String workplanId, @PathVariable String tableId) {
        return ResponseEntity.ok(workplanService.detailsTableInAWorkplanDTO(workplanId, tableId));
    }

    @Operation(summary = "Cambiar mesero a una mesa", description = "El líder de mesero puede cambiar al mesero de una mesa")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mesero cambiado correctamente"),
        @ApiResponse(responseCode = "400", description = "Error al cambiar el mesero de la mesa"),
        @ApiResponse(responseCode = "404", description = "Mesa no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })
    @PutMapping("/{workplanId}/changeWaiter/{tableId}/{waiterId}")
    public ResponseEntity<String> changeWaiterToTable(@PathVariable String workplanId, @PathVariable String tableId, @PathVariable String waiterId) {
        return ResponseEntity.ok(workplanService.changeWaiterToTable(workplanId, tableId, waiterId));
    }

    @Operation(summary = "Lista de mesas con meseros asignados en un plan activo", description = "Obtener la lista de mesas en un plan activo con su ultimo mesero asignado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente"),
        @ApiResponse(responseCode = "400", description = "Error al obtener la lista de mesas"),
        @ApiResponse(responseCode = "404", description = "No hay mesas asignadas en un plan activo"),
        @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })
    @GetMapping("/getEnabledTablesWithLastWaiter")
    public ResponseEntity<List<TableWithWaiterDTO>> getEnabledTablesWithLastWaiter(){
        return ResponseEntity.ok(workplanService.getEnabledTablesWithLastWaiter());
    }

    @Operation(summary = "Obtener plan de trabajo por id", description = "Se muestra el plan de trabajo por id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Plan de trabajo obtenido correctamente"),
        @ApiResponse(responseCode = "400", description = "Error al obtener el plan de trabajo"),
        @ApiResponse(responseCode = "404", description = "Plan de trabajo no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })

    @GetMapping("/{workplanId}")
    public ResponseEntity<Workplan> findById(@PathVariable String workplanId) {
        return ResponseEntity.ok(workplanService.findById(workplanId));
    }

    @Operation(summary = "Eliminar plan de trabajo de la búsqueda", description = "Quitar un plan de trabajo de la búsqueda, seguirá en la bd")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Plan de trabajo eliminado de la búsqueda correctamente"),
        @ApiResponse(responseCode = "400", description = "Error al eliminar el plan de trabajo de la búsqueda"),
        @ApiResponse(responseCode = "404", description = "Plan de trabajo no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })
    @PutMapping("/{workplanId}")
    public ResponseEntity<Boolean> removeWorkplanToSearch(@PathVariable String workplanId) {
        return ResponseEntity.ok(workplanService.removeWorkplanToSearch(workplanId));
    }

    @Operation(summary = "Habilitar o deshabilitar una mesa en un workplan", description = "Habilitar o deshabilitar una mesa en un workplan activo ")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cambio de estado de la mesa correctamente"),
        @ApiResponse(responseCode = "400", description = "Error al cambiar el estado de la mesa"),
        @ApiResponse(responseCode = "404", description = "Mesa no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })
    @PutMapping("/disableTable/{tableId}")
    public ResponseEntity<String> changeStatusTableInAWorkplan(@PathVariable String tableId) {
        return ResponseEntity.ok(workplanService.changeStatusTableInAWorkplan(tableId));
    }

    @Operation(summary = "Obtener las mesas deshabilitadas solo del workplan", description = "Obtener las mesas deshabilitadas solo del workplan activo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de mesas deshabilitadas en workplan obtenida correctamente"),
        @ApiResponse(responseCode = "400", description = "Error al obtener la lista de mesas deshabilitadas"),
        @ApiResponse(responseCode = "404", description = "No hay mesas deshabilitadas en un plan activo"),
        @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })
    @GetMapping("/getDisabledTablesInAWorkplan")
    public ResponseEntity<List<Table>> getDisabledTables(){
        return ResponseEntity.ok(workplanService.getDisabledTablesInAWorkplan());
    }

    @Operation(summary = "Reutilizar un plan de trabajo en existencia", description = "Reutilizar un plan de trabajo en existencia")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Plan de trabajo reutilizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Error al reutilizar el plan de trabajo"),
        @ApiResponse(responseCode = "404", description = "Plan de trabajo no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })
    @PutMapping("/{workplanId}/restartWorkplan")
    public ResponseEntity<Boolean> restartWorkplan(@PathVariable String workplanId){
        return ResponseEntity.ok(workplanService.restartWorkplan(workplanId));
    }

    @Operation(summary = "Contador de mesas de un mesero en un workplan", description = "Contador de mesas de un mesero en un workplan activo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contador de mesas obtenido correctamente"),
        @ApiResponse(responseCode = "400", description = "Error al obtener el contador de mesas"),
        @ApiResponse(responseCode = "404", description = "No hay mesas asignadas a este mesero"),
        @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })
    @GetMapping("/{workplanId}/getCountTablesByWaiterInWorkplan/{waiterId}")
    public ResponseEntity<Integer> getCountTablesByWaiterInWorkplan(@PathVariable String workplanId, @PathVariable String waiterId){
        return ResponseEntity.ok(workplanService. countTablesByWaiterInWorkplan(workplanId, waiterId));
    }
   
    @Operation(summary = "Obtener el id del workplan en curso", description = "Obtener el id del workplan en curso")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Id del plan de trabajo obtenido correctamente"),
        @ApiResponse(responseCode = "400", description = "Error al obtener el id del plan de trabajo"),
        @ApiResponse(responseCode = "404", description = "No hay plan de trabajo activo"),
        @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })
    @GetMapping("/currentWorkplan")
    public ResponseEntity<String> getIdWorkplanPresent(){
        return ResponseEntity.ok(workplanService.getIdWorkplanPresent());
    }

    @Operation(summary = "Obtener mesas de un mesero por su email", description = "Obtener las mesas de un mesero por su email en un plan de trabajo activo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de mesas obtenida correctamente"),
        @ApiResponse(responseCode = "400", description = "Error al obtener la lista de mesas"),
        @ApiResponse(responseCode = "404", description = "No hay mesas asignadas a este mesero"),
        @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })
    @GetMapping("/getTablesInChargeByWaiterInWorkplan/{waiterEmail}")
    public ResponseEntity<List<Table>> getTablesInChargeByWaiterInWorkplan(@PathVariable String waiterEmail){
        return ResponseEntity.ok(workplanService.getTablesInChargeByWaiterInWorkplan(waiterEmail));
    }
}
