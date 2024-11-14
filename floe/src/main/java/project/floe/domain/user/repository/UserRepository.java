package project.floe.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.floe.domain.user.entity.UserEntity;


public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    UserEntity findByUserId(String userId);
    UserEntity findByEmail(String email);

}
