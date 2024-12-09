package edu.miu.horelo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass  // Allows inheritance in other entities
public abstract class BaseEntity {

    @Column(name = "creation_time", updatable = false) // Prevents updates
    private LocalDateTime creationTime;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    // Automatically set the creation time before persisting
    @PrePersist
    protected void onCreate() {
        this.creationTime = LocalDateTime.now(); // Set creation time to current time
        this.lastUpdate = LocalDateTime.now();   // Initialize lastUpdate to the same value
    }

    // Automatically set the last update time before updating
    @PreUpdate
    protected void onUpdate() {
        this.lastUpdate = LocalDateTime.now(); // Set lastUpdate to current time
    }

    // Getters and setters (optional if using Lombok @Data or similar)
    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
