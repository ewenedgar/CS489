package edu.miu.horelo.dto.request;

import edu.miu.horelo.model.ClientOrder;
import edu.miu.horelo.model.Estore;
import edu.miu.horelo.model.OrderItem;

import java.util.List;

public record EstoreOrderRequest(
        //Long estoreOrderId,
        ClientOrder clientOrder,
        Estore estore,
        List<OrderItem> orderItems
) {
}
