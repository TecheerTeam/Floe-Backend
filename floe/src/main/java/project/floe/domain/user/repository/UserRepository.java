package project.floe.domain.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import project.floe.domain.user.entity.User;


public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUserId(String userId);
    Optional<User> findByEmail(String email);

}
