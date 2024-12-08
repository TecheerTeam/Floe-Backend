package project.floe.domain.user.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import project.floe.domain.user.entity.User;
import project.floe.domain.user.entity.UserRole;


@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    public void 유저조회성공_ByUserId() {
        User user = user();
        userRepository.save(user);

        String email = user.getEmail();
        Optional<User> findUser = userRepository.findByEmail(email);

        assertThat(findUser).isPresent();
        assertThat(findUser.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void 유저조회실패_존재하지않는아이디() {
        User user = user();
        userRepository.save(user);

        String email = "noExistEmail@naver.com";
        Optional<User> findUser = userRepository.findByEmail(email);

        assertThat(findUser).isEmpty();
    }

    @Test
    public void 유저생성시_아이디중복확인() {
        User user = user();
        userRepository.save(user);

        User signUpUser = user();
        Optional<User> findUser = userRepository.findByEmail(signUpUser.getEmail());

        assertThat(findUser).isNotNull();
        assertThat(findUser.get().getEmail()).isEqualTo(signUpUser.getEmail());
    }

    @Test
    public void 유저생성시_이메일중복확인() {
        User user = user();
        userRepository.save(user);

        User signUpUser = user();
        Optional<User> findUser = userRepository.findByEmail(signUpUser.getEmail());

        assertThat(findUser).isNotNull();
        assertThat(findUser.get().getEmail()).isEqualTo(signUpUser.getEmail());
    }

    @Test
    public void 유저생성성공() {
        User user = user();

        User savedUser = userRepository.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void 유저삭제성공() {
        User user = user();
        userRepository.save(user);

        userRepository.delete(user);

        Optional<User> findUser = userRepository.findByEmail(user.getEmail());
        assertThat(findUser).isEmpty();
    }

    private User user() {
        return User.builder()
                .nickname("tester")
                .role(UserRole.USER)
                .email("test2@naver.com")
                .password("password")
                .age(23)
                .experience(1)
                .field("backend")
                .build();
    }
}
