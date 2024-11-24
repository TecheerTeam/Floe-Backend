package project.floe.domain.record_like.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record.entity.RecordTags;
import project.floe.domain.record.entity.RecordType;
import project.floe.domain.record.repository.RecordJpaRepository;
import project.floe.domain.record_like.entity.RecordLike;
import project.floe.domain.user.entity.User;
import project.floe.domain.user.repository.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class RecordLikeRepositoryTest {

    @Autowired
    private RecordLikeRepository recordLikeRepository;
    @Autowired
    private RecordJpaRepository recordJpaRepository;
    @Autowired
    private UserRepository userRepository;

    private User user;
    private Record record;

    @BeforeEach
    public void init() {
        user = new User(null, "role", "userId", "password", "name", "email@email.com", 1, 20, "", "field");
        record = Record.builder()
                .id(null)
                .userId(0L)
                .title("테스트")
                .content("테스트 입니다")
                .recordType(RecordType.FLOE)
                .recordTags(new RecordTags())
                .build();
        userRepository.save(user);
        recordJpaRepository.save(record);
    }

    @Test
    public void 좋아요수조회_성공() {
        RecordLike recordLike = new RecordLike(null, user, record);
        recordLikeRepository.save(recordLike);

        long count = recordLikeRepository.countByRecordId(record.getId());

        assertThat(count).isEqualTo(1);
    }

    @Test
    public void 좋아요추가성공() {
        recordLikeRepository.addLike(user.getId(), record.getId());

        long count = recordLikeRepository.countByRecordId(record.getId());

        assertThat(count).isEqualTo(1);
    }

    @Test
    public void 좋아요삭제성공() {
        recordLikeRepository.addLike(user.getId(), record.getId());

        int deleteCount = recordLikeRepository.deleteLike(user.getId(), record.getId());

        assertThat(deleteCount).isEqualTo(1);
    }

    @Test
    public void 좋아요추가실패_중복시예외확인() {
        recordLikeRepository.addLike(user.getId(), record.getId());

        assertThrows(DataIntegrityViolationException.class,
                () -> recordLikeRepository.addLike(user.getId(), record.getId()));
    }

    @Test
    public void 좋아요삭제실패_존재하지않음() {
        int deleteCount = recordLikeRepository.deleteLike(user.getId(), record.getId());

        assertThat(deleteCount).isEqualTo(0);
    }


}
