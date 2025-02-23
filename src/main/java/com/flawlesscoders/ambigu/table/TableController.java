package com.flawlesscoders.ambigu.table;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tables")
public class TableController {

    private final TableService tableService;

    @Operation(summary = "Guardar una nueva mesa", description = "Registra una nueva mesa en la base de datos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mesa guardada correctamente",
                     content = { @Content(mediaType = "application/json", 
                                          schema = @Schema(implementation = Table.class)) }),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })
    @PostMapping()
    public ResponseEntity<Table> save(@Validated @RequestBody Table table) {
        return ResponseEntity.ok(tableService.saveTable(table));
    }

    @Operation(summary = "Obtener todas las mesas", description = "Retorna una lista de todas las mesas registradas en la base de datos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente",
                     content = { @Content(mediaType = "application/json", 
                                          schema = @Schema(implementation = Table.class)) }),
        @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })
    @GetMapping("")
    public ResponseEntity<List<Table>> findAll() {
        return ResponseEntity.ok(tableService.findAllTables());
    }

    @Operation(summary = "Buscar una mesa por ID", description = "Retorna una mesa específica mediante su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mesa encontrada",
                     content = { @Content(mediaType = "application/json", 
                                          schema = @Schema(implementation = Table.class)) }),
        @ApiResponse(responseCode = "404", description = "Mesa no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Table> findById(@PathVariable String id) {
        return ResponseEntity.ok(tableService.findById(id));
    }

    @Operation(summary = "Actualizar una mesa", description = "Actualiza la información de una mesa existente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mesa actualizada correctamente",
                     content = { @Content(mediaType = "application/json", 
                                          schema = @Schema(implementation = Table.class)) }),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Mesa no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })
    @PutMapping()
    public ResponseEntity<Table> update(@Validated @RequestBody Table table) {
        return ResponseEntity.ok(tableService.updateTable(table));
    }

    @Operation(summary = "Habilitar o deshabilitar una mesa", description = "Cambia el estado de una mesa entre habilitada o deshabilitada.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado de la mesa actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "No se puede cambiar el estado de una mesa ocupada"),
        @ApiResponse(responseCode = "404", description = "Mesa no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })
    @PutMapping("/{id}/tableIsEnable")
    public ResponseEntity<String> changeStatusTable(@PathVariable String id) {
        return ResponseEntity.ok(tableService.changeStatusTable(id));
    }

    @Operation(summary = "Obtener todas las mesas habilitadas", description = "Devuelve una lista de mesas que están habilitadas para su uso.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de mesas habilitadas obtenida correctamente",
                     content = { @Content(mediaType = "application/json", 
                                          schema = @Schema(implementation = Table.class)) }),
        @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })
    @GetMapping("/getEnabledTables")
    public ResponseEntity<List<Table>> getEnabledTables() {
        return ResponseEntity.ok(tableService.getEnabledTables());
    }

    @Operation(summary = "Obtener todas las mesas deshabilitadas", description = "Devuelve una lista de mesas que están deshabilitadas.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de mesas deshabilitadas obtenida correctamente",
                     content = { @Content(mediaType = "application/json", 
                                          schema = @Schema(implementation = Table.class)) }),
        @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })
    @GetMapping("/getDisabledTables")
    public ResponseEntity<List<Table>> getDisabledTables() {
        return ResponseEntity.ok(tableService.getDisabledTables());
    }

    @Operation(summary = "Obtener mesas habilitadas sin mesero", description = "Devuelve todas las mesas habilitadas pero sin mesero asignado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente",
                    content = { @Content(mediaType = "application/json", 
                                        schema = @Schema(implementation = Table.class)) }),
        @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })
    @GetMapping("/enabledWithoutWaiter")
    public ResponseEntity<List<Table>> getEnabledTablesWithoutWaiter() {
        return ResponseEntity.ok(tableService.getEnabledTablesWithoutWaiter());
    }

    @Operation(summary = "Obtener mesas habilitadas con meseros", description = "Devuelve todas las mesas habilitadas con mesero asignado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente",
                    content = { @Content(mediaType = "application/json", 
                                        schema = @Schema(implementation = Table.class)) }),
        @ApiResponse(responseCode = "500", description = "Error en el servidor")
    })
    @GetMapping("/enabledWithWaiter")
    public ResponseEntity<List<Table>> getEnabledTablesWithWaiter() {
        return ResponseEntity.ok(tableService.getEnabledTablesWithWaiter());
    }

}