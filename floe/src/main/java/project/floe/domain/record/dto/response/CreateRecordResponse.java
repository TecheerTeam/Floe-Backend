package project.floe.domain.record.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRecordResponse {

    private Long recordId;

    public static CreateRecordResponse from(Long recordId) {
        return CreateRecordResponse.builder()
                .recordId(recordId).build();
    }


}
