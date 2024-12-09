package edu.miu.horelo.service.impl;

import edu.miu.horelo.advice.ResourceNotFoundException;
import edu.miu.horelo.model.Estore;
import edu.miu.horelo.model.Role;
import edu.miu.horelo.model.User;
import edu.miu.horelo.model.UserEstoreRole;
import edu.miu.horelo.repository.EstoreRepository;
import edu.miu.horelo.repository.RoleRepository;
import edu.miu.horelo.repository.UserEstoreRoleRepository;
import edu.miu.horelo.repository.UserRepository;
import edu.miu.horelo.service.UserEstoreRoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class UserEstoreRoleImpl implements UserEstoreRoleService {

    private final UserEstoreRoleRepository userEstoreRoleRepository;

    private final UserRepository userRepository;

    private final EstoreRepository estoreRepository;

    private final RoleRepository roleRepository;

    private final RoleHierarchy roleHierarchy;

    @Override
    public void assignRoleToUser(Integer userId, Long estoreId, Integer roleId, User currentUser) throws BadRequestException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Estore estore = estoreRepository.findById(estoreId)
                .orElseThrow(() -> new ResourceNotFoundException("Estore not found"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        Optional<UserEstoreRole> existingRole = userEstoreRoleRepository.findByUserAndEstore(user, estore);
        if (existingRole.isPresent()) {
            throw new BadRequestException("User already has a role in this e-store");
        }

        UserEstoreRole userEstoreRole = new UserEstoreRole();
        userEstoreRole.setCreatedBy(currentUser); // Set the user who is creating the role
        userEstoreRole.setModifiedBy(currentUser);
        userEstoreRole.setUser(user);
        userEstoreRole.setEstore(estore);
        userEstoreRole.setRole(role);

        userEstoreRoleRepository.save(userEstoreRole);
    }

    @Override
    public Optional<String> getEstoreRoleToUser(Integer userId, Long estoreId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Estore estore = estoreRepository.findById(estoreId)
                .orElseThrow(() -> new ResourceNotFoundException("Estore not found"));
      UserEstoreRole role = userEstoreRoleRepository.findByUserIdAndEstoreId(
              user.getUserId(), estore.getEstoreId()).orElseThrow(
                ()-> new ResourceNotFoundException("No user role found")
        );

        return Optional.ofNullable(String.valueOf(role.getRole()));
    }
    @Override
    public Optional<UserEstoreRole> getRoleByUser(Integer userId, Long estoreId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Estore estore = estoreRepository.findById(estoreId)
                .orElseThrow(() -> new ResourceNotFoundException("Estore not found"));
        return userEstoreRoleRepository.findByUserIdAndEstoreId(user.getUserId(), estore.getEstoreId());
    }
    @Override
    public List<UserEstoreRole> getRolesByUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userEstoreRoleRepository.findAllByUser(user);
    }
    @Override
    public UserEstoreRole updateUserRole(Integer userId, Long estoreId, Integer newRoleId, User currentUser) {

        UserEstoreRole userEstoreRole = userEstoreRoleRepository.findByUserIdAndEstoreId(userId, estoreId)
                .orElseThrow(() -> new ResourceNotFoundException("Role assignment not found"));

        Role newRole = roleRepository.findById(newRoleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        userEstoreRole.setRole(newRole);
        userEstoreRole.setModifiedBy(currentUser);
        return userEstoreRoleRepository.save(userEstoreRole);
    }
    @Override
    public void removeUserRole(Integer userId, Long estoreId) {
        UserEstoreRole userEstoreRole = userEstoreRoleRepository.findByUserIdAndEstoreId(userId, estoreId)
                .orElseThrow(() -> new ResourceNotFoundException("Role assignment not found"));

        userEstoreRoleRepository.delete(userEstoreRole);
    }

    @Override
    public boolean hasAuthority(Integer userId, Long estoreId, String requiredRole) {
        Optional<String> userRole = getEstoreRoleToUser(userId, estoreId);
        return userRole.map(role -> roleHierarchy.getReachableGrantedAuthorities(Set.of(new SimpleGrantedAuthority(role)))
                        .contains(new SimpleGrantedAuthority(requiredRole)))
                .orElse(false);
    }

    @Override
    public void assignRoleUserToUser(Integer userId, Long estoreId, Role newRole, User currentUser) {
        UserEstoreRole userEstoreRole = new UserEstoreRole();
        userEstoreRole.setCreatedBy(currentUser); // Set the user who is creating the role
        userEstoreRole.setModifiedBy(currentUser);
        userEstoreRole.setUser(currentUser);
        userEstoreRole.setEstore(null);
        userEstoreRole.setRole(newRole);

        userEstoreRoleRepository.save(userEstoreRole);
    }
}
