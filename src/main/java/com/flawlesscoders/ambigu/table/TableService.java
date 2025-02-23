package com.flawlesscoders.ambigu.table;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TableService {
    private final TableRepository tableRepository;

    //Method to save a table
    public Table saveTable(Table table){
        try{
            List<Table> found = tableRepository.findAll();
            for(Table t : found){
                if(table.getTableIdentifier().equals(t.getTableIdentifier())){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Ya existe una mesa con ese identificador");
                }
            }
            table.setEnabled(true);
            table.setTableClientStatus(TableClientStatus.UNOCCUPIED);
            table.setTableWaiter(false);
            return tableRepository.save(table);
        }catch(Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Error al guardar mesa");
        }
    }

    //method to get all tables
    public List<Table> findAllTables(){
        try {
            if(tableRepository.findAll().isEmpty()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No hay mesas");
            }else{
                return tableRepository.findAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No hay mesas");
        }
    }

    //method to get table by id
    public Table findById(String id){
        try {
            return tableRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Mesa no encontrada"));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Error al obtener la mesa");
        }
    }

    //method to update table 
    public Table updateTable(Table table){
        try {
            Table foundTable = tableRepository.findById(table.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Mesa no encontrada"));
            if(foundTable != null){
                table.setEnabled(foundTable.isEnabled());
                table.setTableClientStatus(foundTable.getTableClientStatus());
                table.setTableWaiter(foundTable.isTableWaiter());
                return tableRepository.save(table);
            }else{
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Error en la actualizacion");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Error al obtener la mesa");
        }
    }

    //method to change the table status (enabled or disabled)
    public String changeStatusTable(String id) {
        try {
            Table foundTable = tableRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Mesa no encontrada"));
            if (foundTable.isEnabled()) {
                if(foundTable.getTableClientStatus() == TableClientStatus.OCCUPIED){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Error, esta mesa está ocupada en servicio");
                }else{
                    foundTable.setEnabled(false);
                }
            } else {
                foundTable.setEnabled(true);
            }
            tableRepository.save(foundTable);  
            return "La mesa se actualizó correctamente";
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Error al actualizar la mesa");
        }     
    }


    //method to get all enabled tables 
    public List<Table> getEnabledTables(){
        try {
            if (tableRepository.findByIsEnabledTrue().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No hay mesas habilitadas");
            }else{
                return tableRepository.findByIsEnabledTrue();
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Error al obtener las mesas habilitadas");
        }
    }

    //method to get all enabled tables 
    public List<Table> getDisabledTables(){
        try {
            if (tableRepository.findByIsEnabledFalse().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No hay mesas deshabilitadas");
            }else{
                return tableRepository.findByIsEnabledFalse();
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Error al obtener las mesas deshabilitadas");
        }
    }

    //method to find all enabled tables without waiter (mobile)
    public List<Table> getEnabledTablesWithoutWaiter(){
        try {
            if(tableRepository.findEnabledTablesWithoutWaiter().isEmpty()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No hay mesas sin mesero");
            }else{
                return tableRepository.findEnabledTablesWithoutWaiter();
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al obtener las mesas sin meseros");
        }
    }

    //method to find all disabled tables in a work plan (mobile)...

    //method to find all enabled tables with waiter (mobile)
    public List<Table> getEnabledTablesWithWaiter(){
        try {
            if(tableRepository.findEnabledTablesWithWaiter().isEmpty()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay mesas asignadas");
            }else{
                return tableRepository.findEnabledTablesWithWaiter();
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al obtener las mesas con mesero");
        }
    }
}