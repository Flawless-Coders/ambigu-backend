package com.flawlesscoders.ambigu.order;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.flawlesscoders.ambigu.order.dto.OrderFeedbackDTO;
import com.flawlesscoders.ambigu.order.modify.ModifyRequest;
import com.flawlesscoders.ambigu.order.modify.ModifyRequestRepository;

import lombok.AllArgsConstructor;
/**
 * Service for managing orders in the system.
 */
@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepository repository;
    private final ModifyRequestRepository requestRepository;

    /**
     * Retrieves all registered orders.
     * @return List of orders.
     */
    public List<Order> getAllOrders(){
        return repository.findAll();
    }

     /**
     * Searches for an order by its ID.
     * @param id Order ID.
     * @return The found order.
     * @throws ResponseStatusException if the order does not exist.
     */
    public Order getOrderById(String id){
        return repository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la orden"));
    }

    /**
     * Creates a new order.
     * @param order Order to be created.
     * @return The created order.
     * @throws ResponseStatusException if the order cannot be created.
     */
    public Order createOrder(Order order){
        float total = 0;
        try{
            for(int i = 0; i< order.getDishes().size(); i++){
                total += order.getDishes().get(i).getUnitPrice() * order.getDishes().get(i).getQuantity();
            }

            long orderNumber = repository.count()+1; 
            order.setOrderNumber(orderNumber);

            DecimalFormat df = new DecimalFormat("#.##");
            float totalFormatted = Float.parseFloat(df.format(total));

            order.setFinalized(false);

            order.setTotal(totalFormatted);

            order.setOpinion(new Opinion(
                            0,
                            "S/C"
                        ));

            order.setDate(new Date());

            repository.save(order);

            return order;
        }catch(Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Updates an order with the information of a modification request.
     * @param modifiedOrderId ID of the modification request.
     * @return The updated order.
     * @throws ResponseStatusException if the order or the modification request do not exist.
     */
    public Order updateOrder(String modifiedOrderId){
        try{
            ModifyRequest found = requestRepository.findById(modifiedOrderId).orElse(null);
            if (found != null){
                Order order = repository.findById(found.getOrderId()).orElse(null);
                if (order != null){
                    order.setDishes(found.getModifiedDishes());
                    order.setTotal(found.getTotal());
                    order.setWaiter(found.getWaiter());
                    repository.save(order);
                    found.setDeletedRequest(true);
                    requestRepository.save(found);
                    return order;
                }else{
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la orden");
                }
            }else{
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la solicitud de modificación");
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Deletes an order with the information of a deletion request.
     * @param id ID of the deletion request.
     * @return True if the order was deleted successfully.
     * @throws ResponseStatusException if the order or the deletion request do not exist.
     */
    public boolean deleteOrder(String id){
        try{
            ModifyRequest deleteRequestFound = requestRepository.findById(id).orElse(null);
            if (deleteRequestFound != null){
                Order found = repository.findById(deleteRequestFound.getOrderId()).orElse(null);
                if (found != null){
                    found.setDeleted(true);
                    repository.save(found);
                    deleteRequestFound.setDeletedRequest(true);
                    requestRepository.save(deleteRequestFound);
                    return true;
                    
                }else{
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la orden");
                }
            }else{
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la solicitud de eliminación");
            }

        }catch(Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Finalizes an order.
     * @param id Order ID.
     * @return The finalized order.
     * @throws ResponseStatusException if the order does not exist.
     */
    public Order finalizeOrder(String id){
        try{
            Order found = repository.findById(id).orElse(null);
            if (found != null){
                found.setFinalized(true);
                repository.save(found);
                return found;
            }else{
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la orden");
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Rates and comments an order.
     * @param id Order ID.
     * @param orderFeedbackDTO Qualification and comment.
     * @return The rated and commented order.
     * @throws ResponseStatusException if the order does not exist, the qualification is less than 1 or the comment is empty.
     */
    public Order rateAndCommentOrder(String id, OrderFeedbackDTO orderFeedbackDTO){
        if (!(orderFeedbackDTO.getQualification() < 1)){            
            if (!(orderFeedbackDTO.getComment().isEmpty()) || !(orderFeedbackDTO.getComment().isBlank())){                
                Order found = repository.findById(id).orElse(null);
                if (found != null){
                    Opinion opinion = new Opinion(
                        orderFeedbackDTO.getQualification(),
                        orderFeedbackDTO.getComment()
                    );
                    found.setOpinion(opinion);
                    repository.save(found);
                    return found;
                }else{
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la orden");
                }
            }else{
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El comentario no puede estar vacío");
            }
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La calificación no puede ser menor a 1");
        }

    }
    
    /**
     * Retrieves all current orders.
     * @return List of current orders.
     */
    public List<Order> getCurrentOrders(){
        return repository.getCurrentOrders();
    }

    /**
     * Retrieves all finalized orders.
     * @return List of finalized orders.
     */
    public List<Order> getFinalizedOrders(){
        return repository.getFinalizedOrders();
    }
}
