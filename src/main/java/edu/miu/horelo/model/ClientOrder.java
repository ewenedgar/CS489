package edu.miu.horelo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ClientOrder extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long clientOrderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // Tracks the user who owns this order

    @OneToMany(mappedBy = "clientOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EstoreOrder> estoreOrders; // Group of EstoreOrders within this ClientOrder

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", referencedColumnName = "statusId")
    private Status status;

    private String clientOrderIdReference; // Unique identifier for external references

}
