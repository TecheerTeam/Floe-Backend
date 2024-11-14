package project.floe.domain.record.entity;

import java.util.List;
import lombok.Getter;

@Getter
public class Tags {
    private List<Tag> tags;

    public List<Long> getTagIds() {
        return tags.stream()
                .map(Tag::getId)
                .toList();
    }

    public List<String> getTagNames(){
        return tags.stream()
                .map(Tag::getTagName)
                .toList();
    }

    public Tags(List<Tag> tags){
        this.tags = tags;
    }
}
