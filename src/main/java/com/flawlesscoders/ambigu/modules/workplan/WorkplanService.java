package com.flawlesscoders.ambigu.modules.workplan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

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
import com.flawlesscoders.ambigu.modules.user.waiter.DTO.GetWaiterTableDTO;
import com.flawlesscoders.ambigu.modules.workplan.DTO.AssignmentDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkplanService {
    private final WorkplanRepository workplanRepository;
    private final TableRepository tableRepository;
    private final TableService tableService;
    private final WaiterRepository waiterRepository;

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
    public Workplan addAssignmentToWorkplan(String workplanId, String tableId, String waiterId) {
        try {
            Workplan workplan = workplanRepository.findById(workplanId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan de trabajo no encontrado"));
    
            if (!workplan.isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay un plan de trabajo activo");
            }
    
            Table table = tableRepository.findById(tableId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa no encontrada"));
    
            if (table.isTableWaiter()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La mesa ya está asignada a un mesero");
            }
    
            if(table.isEnabled() == false){
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
            } else {
                // Si no existe una asignación, crear una nueva
                assignment = new Assignment();
                assignment.setTable(tableId);
                assignment.setWaiters(new ArrayList<>());
                workplan.getAssigment().add(assignment);
            }
    
            // Agregar el nuevo mesero a la lista de meseros de la asignación
            assignment.getWaiters().add(waiterId);
    
            // Marcar la mesa como asignada a un mesero
            table.setTableWaiter(true);
            tableRepository.save(table);
    
            return workplanRepository.save(workplan);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al agregar la asignación");
        }
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

    //method to change waiter of a table
    public String changeWaiterToTable(String workplanId, String tableId, String waiterId) {
        try {
            Workplan workplan = workplanRepository.findById(workplanId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan de trabajo no encontrado"));

            if (!workplan.isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay un plan de trabajo activo");
            }

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

            // Cambiar el mesero asignado a la mesa
            assignment.getWaiters().add(waiterId);
            table.setTableWaiter(true);
            tableRepository.save(table);
            workplanRepository.save(workplan);

            return "Se cambió el mesero exitosamente";
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al cambiar el mesero de la mesa");
        }
    }

    //method to get all tables with their last waiter in a current workplan 
    public List<TableWithWaiterDTO> getEnabledTablesWithLastWaiter() {
    try {
        List<Table> enabledTables = tableService.getEnabledTablesWithWaiter();

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

                        // Obtener el último mesero de la lista
                        List<String> waiterIds = assignment.getWaiters();
                        if (waiterIds.isEmpty()) {
                            return null; // No hay meseros, no agregar esta mesa
                        }

                        String lastWaiterId = waiterIds.get(waiterIds.size() - 1);

                        // Buscar el mesero en la base de datos
                        Waiter waiter = waiterRepository.findById(lastWaiterId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesero no encontrado"));

                        // Crear y devolver el DTO
                        return new TableWithWaiterDTO(table.getId(), table.getTableIdentifier(), waiter.getName(), table.isTableWaiter(),workplan.getId());
                    }
                    return null; // No tiene asignación en el Workplan
                })
                .filter(Objects::nonNull) // Eliminar valores nulos
                .collect(Collectors.toList());

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al obtener las mesas con su último mesero asignado");
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

    //method to finalize a workplan
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
    
    //method to remove a waiter from a table
    public String removeWaiterToTable(String workplanId, String tableId) {
        try {
            Workplan workplan = workplanRepository.findById(workplanId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan de trabajo no encontrado"));

            if (!workplan.isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay un plan de trabajo activo");
            }

            Table table = tableRepository.findById(tableId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa no encontrada"));

            if (table.isTableWaiter()) {
                if(table.getTableClientStatus() == TableClientStatus.OCCUPIED){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Error al quitar mesero, esta mesa está ocupada por un cliente");
                }else{
                    table.setTableWaiter(false);
                }
            }

            tableRepository.save(table);
            workplanRepository.save(workplan);
            return "Se quitó al mesero exitosamente";
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Error al liberar la mesa de un mesero");
        }   
    }

    //method to change favorite status of a workplan
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

    //method to get all favorite workplans
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

    public AssignmentDTO detailsTableInAWorkplanDTO(String workplanId, String tableId) {
        try {
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
    
            // Validar la lista de meseros
            List<String> waiterIds = assignment.getWaiters();
            if (waiterIds == null || waiterIds.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay meseros asignados a esta mesa");
            }
    
            // Obtener la lista de meseros con `map()` asegurando el tipo de retorno
            List<GetWaiterTableDTO> waiters = waiterIds.stream()
                    .map(waiterId -> {
                        Waiter waiter = waiterRepository.findById(waiterId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesero no encontrado"));
                        return new GetWaiterTableDTO(waiter.getName(), waiter.getLastname_p(), waiter.getShift());
                    })
                    .collect(Collectors.toList());
    
            // Retornar el DTO con la lista de meseros
            return new AssignmentDTO(table.getTableIdentifier(), waiters);
    
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al obtener los detalles de la mesa en el plan de trabajo");
        }
    }
    
    //method to remove a workplan from search but in database still exists
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

    //method to change status to a table in a worikplan active
    public String changeStatusTableInAWorkplan(String workplanId, String tableId) {
        try {
            Workplan workplan = workplanRepository.findById(workplanId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan de trabajo no encontrado"));
    
            if (!workplan.isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay un plan de trabajo activo");
            }
    
            Table table = tableRepository.findById(tableId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa no encontrada"));
    
            if(table.isEnabled() && (!table.isTableWaiter())){
                table.setEnabled(false);
            }else if((!table.isTableWaiter()) && (!table.isEnabled())){
                table.setEnabled(true);
            }else{
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La mesa tiene un mesero asignado");
            }
    
            // Buscar la asignación que corresponde a la mesa especificada
            Assignment assignment = workplan.getAssigment().stream()
                    .filter(a -> a.getTable().equals(tableId))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa no encontrada en este plan de trabajo"));
    
          
            tableRepository.save(table);
            workplanRepository.save(workplan);
    
            return "Se cambió el estado de la mesa del workplan exitosamente";
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al cambiar el estado de la mesa en el plan de trabajo");
        }
    }
    
    //method to get all disabled tables in a workplan
    public List<Table> getDisabledTablesInAWorkplan(String workplanId) {
        try {
            Workplan workplan = workplanRepository.findById(workplanId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan de trabajo no encontrado"));
    
            List<Table> disabledTables = tableRepository.findDisabledTablesWithoutWaiter();
    
            if (disabledTables.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay mesas deshabilitadas");
            }
    
            return disabledTables;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al obtener las mesas deshabilitadas");
        }
    }

    //method to restart a workplan existing
    public boolean restartWorkplan(String workplanId) {
        try {
            Workplan workplan = workplanRepository.findById(workplanId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan de trabajo no encontrado"));
    
            //obtener todas las asignaciones existentes
            List<Assignment> updatedAssignments = workplan.getAssigment().stream().map(assignment -> {
                // 3️⃣ Buscar la mesa correspondiente
                Table table = tableRepository.findById(assignment.getTable())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa no encontrada"));
    
                //verificar si hay meseros asignados previamente
                List<String> waiterIds = assignment.getWaiters();
                if (!waiterIds.isEmpty()) {
                    // 5️⃣ Obtener el último mesero asignado
                    String lastWaiterId = waiterIds.get(waiterIds.size() - 1);
    
                    //verificar si el mesero sigue existiendo
                    Waiter waiter = waiterRepository.findById(lastWaiterId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Último mesero asignado no encontrado"));
    
                    // crear un nuevo assignment con la misma mesa pero solo con el último mesero
                    Assignment newAssignment = new Assignment();
                    newAssignment.setTable(table.getId());
                    newAssignment.setWaiters(new ArrayList<>(List.of(lastWaiterId)));
                    
                    table.setTableWaiter(true);
                    tableRepository.save(table);
                    return newAssignment;
                } else {
                    //si la mesa no tiene meseros previos, mantener la asignación vacía
                    Assignment newAssignment = new Assignment();
                    newAssignment.setTable(table.getId());
                    newAssignment.setWaiters(new ArrayList<>());
                    return newAssignment;
                }         
            }).collect(Collectors.toList());
    
            //actualizar el Workplan con los nuevos assignments y reiniciarlo
            workplan.setAssigment(updatedAssignments);
            workplan.setPresent(true);
            workplanRepository.save(workplan);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al reiniciar el plan de trabajo");
        }
    }
    
    //method to count the number of tables that one waiter has in a workplan
    public int countTablesByWaiterInWorkplan(String workplanId, String waiterId) {
        try {
            Workplan workplan = workplanRepository.findById(workplanId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan de trabajo no encontrado"));
    
            // Contar solo las mesas donde el `waiterId` es el último mesero en la lista de waiters y `tableWaiter == true`
            long count = workplan.getAssigment().stream()
                    .filter(assignment -> {
                        List<String> waiters = assignment.getWaiters();
                        Table table = tableRepository.findById(assignment.getTable())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa no encontrada"));
    
                        return table.isTableWaiter() //que aún tenga mesero asignado
                                && !waiters.isEmpty()
                                && waiters.get(waiters.size() - 1).equals(waiterId); // solo si es el último
                    })
                    .count();
    
            return (int) count;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al contar las mesas asignadas al último mesero en el plan de trabajo");
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
    
    public List<Table> getTablesInChargeByWaiterInWorkplan(String waiterEmail) {
        try {
            // Buscar al mesero por email
            Waiter waiter = waiterRepository.findByEmail(waiterEmail)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesero no encontrado"));
    
            String waiterId = waiter.getId(); // Obtenemos el ID del mesero
    
            // Buscar el Workplan activo
            Workplan workplan = workplanRepository.findByIsPresent(true)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay un plan de trabajo activo"));
    
            // Filtrar las mesas donde el `waiterId` es el último mesero en la lista de waiters
            List<Table> tables = workplan.getAssigment().stream()
                    .filter(assignment -> {
                        List<String> waiters = assignment.getWaiters();
                        Table table = tableRepository.findById(assignment.getTable())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa no encontrada"));
    
                        return table.isTableWaiter() // Que aún tenga mesero asignado
                                && !waiters.isEmpty()
                                && waiters.get(waiters.size() - 1).equals(waiterId); // ✅ Solo si es el último mesero asignado
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
    
    
}
