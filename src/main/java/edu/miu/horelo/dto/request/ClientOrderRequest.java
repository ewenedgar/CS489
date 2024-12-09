package edu.miu.horelo.dto.request;

import edu.miu.horelo.model.EstoreOrder;
import edu.miu.horelo.model.Status;
import edu.miu.horelo.model.User;

import java.util.List;

public record ClientOrderRequest(
        //Long clientOrderId

        User user, // Tracks the user who owns this order


        List<EstoreOrder> estoreOrders, // Group of EstoreOrders within this ClientOrder

        Status status,

        String clientOrderIdReference
) {
}
