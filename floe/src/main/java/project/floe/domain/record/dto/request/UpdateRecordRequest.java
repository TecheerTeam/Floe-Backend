package project.floe.domain.record.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

        @NotBlank(message = "Record title cannot be blank")
        private String title;

        @NotBlank(message = "Record content cannot be blank")
        private String content;

        @NotNull(message = "RecordType cannot be empty")
        private RecordType recordType;

        private List<String> tags;

        private List<UpdateMediaRequest> medias;
}
