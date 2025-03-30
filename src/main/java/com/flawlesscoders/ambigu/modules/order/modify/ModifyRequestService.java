package com.flawlesscoders.ambigu.modules.order.modify;

import java.text.DecimalFormat;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.flawlesscoders.ambigu.modules.order.Order;
import com.flawlesscoders.ambigu.modules.order.OrderRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ModifyRequestService {
    private final ModifyRequestRepository requestRepository;
    private final OrderRepository repository;

    /**
     * Retrieves all modify requests.
     * @return List of modify requests.
     */
    public List<ModifyRequest> getAllRequests(){
        return requestRepository.findAll();
    }   

    public List<ModifyRequest> getPendingRequests(){
        return requestRepository.findActiveModifyRequestsWithOrders();
    }  

    public ModifyRequest getById(String id){
        return requestRepository.findById(id)
        .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
    /**
     * Searches for a modify request by its ID.
     * @param id Modify request ID.
     * @return The found modify request.
     * @throws ResponseStatusException if the modify request does not exist.
     */
    public ModifyRequest sendModifyRequest (ModifyRequest modifyRequest){
        try {
            float total = 0;
            if (repository.existsById(modifyRequest.getOrderId())) {
                for(int i = 0; i< modifyRequest.getModifiedDishes().size(); i++){
                    total += modifyRequest.getModifiedDishes().get(i).getUnitPrice() * modifyRequest.getModifiedDishes().get(i).getQuantity();
                }

                DecimalFormat df = new DecimalFormat("#.##");
                float totalFormatted = Float.parseFloat(df.format(total));                
                modifyRequest.setTotal(totalFormatted);
                modifyRequest.setToDelete(false);
                requestRepository.save(modifyRequest);
                return modifyRequest;
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la orden");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Sends a delete request for an order.
     * @param id Order ID.
     * @return The created delete request.
     * @throws ResponseStatusException if the order does not exist.
     */
    public ModifyRequest sendDeleteRequest(String id){
        try{
            Order found = repository.findById(id).orElse(null);
            if (found != null){
                ModifyRequest deleteRequest = ModifyRequest.builder()
                    .orderId(found.getId())
                    .toDelete(true)
                    .modifiedDishes(found.getDishes())
                    .total(found.getTotal())
                    .waiter(found.getWaiter())
                    .table((found.getTable()))
                    .tableName(found.getTableName())
                    .workplan((found.getWorkplan()))
                    .orderNumber((found.getOrderNumber()))
                    .deletedRequest(false)
                    .build();
                requestRepository.save(deleteRequest);
                return deleteRequest;
            }else{
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la orden");
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public boolean rejectModifyRequest(String id){
        ModifyRequest request = requestRepository.findById(id)
        .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
        request.setDeletedRequest(true);
        requestRepository.save(request);
        return true;
    }
}

