package edu.miu.horelo.repository;

import edu.miu.horelo.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findUserByUsername(String username);

    @Query(value = "SELECT user_id FROM users WHERE email = :email", nativeQuery = true)
    Optional<Integer> findUserIdByEmail(String email);
    //UserProfileResponse addUserProfile(UserRequest userRequest);
    @Query(value = "SELECT * FROM users WHERE user_id = :userId", nativeQuery = true)
    Optional<User> findUserByUserId(Integer userId);
    Optional<User> findUserByEmail(String email);
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.defaultEstore = :estoreId WHERE u.username = :email")
    int updateDefaultEstore(@Param("email") String email, @Param("estoreId") Long estoreId);

    Optional<User> findByUserId(Integer userId);

    User getUserByEmail(String email);
}
