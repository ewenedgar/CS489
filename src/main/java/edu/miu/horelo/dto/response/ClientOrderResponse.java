package edu.miu.horelo.dto.response;

import edu.miu.horelo.model.EstoreOrder;
import edu.miu.horelo.model.Status;
import edu.miu.horelo.model.User;

import java.util.List;

public record ClientOrderResponse(
        Long clientOrderId,
        User user,
        List<EstoreOrder> estoreOrders,
        Status status,
        String clientOrderIdReference
) {
}
