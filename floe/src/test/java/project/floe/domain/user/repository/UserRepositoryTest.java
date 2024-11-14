package project.floe.domain.user.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import project.floe.domain.user.entity.UserEntity;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void 유저조회성공_ByUserId(){
        UserEntity user = user();
        userRepository.save(user);

        String userId = user.getUserId();
        UserEntity findUser = userRepository.findByUserId(userId);

        assertThat(findUser.getUserId()).isEqualTo(user.getUserId());
    }

    @Test
    public void 유저조회실패_존재하지않는아이디(){
        UserEntity user = user();
        userRepository.save(user);

        String userId = "noExistId";
        UserEntity findUser = userRepository.findByUserId(userId);

        assertThat(findUser).isNull();
    }

    @Test
    public void 유저생성시_아이디중복확인(){
        UserEntity user = user();
        userRepository.save(user);

        UserEntity signUpUser = user();
        UserEntity findUser = userRepository.findByUserId(signUpUser.getUserId());

        assertThat(findUser).isNotNull();
        assertThat(findUser.getUserId()).isEqualTo(signUpUser.getUserId());
    }

    @Test
    public void 유저생성시_이메일중복확인(){
        UserEntity user = user();
        userRepository.save(user);

        UserEntity signUpUser = user();
        UserEntity findUser = userRepository.findByEmail(signUpUser.getEmail());

        assertThat(findUser).isNotNull();
        assertThat(findUser.getEmail()).isEqualTo(signUpUser.getEmail());
    }

    @Test
    public void 유저생성성공(){
        UserEntity user = user();

        UserEntity savedUser = userRepository.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUserId()).isEqualTo(user.getUserId());
    }

   @Test
   public void 유저삭제성공(){
       UserEntity user = user();
       userRepository.save(user);

       userRepository.delete(user);

       UserEntity findUser = userRepository.findByUserId(user.getUserId());
       assertThat(findUser).isNull();
   }

    private UserEntity user(){
        return new UserEntity(0,"userId","password","name","email",1,20,"","backend");
    }
}
