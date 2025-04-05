package com.flawlesscoders.ambigu.modules.order.modify;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.flawlesscoders.ambigu.modules.order.OrderDishes;

@Document(collection = "modify_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Model that repsents the base Modify requests for the waiter leader")
public class ModifyRequest {
    
    @Id
    private String id;

    @NotNull(message = "El ID de la orden es obligatorio")
    private String orderId;

    private List<OrderDishes> modifiedDishes;

    @NotNull(message = "El precio unitario no puede ser nulo")
    @Schema(description = "Unit price of the dish")
    private float total;
    private String waiter;
    private String waiterId;
    private long orderNumber;
    private String table;
    private String tableName;
    private String workplan;
    private boolean toDelete;
    private boolean deletedRequest;
}
