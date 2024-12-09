package edu.miu.horelo.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "user_estore_roles"
        , uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "estore_id"})}
)
public class UserEstoreRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private LocalDate assignedDate;
    private boolean acceptedRole=false;//by_user defaults to true
    private boolean active=true;//defaults to true
    private boolean isActive;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    @OneToOne(fetch = FetchType.EAGER)

    @JoinColumn(name = "created_by", nullable = false, unique = false)
    private User createdBy;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "modified_by",  nullable = false, unique = false)
    private User modifiedBy;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estore_id")
    private Estore estore;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
    // Automatically set the creation time before persisting
    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now(); // Set creation time to current time
        this.modifiedDate = LocalDateTime.now();   // Initialize lastUpdate to the same value
    }
    public boolean isActive() {
        return this.active;
    }
    // Automatically set the last  update time before updating
    @PreUpdate
    protected void onUpdate() {
        this.modifiedDate = LocalDateTime.now(); // Set lastUpdate to current time
    }

}