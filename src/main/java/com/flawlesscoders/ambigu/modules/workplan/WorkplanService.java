package com.flawlesscoders.ambigu.modules.workplan;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.flawlesscoders.ambigu.modules.table.Table;
import com.flawlesscoders.ambigu.modules.table.TableClientStatus;
import com.flawlesscoders.ambigu.modules.table.TableRepository;
import com.flawlesscoders.ambigu.modules.table.TableService;
import com.flawlesscoders.ambigu.modules.table.DTO.TableWithWaiterDTO;
import com.flawlesscoders.ambigu.modules.user.waiter.Waiter;
import com.flawlesscoders.ambigu.modules.user.waiter.WaiterRepository;
import com.flawlesscoders.ambigu.modules.user.waiter.WaiterService;
import com.flawlesscoders.ambigu.modules.user.waiter.DTO.GetWaiterWAvatarDTO;
import com.flawlesscoders.ambigu.modules.workplan.DTO.AssignmentDTO;
import com.flawlesscoders.ambigu.modules.workplan.DTO.GetWaiterTableDTO;
import com.flawlesscoders.ambigu.modules.workplan.historyDisabled.DisabledTableHistory;
import com.flawlesscoders.ambigu.modules.workplan.historyDisabled.DisabledTableHistoryRepository;
import com.flawlesscoders.ambigu.modules.workplan.objects.Assignment;
import com.flawlesscoders.ambigu.modules.workplan.objects.HourWaiter;
import com.flawlesscoders.ambigu.modules.workplan.objects.WaiterWorkplan;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkplanService {
    private final WorkplanRepository workplanRepository;
    private final TableRepository tableRepository;
    private final TableService tableService;
    private final WaiterRepository waiterRepository;
    private final DisabledTableHistoryRepository disabledTableHistoryRepository;
    private final WaiterService waiterService;
    private static final Logger logger = LoggerFactory.getLogger(WorkplanService.class);


    //method to initialize a workplan
     public Workplan initializeWorkplan(String workplanName) {
        try {
            if (workplanRepository.existsByIsPresent(true)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un plan en curso");
            }

            Workplan workplan = new Workplan();
            workplan.setPresent(true);
            workplan.setFavorite(false);
            workplan.setDate(new Date());
            workplan.setExisting(true);
            workplan.setAssigment(new ArrayList<>()); 

            if (workplanName == null || workplanName.trim().isEmpty()) {
                workplan.setName(formatDate(workplan.getDate()) + "-PT");
            } else {
                workplan.setName(workplanName);
            }

            return workplanRepository.save(workplan);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al inicializar el plan de trabajo");
        }
    }

    //method to generated name to a workplan
    private String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(date);
    }

    // Método para agregar una asignación a un Workplan activo
    public Workplan addAssignmentToWorkplan(String workplanId, String tableId, WaiterWorkplan waiterWorkplan) {
        try {
            // Validar las horas de inicio y fin
            HourWaiter.validateHora(waiterWorkplan.getHoraInicio());
            HourWaiter.validateHora(waiterWorkplan.getHoraFin());
    
            // Buscar el plan de trabajo
            Workplan workplan = workplanRepository.findById(workplanId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan de trabajo no encontrado"));
    
            if (!workplan.isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay un plan de trabajo activo");
            }
    
            // Buscar la mesa
            Table table = tableRepository.findById(tableId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa no encontrada"));
    
            if (!table.isEnabled()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al asignar mesero, la mesa está deshabilitada");
            }
    
            // Buscar si ya existe una asignación para esta mesa
            Optional<Assignment> existingAssignmentOpt = workplan.getAssigment().stream()
                    .filter(a -> a.getTable().equals(tableId))
                    .findFirst();
    
            Assignment assignment;
            if (existingAssignmentOpt.isPresent()) {
                // Si ya existe una asignación, usarla
                assignment = existingAssignmentOpt.get();
    
                // Validar que el mesero no esté ya asignado a esta mesa
                boolean isWaiterAlreadyAssigned = assignment.getWaiterWorkplan().stream()
                        .anyMatch(w -> w.getWaiter().equals(waiterWorkplan.getWaiter()));
    
                if (isWaiterAlreadyAssigned) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El mesero ya está asignado a esta mesa");
                }
    
                // Validar que las horas no se solapen con las asignaciones existentes
                for (WaiterWorkplan existingWaiterWorkplan : assignment.getWaiterWorkplan()) {
                    if (isOverlap(existingWaiterWorkplan, waiterWorkplan)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Las horas se solapan con otra asignación");
                    }
                }
            } else {
                // Si no existe una asignación, crear una nueva
                assignment = new Assignment();
                assignment.setTable(tableId);
                assignment.setWaiterWorkplan(new ArrayList<>());
                workplan.getAssigment().add(assignment);
            }
    
            // Agregar el nuevo mesero a la lista de meseros de la asignación
            assignment.getWaiterWorkplan().add(waiterWorkplan);
    
            // Marcar la mesa como asignada a un mesero
            table.setTableWaiter(true);
            tableRepository.save(table);
    
            // Guardar el plan de trabajo actualizado
            return workplanRepository.save(workplan);
        } catch (IllegalArgumentException e) {
            // Capturar excepciones de validación
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            // Capturar otras excepciones
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al agregar la asignación");
        }
    }
    
    // Método para verificar si dos asignaciones se solapan
    private boolean isOverlap(WaiterWorkplan existing, WaiterWorkplan newWaiterWorkplan) {
        int existingStart = existing.getHoraInicio().toSeconds();
        int existingEnd = existing.getHoraFin().toSeconds();
        int newStart = newWaiterWorkplan.getHoraInicio().toSeconds();
        int newEnd = newWaiterWorkplan.getHoraFin().toSeconds();
    
        // Verificar si las horas se solapan
        return !(newEnd <= existingStart || newStart >= existingEnd);
    }

    //method to get all workplans IN SEARCH (existing)
    public List<Workplan> findAllWorkplansToSearch(){
        try {
            List<Workplan> existingWorkplans = workplanRepository.findByExistingTrue();

            if (existingWorkplans.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay planes de trabajo activos");
            }
            return existingWorkplans;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al obtener planes de trabajo");
        }
    }

    // Método optimizado para obtener mesas de un Workplan
    public List<Table> getTablesByWorkplan(String workplanId) {
        try {
            Workplan workplan = workplanRepository.findById(workplanId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan de trabajo no encontrado"));

            return workplan.getAssigment().stream()
                    .map(assignment -> tableRepository.findById(assignment.getTable()).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error al obtener mesas del Workplan", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al obtener las mesas del WorkPlan");
        }
    }
    
    
    // //method to change waiter of a table
    public String changeWaiterToTable(String workplanId, String tableId, String waiterId) {
        try {
            // Obtener la hora actual del sistema
            LocalTime currentTime = LocalTime.now();
    
            // Buscar el plan de trabajo
            Workplan workplan = workplanRepository.findById(workplanId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan de trabajo no encontrado"));
    
            if (!workplan.isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay un plan de trabajo activo");
            }
    
            // Buscar la mesa
            Table table = tableRepository.findById(tableId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa no encontrada"));
    
            if (!table.isTableWaiter()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La mesa no tiene un mesero asignado");
            }
    
            // Buscar la asignación que corresponde a la mesa especificada
            Assignment assignment = workplan.getAssigment().stream()
                    .filter(a -> a.getTable().equals(tableId))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa no encontrada en este plan de trabajo"));
    
            // Buscar el mesero actual asignado a la mesa en este momento
            Optional<WaiterWorkplan> currentWaiterOpt = assignment.getWaiterWorkplan().stream()
                    .filter(waiterWorkplan -> {
                        LocalTime startTime = LocalTime.of(
                                waiterWorkplan.getHoraInicio().getHour(),
                                waiterWorkplan.getHoraInicio().getMinute(),
                                waiterWorkplan.getHoraInicio().getSecond()
                        );
                        LocalTime endTime = LocalTime.of(
                                waiterWorkplan.getHoraFin().getHour(),
                                waiterWorkplan.getHoraFin().getMinute(),
                                waiterWorkplan.getHoraFin().getSecond()
                        );
                        // Verificar si la hora actual está dentro del intervalo
                        return !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime);
                    })
                    .findFirst();
    
            if (currentWaiterOpt.isPresent()) {
                // Obtener el mesero actual
                WaiterWorkplan currentWaiter = currentWaiterOpt.get();
    
                // Reemplazar al mesero actual con el nuevo mesero
                currentWaiter.setWaiter(waiterId);
    
                // Guardar los cambios en la base de datos
                workplanRepository.save(workplan);
    
                return "Se cambió el mesero exitosamente";
            } else {
                // No hay un mesero asignado en este momento
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay un mesero asignado a esta mesa en este momento");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al cambiar el mesero de la mesa");
        }
    }

    // //method to get all tables with their current waiter hour active 
    public List<TableWithWaiterDTO> getEnabledTablesWithLastWaiter() {
    try {
        // Obtener la hora actual del sistema
        LocalTime currentTime = LocalTime.now();

        // Obtener todas las mesas habilitadas
        List<Table> enabledTables = tableService.getEnabledTablesWithWaiter();

        // Buscar el plan de trabajo activo
        Workplan workplan = workplanRepository.findByIsPresent(true)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay un plan de trabajo activo"));

        // Filtrar asignaciones solo de mesas habilitadas y mapear al DTO
        List<TableWithWaiterDTO> result = enabledTables.stream()
                .map(table -> {
                    // Buscar la asignación correspondiente en el Workplan
                    Optional<Assignment> assignmentOpt = workplan.getAssigment().stream()
                            .filter(a -> a.getTable().equals(table.getId()))
                            .findFirst();

                    if (assignmentOpt.isPresent()) {
                        Assignment assignment = assignmentOpt.get();

                        // Buscar el mesero activo en este momento
                        Optional<WaiterWorkplan> activeWaiterOpt = assignment.getWaiterWorkplan().stream()
                                .filter(waiterWorkplan -> {
                                    LocalTime startTime = LocalTime.of(
                                            waiterWorkplan.getHoraInicio().getHour(),
                                            waiterWorkplan.getHoraInicio().getMinute(),
                                            waiterWorkplan.getHoraInicio().getSecond()
                                    );
                                    LocalTime endTime = LocalTime.of(
                                            waiterWorkplan.getHoraFin().getHour(),
                                            waiterWorkplan.getHoraFin().getMinute(),
                                            waiterWorkplan.getHoraFin().getSecond()
                                    );
                                    // Verificar si la hora actual está dentro del intervalo
                                    return !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime);
                                })
                                .findFirst();

                        if (activeWaiterOpt.isPresent()) {
                            // Obtener el mesero activo
                            WaiterWorkplan activeWaiter = activeWaiterOpt.get();

                            // Buscar el mesero en la base de datos
                            Waiter waiter = waiterRepository.findById(activeWaiter.getWaiter())
                                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesero no encontrado"));

                            // Crear y devolver el DTO
                            return new TableWithWaiterDTO(
                                    table.getId(),
                                    table.getTableIdentifier(),
                                    waiter.getName(),
                                    waiter.getLastname_p(),
                                    table.isTableWaiter(),
                                    table.isEnabled(),
                                    workplan.getId(),
                                    table.getTableClientStatus()
                            );
                        } else {
                            // No hay mesero activo en este momento
                            return new TableWithWaiterDTO(
                                    table.getId(),
                                    table.getTableIdentifier(),
                                    "Sin turno",
                                    "",
                                    table.isTableWaiter(),
                                    table.isEnabled(),
                                    workplan.getId(),
                                    table.getTableClientStatus()
                            );
                        }
                    }
                    return null; // No tiene asignación en el Workplan
                })
                .filter(Objects::nonNull) // Eliminar valores nulos
                .collect(Collectors.toList());

        return result;
    } catch (Exception e) {
        e.printStackTrace();
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al obtener las mesas con su mesero activo");
    }
}

    //method to get all workplans, including the ones that are not in search
    public List<Workplan> findAllWorkplans() {
        try {
            if (workplanRepository.findAll().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay planes de trabajo");
            } else {
                return workplanRepository.findAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay planes de trabajo");
        }
    }

    // //method to finalize a workplan
    public boolean killPresentWorkplan() {
        try {
            // Buscar el plan de trabajo en curso
            Workplan workplan = workplanRepository.findByIsPresent(true)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay plan en curso"));
    
            // Iterar sobre las asignaciones para desasociar las mesas
            for (Assignment assignment : workplan.getAssigment()) {
                Table foundTable = tableRepository.findById(assignment.getTable())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa no encontrada"));
    
                // Marcar la mesa como disponible
                foundTable.setTableWaiter(false);
                tableRepository.save(foundTable); // Guardar el cambio en la mesa
            }
    
            // Marcar el Workplan como inactivo
            workplan.setPresent(false);
            workplanRepository.save(workplan);
    
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al finalizar el plan de trabajo");
        }
    }
    

    // //method to change favorite status of a workplan
    public String changeFavorite(String workplanId) {
        try {
            Workplan workplan = workplanRepository.findById(workplanId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan de trabajo no encontrado"));
                    if(workplan.isFavorite()){
                        workplan.setFavorite(false);
                    }else{
                        workplan.setFavorite(true);
                    }
                    workplanRepository.save(workplan);
            return "Favorito cambiado correctamente";
        } catch (Exception e) {
           e.printStackTrace();
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Error al cambiar favorito");
        }
    }

    // //method to get all favorite workplans
    public List<Workplan> findAllFavorites() {
        try {
            List<Workplan> favoriteWorkplans = workplanRepository.findByFavoriteWorkplans();
    
            if (favoriteWorkplans.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay planes de trabajo favoritos");
            }
            return favoriteWorkplans;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al obtener planes de trabajo favoritos");
        }
    }
    
    //method to find a workplan by id
    public Workplan findById(String id) {
        try {
            return workplanRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan de trabajo no encontrado"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al obtener el plan de trabajo");
        }
    }

    //method to get all waiters in a table on a workplan
    public AssignmentDTO detailsTableInAWorkplanDTO(String workplanId, String tableId) {
        try {
            // Buscar el plan de trabajo
            Workplan workplan = workplanRepository.findById(workplanId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan de trabajo no encontrado"));
    
            // Buscar la asignación que corresponde a la mesa especificada
            Assignment assignment = workplan.getAssigment().stream()
                    .filter(a -> a.getTable().equals(tableId))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa no encontrada en este plan de trabajo"));
    
            // Buscar los detalles de la mesa
            Table table = tableRepository.findById(assignment.getTable())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa no encontrada"));
    
            // Validar la lista de WaiterWorkplan
            List<WaiterWorkplan> waiterWorkplans = assignment.getWaiterWorkplan();
            if (waiterWorkplans == null || waiterWorkplans.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay meseros asignados a esta mesa");
            }
    
            // Mapear los WaiterWorkplan a DTOs
            List<GetWaiterTableDTO> waiters = waiterWorkplans.stream()
                    .map(waiterWorkplan -> {
                        // Buscar el mesero correspondiente
                        Waiter waiter = waiterRepository.findById(waiterWorkplan.getWaiter())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesero no encontrado"));
    
                        // Formatear las horas de inicio y fin
                        String horaInicio = String.format("%02d:%02d",
                                waiterWorkplan.getHoraInicio().getHour(),
                                waiterWorkplan.getHoraInicio().getMinute(),
                                waiterWorkplan.getHoraInicio().getSecond());
                        String horaFin = String.format("%02d:%02d",
                                waiterWorkplan.getHoraFin().getHour(),
                                waiterWorkplan.getHoraFin().getMinute(),
                                waiterWorkplan.getHoraFin().getSecond());
    
                        // Crear el DTO del mesero con sus horarios
                        return GetWaiterTableDTO.builder()
                                .waiterId(waiter.getId())
                                .name(waiter.getName())
                                .lastname_p(waiter.getLastname_p())
                                .horaInicio(horaInicio)
                                .horaFin(horaFin)
                                .build();
                    })
                    .collect(Collectors.toList());
    
            // Retornar el DTO con la lista de meseros y sus horarios
            return AssignmentDTO.builder()
                    .table(table.getTableIdentifier())
                    .waiters(waiters)
                    .build();
    
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al obtener los detalles de la mesa en el plan de trabajo");
        }
    }

    // //method to remove a workplan from search but in database still exists
    public boolean removeWorkplanToSearch(String workplanId) {
        try {
            Workplan workplan = workplanRepository.findById(workplanId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan de trabajo no encontrado"));
    
            workplan.setExisting(false);
            workplanRepository.save(workplan);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al quitar el plan de trabajo de la búsqueda");
        }
    }

    // //method to change status to a table in a worikplan active
    public String changeStatusTableInAWorkplan(String tableId) {
        try {
            // Verificar si hay un Workplan activo
            Workplan activeWorkplan = workplanRepository.findByIsPresent(true)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay un Workplan activo"));
    
            // Buscar la mesa
            Table table = tableRepository.findById(tableId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa no encontrada"));
    
            // No se puede deshabilitar si la mesa está ocupada
            if (table.getTableClientStatus() == TableClientStatus.OCCUPIED) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La mesa está ocupada, no se puede deshabilitar.");
            }
    
            // Cambiar el estado de la mesa
            boolean newStatus = !table.isEnabled();
            table.setEnabled(newStatus);
    
            // Si la mesa se está desactivando, eliminar las asignaciones de meseros y guardar en el historial
            if (!newStatus) {
                // Buscar la asignación correspondiente a la mesa
                Optional<Assignment> assignmentOpt = activeWorkplan.getAssigment().stream()
                        .filter(a -> a.getTable().equals(tableId))
                        .findFirst();
    
                if (assignmentOpt.isPresent()) {
                    Assignment assignment = assignmentOpt.get();
    
                    // Eliminar todas las asignaciones de meseros (WaiterWorkplan) para esta mesa
                    assignment.getWaiterWorkplan().clear();
    
                    // Guardar los cambios en el Workplan
                    workplanRepository.save(activeWorkplan);
                }
    
                // Guardar la mesa desactivada en el historial
                DisabledTableHistory disabledTableHistory = new DisabledTableHistory();
                disabledTableHistory.setId(table.getId());
                disabledTableHistory.setTableIdentifier(table.getTableIdentifier());
                disabledTableHistory.setTableWaiter(table.isTableWaiter());
                disabledTableHistory.setEnabled(table.isEnabled());
                disabledTableHistory.setWorkplanId(activeWorkplan.getId()); // ID del Workplan activo
    
                // Guardar en la colección de historial
                disabledTableHistoryRepository.save(disabledTableHistory);
            } else {
                // Si la mesa se está habilitando, eliminar del historial de mesas desactivadas
                disabledTableHistoryRepository.deleteById(tableId);
    
                // Establecer tableWaiter en false
                table.setTableWaiter(false);
    
                // Eliminar la asignación de la mesa del Workplan activo
                activeWorkplan.getAssigment().removeIf(a -> a.getTable().equals(tableId));
                workplanRepository.save(activeWorkplan);
            }
    
            // Guardar los cambios en la mesa
            tableRepository.save(table);
    
            return "Estado de la mesa cambiado exitosamente";
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al cambiar el estado de la mesa en el Workplan");
        }
    }

    // //method to get all disabled tables in a workplan
    //method in history...
    
    //method to reutilice a workplan to still existing
    public boolean restartWorkplan(String workplanId) {
        try {
            // Buscar el plan de trabajo
            Workplan workplan = workplanRepository.findById(workplanId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan de trabajo no encontrado"));
    
            // Obtener todas las asignaciones existentes
            List<Assignment> updatedAssignments = workplan.getAssigment().stream().map(assignment -> {
                // Buscar la mesa correspondiente
                Table table = tableRepository.findById(assignment.getTable())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa no encontrada"));
    
                // Verificar si hay meseros asignados previamente
                List<WaiterWorkplan> waiterWorkplans = assignment.getWaiterWorkplan();
                if (!waiterWorkplans.isEmpty()) {
                    // Crear una nueva asignación con la misma mesa y todos los meseros asignados
                    Assignment newAssignment = new Assignment();
                    newAssignment.setTable(table.getId());
                    newAssignment.setWaiterWorkplan(new ArrayList<>(waiterWorkplans)); // Conservar todos los waiterWorkplan
    
                    // Marcar la mesa como ocupada (si es necesario)
                    table.setTableWaiter(true);
                    tableRepository.save(table);
                    return newAssignment;
                } else {
                    // Si la mesa no tiene meseros previos, mantener la asignación vacía
                    Assignment newAssignment = new Assignment();
                    newAssignment.setTable(table.getId());
                    newAssignment.setWaiterWorkplan(new ArrayList<>());
                    return newAssignment;
                }
            }).collect(Collectors.toList());
    
            // Actualizar el Workplan con los nuevos assignments y reiniciarlo
            workplan.setAssigment(updatedAssignments);
            workplan.setPresent(true); // Reiniciar el estado del plan de trabajo
            workplanRepository.save(workplan);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al reiniciar el plan de trabajo");
        }
    }
    
    // //method to count the number of tables that one waiter has in a workplan
    public int countTablesByWaiterInWorkplan(String workplanId, String waiterId) {
        try {
            // Buscar el plan de trabajo
            Workplan workplan = workplanRepository.findById(workplanId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan de trabajo no encontrado"));
    
            // Contar todas las mesas donde el `waiterId` esté asignado
            long count = workplan.getAssigment().stream()
                    .filter(assignment -> {
                        // Verificar si el mesero está asignado a esta mesa
                        return assignment.getWaiterWorkplan().stream()
                                .anyMatch(waiterWorkplan -> waiterWorkplan.getWaiter().equals(waiterId));
                    })
                    .count();
    
            return (int) count;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al contar las mesas asignadas al mesero en el plan de trabajo");
        }
    }

    //method to get id of a workplan in curse
    public String getIdWorkplanPresent() {
        try {
            Workplan workplan = workplanRepository.findByIsPresent(true)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay plan de trabajo activo"));
    
            return workplan.getId();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al obtener el ID del plan de trabajo activo");
        }
    }
    
    //method to get all tables of one waiter by their email
    public List<Table> getTablesInChargeByWaiterInWorkplan(String waiterEmail) {
        try {
            // Buscar al mesero por email
            Waiter waiter = waiterRepository.findByEmail(waiterEmail)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesero no encontrado"));
    
            String waiterId = waiter.getId(); // Obtenemos el ID del mesero
    
            // Buscar el Workplan activo
            Workplan workplan = workplanRepository.findByIsPresent(true)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay un plan de trabajo activo"));
    
            // Obtener la hora actual
            LocalTime now = LocalTime.now();
    
            // Filtrar las mesas donde el `waiterId` está asignado en algún WaiterWorkplan y la hora actual está dentro del rango de horario
            List<Table> tables = workplan.getAssigment().stream()
                    .filter(assignment -> {
                        // Verificar si el mesero está asignado a esta mesa y la hora actual está dentro de su horario
                        return assignment.getWaiterWorkplan().stream()
                                .anyMatch(waiterWorkplan -> {
                                    // Comparar el ID del mesero
                                    if (!waiterWorkplan.getWaiter().equals(waiterId)) {
                                        return false;
                                    }
    
                                    // Convertir las horas de inicio y fin a LocalTime
                                    LocalTime horaInicio = LocalTime.of(
                                            waiterWorkplan.getHoraInicio().getHour(),
                                            waiterWorkplan.getHoraInicio().getMinute(),
                                            waiterWorkplan.getHoraInicio().getSecond()
                                    );
                                    LocalTime horaFin = LocalTime.of(
                                            waiterWorkplan.getHoraFin().getHour(),
                                            waiterWorkplan.getHoraFin().getMinute(),
                                            waiterWorkplan.getHoraFin().getSecond()
                                    );
    
                                    // Verificar si la hora actual está dentro del rango de horario
                                    return !now.isBefore(horaInicio) && !now.isAfter(horaFin);
                                });
                    })
                    .map(assignment -> tableRepository.findById(assignment.getTable())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa no encontrada")))
                    .collect(Collectors.toList());
    
            return tables;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al obtener las mesas asignadas al mesero en el plan de trabajo");
        }
    }

    //method to get all waiters who aren't assigned to a specific table
    public List<GetWaiterWAvatarDTO> getWaitersNotAssignedToTable(String tableId) {
        try {
            // Obtener todos los meseros activos usando WaiterService
            List<GetWaiterWAvatarDTO> allWaiters = waiterService.getWaitersWAvatar().getBody();
    
            if (allWaiters == null || allWaiters.isEmpty()) {
                return Collections.emptyList(); // No hay meseros activos
            }
    
            // Obtener el Workplan activo
            Workplan activeWorkplan = workplanRepository.findByIsPresent(true)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay un Workplan activo"));
    
            // Obtener la asignación de la mesa específica
            Optional<Assignment> assignmentOpt = activeWorkplan.getAssigment().stream()
                    .filter(a -> a.getTable().equals(tableId))
                    .findFirst();
    
            // Si no hay asignación para la mesa, todos los meseros están disponibles
            if (assignmentOpt.isEmpty()) {
                return allWaiters;
            }
    
            // Obtener los IDs de los meseros asignados a la mesa
            Assignment assignment = assignmentOpt.get();
            Set<String> assignedWaiterIds = assignment.getWaiterWorkplan().stream()
                    .map(WaiterWorkplan::getWaiter)
                    .collect(Collectors.toSet());
    
            // Filtrar los meseros que no están asignados a la mesa
            return allWaiters.stream()
                    .filter(waiter -> !assignedWaiterIds.contains(waiter.getId()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al obtener los meseros no asignados a la mesa");
        }
    }

    //method to update just the time to waiters in a specific table
    public WaiterWorkplan updateWaiterWorkplanHours(String workplanId, String tableId, String waiterId, WaiterWorkplan newHourWaiter) {
        try {
            // Validar la nueva hora
            HourWaiter.validateHora(newHourWaiter.getHoraInicio());
            HourWaiter.validateHora(newHourWaiter.getHoraFin());
    
            // Buscar el plan de trabajo
            Workplan workplan = workplanRepository.findById(workplanId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan de trabajo no encontrado"));
    
            // Buscar la mesa dentro del plan de trabajo
            Assignment assignment = workplan.getAssigment().stream()
                    .filter(a -> a.getTable().equals(tableId))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa no asignada en el plan de trabajo"));
    
            // Buscar el mesero dentro de la asignación
            WaiterWorkplan waiterWorkplan = assignment.getWaiterWorkplan().stream()
                    .filter(w -> w.getWaiter().equals(waiterId))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesero no encontrado en la asignación"));
    
            // Convertir las horas a segundos para facilitar la comparación
            int newHoraInicio = newHourWaiter.getHoraInicio().toSeconds();
            int newHoraFin = newHourWaiter.getHoraFin().toSeconds();
    
            // Verificar si el nuevo rango de horas se solapa con algún rango existente de otros meseros
            boolean isOverlappingWithOthers = assignment.getWaiterWorkplan().stream()
                    .filter(w -> !w.getWaiter().equals(waiterId)) // Excluir el rango de horas del mesero actual
                    .anyMatch(w -> {
                        int existingHoraInicio = w.getHoraInicio().toSeconds();
                        int existingHoraFin = w.getHoraFin().toSeconds();
    
                        // Verificar si hay solapamiento
                        return (newHoraInicio < existingHoraFin && newHoraFin > existingHoraInicio);
                    });
    
            if (isOverlappingWithOthers) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nuevo rango de horas se solapa con un rango existente de otro mesero");
            }
    
            // Actualizar únicamente el objeto hora
            waiterWorkplan.setHoraInicio(newHourWaiter.getHoraInicio());
            waiterWorkplan.setHoraFin(newHourWaiter.getHoraFin());
    
            // Guardar el plan de trabajo actualizado
            workplanRepository.save(workplan);
    
            // Devolver el waiterWorkplan actualizado
            return waiterWorkplan;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar la hora del mesero");
        }
    }
}

