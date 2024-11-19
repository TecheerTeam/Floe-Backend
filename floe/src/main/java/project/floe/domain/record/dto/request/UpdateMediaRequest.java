package project.floe.domain.record.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMediaRequest {

    private Long mediaId;

    private String mediaUrl;
}
