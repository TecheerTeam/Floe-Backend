package project.floe.domain.record.service;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.floe.domain.record.dto.response.TagStatisticsResponse;
import project.floe.domain.record.repository.RecordJpaRepository;
import project.floe.domain.record.repository.RecordTagJpaRepository;
import project.floe.domain.record.repository.TagJpaRepository;
import project.floe.domain.user.entity.User;
import project.floe.domain.user.repository.UserRepository;
import project.floe.global.auth.jwt.service.JwtService;
import project.floe.global.error.ErrorCode;
import project.floe.global.error.exception.UserServiceException;

@Service
@RequiredArgsConstructor
public class TagStatisticsService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RecordJpaRepository recordRepository;
    private final RecordTagJpaRepository recordTagJpaRepository;
    private final TagJpaRepository tagJpaRepository;

    public List<TagStatisticsResponse> getTagStatistics(){
        List<Object[]> tagStatistics = tagJpaRepository.findAllTagStatistics();
        Long totalTagCount = tagJpaRepository.findTotalTagCount();

        return tagStatistics.stream()
                .map(result -> {
                    return TagStatisticsResponse.builder()
                            .tag((String) result[0])
                            .count  ((Long) result[1])
                            .ratio(totalTagCount > 0 ? BigDecimal.valueOf((Long) result[1])
                                    .divide(BigDecimal.valueOf(totalTagCount), 4, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100)) // 비율을 퍼센트로 변환
                                    : BigDecimal.ZERO)
                            .build();
                })
                .toList();
    }

    public List<TagStatisticsResponse> getUserTagStatistics(HttpServletRequest request) {
        String userEmail = jwtService.extractEmail(request).orElseThrow(
                () -> new UserServiceException(ErrorCode.TOKEN_ACCESS_NOT_EXIST)
        );
        User user = userRepository.findByEmail(userEmail).orElseThrow(
                () -> new UserServiceException(ErrorCode.EMAIL_NOT_FOUND_ERROR)
        );

        List<Long> recordIds = recordRepository.findRecordIdsByUserId(user.getId());
        if (recordIds.isEmpty()){
            return List.of();
        }
        List<Object[]> tagStatistics = recordTagJpaRepository.findTagStatisticsByRecordIds(recordIds);
        Long totalTagCount = recordTagJpaRepository.findTotalTagCountByRecordIds(recordIds);

        return tagStatistics.stream()
                .map(result -> {
                    return TagStatisticsResponse.builder()
                            .tag((String) result[0])
                            .count((Long) result[1])
                            .ratio(totalTagCount > 0 ? BigDecimal.valueOf((Long) result[1])
                                    .divide(BigDecimal.valueOf(totalTagCount), 4, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100)) // 비율을 퍼센트로 변환
                                    : BigDecimal.ZERO)
                            .build();
                })
                .toList();
    }
}
