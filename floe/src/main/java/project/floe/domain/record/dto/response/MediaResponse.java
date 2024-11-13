package project.floe.domain.record.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MediaResponse {
    private Long mediaId;
    private String mediaUrl;
}
