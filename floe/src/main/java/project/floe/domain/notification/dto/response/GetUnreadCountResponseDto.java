package project.floe.domain.notification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetUnreadCountResponseDto {
    private Long count;
}
