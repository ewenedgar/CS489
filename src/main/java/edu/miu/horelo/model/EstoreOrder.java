package edu.miu.horelo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
@EqualsAndHashCode(callSuper = true)
@Entity(name="estore_orders")
@Data@AllArgsConstructor@NoArgsConstructor
public class EstoreOrder extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long estoreOrderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_order_id")
    private ClientOrder clientOrder; // Parent client order

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estore_id")
    private Estore estore; // The Estore this order is associated with

    @OneToMany(mappedBy = "estoreOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems; // OrderItems associated with this EstoreOrder
}
