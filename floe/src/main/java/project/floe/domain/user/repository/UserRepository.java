package project.floe.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.floe.domain.user.entity.User;


public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUserId(String userId);
    User findByEmail(String email);

}
