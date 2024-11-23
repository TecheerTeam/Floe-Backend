package project.floe.domain.user.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import project.floe.domain.user.entity.User;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void 유저조회성공_ByUserId() {
        User user = user();
        userRepository.save(user);

        String userId = user.getUserId();
        Optional<User> findUser = userRepository.findByUserId(userId);

        assertThat(findUser).isPresent();
        assertThat(findUser.get().getUserId()).isEqualTo(user.getUserId());
    }

    @Test
    public void 유저조회실패_존재하지않는아이디() {
        User user = user();
        userRepository.save(user);

        String userId = "noExistId";
        Optional<User> findUser = userRepository.findByUserId(userId);

        assertThat(findUser).isEmpty();
    }

    @Test
    public void 유저생성시_아이디중복확인() {
        User user = user();
        userRepository.save(user);

        User signUpUser = user();
        Optional<User> findUser = userRepository.findByUserId(signUpUser.getUserId());

        assertThat(findUser).isNotNull();
        assertThat(findUser.get().getUserId()).isEqualTo(signUpUser.getUserId());
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
        assertThat(savedUser.getUserId()).isEqualTo(user.getUserId());
    }

    @Test
    public void 유저삭제성공() {
        User user = user();
        userRepository.save(user);

        userRepository.delete(user);

        Optional<User> findUser = userRepository.findByUserId(user.getUserId());
        assertThat(findUser).isEmpty();
    }

    private User user() {
        return new User(null, "role", "userId", "password", "name", "email", 1, 20, "image", "field");
    }
}
