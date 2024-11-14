package project.floe.domain.record.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@Embeddable
@AllArgsConstructor
public class RecordTags {

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL)
    private List<RecordTag> value;

    public RecordTags(){
        this.value = new ArrayList<>();
    }

    public void clear(){
        value.clear();
    }

    public List<String> getTagNames(){
        return value.stream()
                .map(recordTag -> recordTag.getTag().getTagName())
                .toList();
    }

    public void add(Record record, Tags tags){
        tags.getTags()
                .forEach(tag -> value.add(new RecordTag(record, tag)));
    }

    public List<Long> getTagIds(){
        return value.stream()
                .map(recordTag -> recordTag.getTag().getId())
                .toList();
    }
}
