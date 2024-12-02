package project.floe.domain.record_save.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FindMediasByRecordIdsResponseDto {
    private Long recordId;
    private Long mediaId;
    private String mediaUrl;

}
