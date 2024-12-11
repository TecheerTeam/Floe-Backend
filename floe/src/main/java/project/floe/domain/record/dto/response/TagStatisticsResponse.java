package project.floe.domain.record.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class TagStatisticsResponse {
    private String tagName;
    private Long count;
    private BigDecimal ratio; // 비율 (%)

}
