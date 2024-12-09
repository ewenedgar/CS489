package edu.miu.horelo.repository;

 import edu.miu.horelo.model.Estore;
 import edu.miu.horelo.model.User;
 import edu.miu.horelo.model.UserEstoreRole;
        import org.springframework.data.jpa.repository.JpaRepository;
 import org.springframework.data.jpa.repository.Query;
 import org.springframework.data.repository.query.Param;
 import org.springframework.stereotype.Repository;

 import java.util.List;
 import java.util.Optional;
@Repository
public interface UserEstoreRoleRepository extends JpaRepository<UserEstoreRole, Integer> {
    @Query("SELECT uer FROM UserEstoreRole uer WHERE uer.user = :user AND uer.estore = :estore")
    Optional<UserEstoreRole> findByUserAndEstore(@Param("user") User user, @Param("estore") Estore estore);
        //Optional<UserEstoreRole> findByUserAndEstore(User user, Estore estore);
        @Query("SELECT uer.role FROM UserEstoreRole uer WHERE uer.user.userId = :userId AND uer.estore.estoreId = :estoreId")
        Optional<String> findRoleByUserIdAndEstoreId(@Param("userId") Integer userId, @Param("estoreId") Long estoreId);

        @Query("SELECT uer.role FROM UserEstoreRole uer WHERE uer.user.userId = :userId AND uer.estore.estoreId = :estoreId")
        Optional<UserEstoreRole> findByUserIdAndEstoreId(Integer userId, Long estoreId);

        List<UserEstoreRole> findAllByUser(User user);

        List<UserEstoreRole> findAllByEstore(Estore estore);


}
