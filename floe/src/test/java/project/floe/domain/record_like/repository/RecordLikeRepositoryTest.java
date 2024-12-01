package project.floe.domain.record_like.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import project.floe.domain.record.entity.Record;
import project.floe.domain.record.entity.RecordTags;
import project.floe.domain.record.entity.RecordType;
import project.floe.domain.record.repository.RecordJpaRepository;
import project.floe.domain.record_like.entity.RecordLike;
import project.floe.domain.user.entity.User;
import project.floe.domain.user.repository.UserRepository;

@ActiveProfiles("test")
@DataJpaTest
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
        user = User.builder().email("test@example.com").password("1234").nickname("test").build();
        record = Record.builder()
                .id(null)
                .user(user)
                .title("테스트")
                .content("테스트 입니다")
                .recordType(RecordType.FLOE)
                .recordTags(new RecordTags())
                .build();
        userRepository.save(user);
        recordJpaRepository.save(record);
    }

    @Test
    public void 좋아요수조회() {
        RecordLike recordLike = new RecordLike(user, record);
        recordLikeRepository.save(recordLike);

        long count = recordLikeRepository.countByRecordId(record.getId());

        assertThat(count).isEqualTo(1);
    }

    @Test
    public void 좋아요추가() {
        recordLikeRepository.save(new RecordLike(user, record));

        Optional<RecordLike> optionalRecordLike = recordLikeRepository.findByUserIdAndRecordId(user.getId(),
                record.getId());

        assertThat(optionalRecordLike).isPresent();
    }

    @Test
    public void 좋아요삭제() {
        RecordLike recordLike = new RecordLike(user, record);
        recordLikeRepository.save(recordLike);

        recordLikeRepository.delete(recordLike);
        Optional<RecordLike> optionalRecordLike = recordLikeRepository.findByUserIdAndRecordId(user.getId(),
                record.getId());

        assertThat(optionalRecordLike).isEmpty();
    }

    @Test
    public void 좋아요중복() {
        RecordLike recordLike = new RecordLike(user, record);
        recordLikeRepository.save(recordLike);

        assertThrows(DataIntegrityViolationException.class, () ->
                recordLikeRepository.save(new RecordLike(user, record)));

    }


}
