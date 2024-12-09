package edu.miu.horelo.service;

import edu.miu.horelo.model.Role;
import edu.miu.horelo.model.User;
import edu.miu.horelo.model.UserEstoreRole;
import org.apache.coyote.BadRequestException;

import java.util.List;
import java.util.Optional;

public interface UserEstoreRoleService {
        /**
         * Assigns a role to a user for a specific estore.
         *
         * @param userId   The ID of the user.
         * @param estoreId The ID of the estore.
         * @param roleId   The ID of the role.
         * @throws BadRequestException if the assignment is invalid (e.g., role already exists).
         */
        void assignRoleToUser(Integer userId, Long estoreId, Integer roleId, User currentUser) throws BadRequestException;

        /**
         * Retrieves the UserEstoreRole for a user and estore.
         * @param userId The User entity.
         * @param estoreId The ID of the estore.
         * @return The UserEstoreRole entity, or null if no role is assigned.
         */
        Optional<String> getEstoreRoleToUser(Integer userId, Long estoreId);

        /**
         * Fetches all roles assigned to a user.
         * @param userId The ID of the user.
         * @return A list of UserEstoreRole entities for the user.
         */
        List<UserEstoreRole> getRolesByUser(Integer userId);

        /**
         * Retrieves the role of a user for a specific estore.
         * @param userId The ID of the user.
         * @param estoreId The ID of the estore.
         * @return An Optional containing the UserEstoreRole, or empty if no role exists.
         */
        Optional<UserEstoreRole> getRoleByUser(Integer userId, Long estoreId);

        /**
         * Updates the role of a user for a specific estore.
         * @param userId The ID of the user.
         * @param estoreId The ID of the estore.
         * @param newRoleId The ID of the new role.
         * @return The updated UserEstoreRole entity.
         */
        UserEstoreRole updateUserRole(Integer userId, Long estoreId, Integer newRoleId, User currentUser);

        /**
         * Removes the role of a user for a specific estore.
         * @param userId The ID of the user.
         * @param estoreId The ID of the estore.
         */
        void removeUserRole(Integer userId, Long estoreId);
        boolean hasAuthority(Integer userId, Long estoreId, String requiredRole);
        void assignRoleUserToUser(Integer userId, Long estoreId, Role newRoleId, User currentUser);

}
