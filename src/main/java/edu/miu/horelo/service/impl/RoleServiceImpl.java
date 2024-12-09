package edu.miu.horelo.service.impl;

import edu.miu.horelo.advice.ResourceNotFoundException;
import edu.miu.horelo.model.Role;
import edu.miu.horelo.repository.RoleRepository;
import edu.miu.horelo.service.RoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    @Override
    public Role findByRoleName(String roleName) {
        return Optional.of(roleRepository.findByRoleName(roleName)).get().orElseThrow(
                () ->  new ResourceNotFoundException("Role not found") );
    }
}
