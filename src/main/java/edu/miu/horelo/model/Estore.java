package edu.miu.horelo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity(name="estores")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Estore {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long estoreId;
    @NotBlank(message="phone number cannot be empty")
    private String phoneNumber;
    @NotBlank(message="name cannot be empty")
    private String name;
    private String email;
    private String logo;
    private String website;
    @Embedded
    @JsonIgnore
    private OpenDaysAndHours openDaysAndHours;
    private String visibility;
    private LocalDateTime lastUpdate;
    // Time zone for the estore
    private String timeZone;

    @Embedded
    private OrderPolicy orderPolicy;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="address_id")
    //@JsonManagedReference
    @JsonIgnore
    private Address primaryAddress;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    @JsonIgnore
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "edited_by", nullable = false)
    @JsonIgnore
    private User editor;

//    @Column(name = "create_by", nullable = false)
//    private Integer creatorId;
//
//    @Column(name = "edit_by", nullable = false)
//    private Integer editorId;
//    @OneToMany(mappedBy = "estore", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JsonIgnore // Prevents circular reference during JSON serialization
//    private List<Product> products;
@OneToMany(mappedBy = "estore", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@JsonIgnore
private List<Category> categoryList;
    @OneToMany(mappedBy = "estore", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<SubCategory> subCategoryList;
    @OneToMany(mappedBy = "estore", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<SpecialEvent> specialEvents;
//    @OneToOne(mappedBy = "estore", cascade = CascadeType.ALL)
//    @JoinColumn(name = "food_safety_message_id")
    private String foodSafetyMessage;
    @OneToMany(mappedBy = "estore", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Optional to avoid cyclic references
    private Set<UserEstoreRole> userEstoreRoles = new HashSet<>();


    public Estore( String name, String email, String phoneNumber, User creator) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.creator = creator;
        this.editor = creator;

        // Set other fields to default values or null
        this.visibility = null;
        this.lastUpdate = null;
        this.primaryAddress = null;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Estore estore = (Estore) o;
        return Objects.equals(estoreId, estore.estoreId);
    }
    public Set<UserEstoreRole> getUserEstoreRoles() {
        return userEstoreRoles;
    }
    @Override
    public int hashCode() {
        return Objects.hash(estoreId);
    }

}
