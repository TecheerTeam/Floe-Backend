package project.floe.domain.record.entity;

import java.util.List;
import lombok.Getter;

@Getter
public class Tags {
    private List<Tag> tags;

    public List<String> getTagNames() {
        return tags != null ? tags.stream()
                .map(Tag::getTagName)
                .toList() : List.of();
    }

    public Tags(List<Tag> tags) {
        this.tags = tags;
    }
}
