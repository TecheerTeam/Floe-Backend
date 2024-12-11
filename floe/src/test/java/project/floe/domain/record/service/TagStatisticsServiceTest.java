package project.floe.domain.record.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import project.floe.domain.record.dto.response.TagStatisticsResponse;
import project.floe.domain.record.repository.RecordJpaRepository;
import project.floe.domain.record.repository.RecordTagJpaRepository;
import project.floe.domain.record.repository.TagJpaRepository;
import project.floe.domain.user.entity.User;
import project.floe.domain.user.repository.UserRepository;
import project.floe.global.auth.jwt.service.JwtService;

@ExtendWith(MockitoExtension.class)
public class TagStatisticsServiceTest {

    @InjectMocks
    TagStatisticsService tagStatisticsService;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecordJpaRepository recordRepository;

    @Mock
    private RecordTagJpaRepository recordTagJpaRepository;

    @Mock
    private TagJpaRepository tagJpaRepository;

    @Test
    @DisplayName("태그 전체 통계 조회 테스트")
    void 태그_전체_통계_조회() {
        // given
        List<Object[]> mockTagStatistics = List.of(
                new Object[]{"Java", 30L},
                new Object[]{"Spring", 20L}
        );
        Long mockTotalTagCount = 50L;

        when(tagJpaRepository.findAllTagStatistics()).thenReturn(mockTagStatistics);
        when(tagJpaRepository.findTotalTagCount()).thenReturn(mockTotalTagCount);

        // when
        List<TagStatisticsResponse> result = tagStatisticsService.getTagStatistics();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("tagName").containsExactly("Java", "Spring");
        assertThat(result).extracting("count").containsExactly(30L, 20L);
        assertThat(result).extracting("ratio").containsExactly(
                BigDecimal.valueOf(60.00).setScale(4, RoundingMode.HALF_UP),
                BigDecimal.valueOf(40.00).setScale(4, RoundingMode.HALF_UP)
        );

        Mockito.verify(tagJpaRepository).findAllTagStatistics();
        Mockito.verify(tagJpaRepository).findTotalTagCount();
    }

    @Test
    @DisplayName("태그가 없으면 빈 결과를 반환합니다")
    void 태그_조회_없음() {
        // given
        Mockito.when(tagJpaRepository.findAllTagStatistics()).thenReturn(List.of());
        Mockito.when(tagJpaRepository.findTotalTagCount()).thenReturn(0L);

        // when
        List<TagStatisticsResponse> result = tagStatisticsService.getTagStatistics();

        // then
        assertThat(result).isEmpty();

        // Verify that the repository methods were called
        Mockito.verify(tagJpaRepository).findAllTagStatistics();
        Mockito.verify(tagJpaRepository).findTotalTagCount();
    }

    @Test
    @DisplayName("유저 태그 통계 조회 테스트")
    void 유저_태그_통계_조회() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        String email = "test@example.com";
        Long userId = 1L;
        List<Long> recordIds = List.of(1L, 2L);
        List<Object[]> tagStatistics = List.of(
                new Object[]{"Java", 30L},
                new Object[]{"Spring", 20L}
        );
        Long totalTagCount = 50L;

        // Mocking
        when(jwtService.extractEmail(request)).thenReturn(Optional.of(email));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(User.builder().email(email).id(userId).build()));
        when(recordRepository.findRecordIdsByUserId(userId)).thenReturn(recordIds);
        when(recordTagJpaRepository.findTagStatisticsByRecordIds(recordIds)).thenReturn(tagStatistics);
        when(recordTagJpaRepository.findTotalTagCountByRecordIds(recordIds)).thenReturn(totalTagCount);

        // When
        List<TagStatisticsResponse> result = tagStatisticsService.getUserTagStatistics(request);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("tagName").containsExactly("Java", "Spring");
        assertThat(result).extracting("count").containsExactly(30L, 20L);
        assertThat(result).extracting("ratio").containsExactly(
                BigDecimal.valueOf(60.0000).setScale(4),
                BigDecimal.valueOf(40.0000).setScale(4)
        );
    }

    @Test
    @DisplayName("사용자가 작성한 글이 없다면 빈 결과를 반환합니다")
    void 사용자_작성_기록_없음() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        String email = "test@example.com";
        Long userId = 1L;

        // Mocking
        when(jwtService.extractEmail(request)).thenReturn(Optional.of(email));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(User.builder().email(email).id(userId).build()));
        when(recordRepository.findRecordIdsByUserId(userId)).thenReturn(List.of());

        // When
        List<TagStatisticsResponse> result = tagStatisticsService.getUserTagStatistics(request);

        // Then
        assertThat(result).isEmpty();
    }
}


