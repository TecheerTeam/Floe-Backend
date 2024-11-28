package project.floe.domain.record.dto.response;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.floe.domain.record.entity.Media;
import project.floe.domain.record.entity.Record;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MediaResponse {
    private Long mediaId;
    private String mediaUrl;

    public static List<MediaResponse> from(Record record){
        return record.getMedias().stream()
                .map(media -> MediaResponse.builder()
                        .mediaId(media.getId())
                        .mediaUrl(media.getMediaUrl())
                        .build()
                ).toList();
    }
}
