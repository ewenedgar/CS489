package edu.miu.horelo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Entity
@Table(name="users", indexes = {
        @Index(name = "idx_username", columnList = "username"),
        @Index(name = "idx_email", columnList = "email")
})
@Getter
@Data
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer userId;
    private String username;
    private String preferredName;
    private String password;
    @NotBlank
    @Email(message = "Please provide a valid email address")
    private String email;
    @Embedded
    private UserProfile userProfile;
    // Many-to-many relationship with companies and roles through UserCompanyRole
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Optional to avoid cyclic references
    private Set<UserEstoreRole> userEstoreRoles = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<UserRole> roles = new HashSet<>();
    private Long defaultEstore;

    // Data fields needed for implementing methods from UserDetails interface
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;


    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Address> homeAddress;
    // Many users can have many allergies
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Allergy> allergyList;

    public User(Integer userId, String username, String password,
                String email, boolean accountNonExpired,
                boolean accountNonLocked, boolean credentialsNonExpired, boolean enabled) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
    }

    public User(String username, String password) {
        this(null, username, password,  null, true, true, true, true);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        /*String[] userRoles = getRoles().stream()
                .map(UserRoleRepository::getRole)
                //.map((role) -> role.getRoleName())
                .toArray(String[]::new);
        return AuthorityUtils.createAuthorityList(userRoles);*/
        return userEstoreRoles.stream()
                .filter(UserEstoreRole::isActive) // Optional: Include only active roles
                .map(UserEstoreRole::getRole) // Extract the Role object
                .map(Role::getRoleName)      // Extract the role name
                .map(SimpleGrantedAuthority::new) // Wrap each role name in SimpleGrantedAuthority
                .collect(Collectors.toList());

    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }



    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }
    public Set<UserRole> getRole() {
        return roles;
    }
    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
