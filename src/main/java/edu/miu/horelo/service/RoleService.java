package edu.miu.horelo.service;

import edu.miu.horelo.model.Role;

public interface RoleService {
    Role findByRoleName(String roleName);
}
