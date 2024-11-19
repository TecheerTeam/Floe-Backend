package project.floe.domain.record.dto.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.floe.domain.record.entity.RecordType;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRecordRequest {

        private String title;

        private String content;

        private RecordType recordType;

        private List<String> tags;

        private List<UpdateMediaRequest> medias;
}
